/*
 * Author: C M Khaled Saifullah
 * nsid: cms500
 * Assignment for CMPT470/816
 */
package extraction;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;


import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;


import config.Config;
import static util.ParseUtil.getFilteredRecursiveFiles;
import static util.ParseUtil.reformatFQN;
import model.APIMethod;
import model.CompileUnit;
import util.FileUtil;

public class MethodCallCollector{
    private static String[] jarPath,sourceFilePath; // variable for storing the paths of jar file and source file

    private static final Logger logger = LogManager.getLogger(MethodCallCollector.class.getName()); // logger variable for loggin in the file

    private static final DecimalFormat df = new DecimalFormat(); // Decimal formet variable for formating decimal into 2 digits

    /**
     * Used to define simpler print function
     * @param s: object of any type/class that will be displayed in console.
     */
    private static void print(Object s){System.out.println(s.toString());}

    /**
     * The Method takes the path of the repository and collect the source code using Eclipse JDT Core.
     * @param projectFile: Path of the repository
     * @return List of API elements
     */
    private static List<APIMethod> extractfromSource(File projectFile)
    {
        String[] sources = { projectFile.getAbsolutePath()};

        df.setMaximumFractionDigits(2);

        print("Colelcting Source code files....");
        collectJavaAndJarFiles(projectFile);

        print("Configuring Eclise JDT Parser....");
        ASTParser astParser = configEclipseJDTParser(sources);

        print("Collecting Compilation Units....");
        final List<CompileUnit> cus = collectCompilationUnits(astParser);

        print("Extracting types/methods/fields from each Compilation Unit");
        List<APIMethod> apiMethods = new ArrayList<>();
        int count = 0;

        for (final CompileUnit compileUnit : cus) {
            System.gc();
            apiMethods.addAll(parseSourceCode(compileUnit));
            count++;

            if(count%100 == 0){
                logger.info(count+" compilation units out of "+cus.size()+" are parsed. Percentage of completion: "+df.format((count*100/cus.size()))+"%");
            }
        }

        logger.info(count+" compilation units out of "+cus.size()+" are parsed. Percentage of completion: "+df.format((count*100/cus.size()))+"%");

        return apiMethods;

    }


    /**
     * The Method collects paths of all source files (.java) and all library files (.jar) for all projects in the repository
     * @param projectFile: path of the repository
     */
    private static void collectJavaAndJarFiles(File projectFile){
        logger.info("Colelcting Java and Jar Files");
        String[] extensions = { ".java", ".jar" };
        HashMap<String, List<File>> allJarJavaFiles = getFilteredRecursiveFiles(projectFile, extensions);

        logger.info("Colelcting Java Files");
        List<File> sourceCodeFiles = allJarJavaFiles.get(".java");
        if (sourceCodeFiles == null) {
            sourceCodeFiles = new ArrayList<>();
        }
        sourceFilePath = new String[sourceCodeFiles.size()];
        for (int i = 0; i < sourceCodeFiles.size(); i++) {
            sourceFilePath[i] = sourceCodeFiles.get(i).getAbsolutePath();
        }
        logger.info("Total Number of Java Files: "+ sourceFilePath.length);

        logger.info("Colelcting Jar Files");
        List<File> arrJars = allJarJavaFiles.get(".jar");
        if (arrJars == null) {
            arrJars = new ArrayList<>();
        }
        jarPath = new String[arrJars.size() + 1];
        jarPath[0] = System.getProperty("java.home") +File.separator+ "lib"+File.separator+"rt.jar";

        for (int i = 0; i < arrJars.size(); i++) {
            jarPath[i + 1] = arrJars.get(i).getAbsolutePath();
        }
        logger.info("Total Number of JAR Files: "+ jarPath.length);
    }

    /**
     * The Method configure the Eclipse JDT Parser for our data extraction work.
     * @param sources: Array of paths of all source files
     * @return The configured AST parser that will be used to parse each source file.
     */
    private static ASTParser configEclipseJDTParser(String[] sources){
        logger.info("Configure Eclipse JDT");

        @SuppressWarnings("rawtypes")
        Map options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setCompilerOptions(options);
        parser.setEnvironment(jarPath == null ? new String[0] : jarPath, sources, new String[]{"UTF-8"}, true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setStatementsRecovery(true);

        return parser;
    }

    /**
     * The method creates compilation units from each source file using AST parser. A compilation unit can be known as a class.
     * @param parser: The configured AST parser
     * @return A list of compilation unit.
     */
    private static List<CompileUnit> collectCompilationUnits(ASTParser parser){
        logger.info("Creating Compilation Unit for each java file");
        final List<CompileUnit> cus = new ArrayList<>();
        FileASTRequestor r = new FileASTRequestor() {
            @Override
            public void acceptAST(String sourceFilePath, CompilationUnit cu) {
                CompileUnit compileUnit = new CompileUnit(sourceFilePath,cu);
                cus.add(compileUnit);
            }
        };
        parser.createASTs(sourceFilePath, null, new String[0], r, null);

        logger.info("Total Number of Compilation Units: " + cus.size());

        return cus;
    }


    /**
     * The method takes each compilation unit, visits the AST tree and collect desired data
     * @param compileUnit: The object that has the path of the source file and the compilation unit
     * @return A list of API element
     */
    private static List<APIMethod> parseSourceCode(CompileUnit compileUnit)
    {
        CompilationUnit cu = compileUnit.getCompilationUnit();
        List<APIMethod> apiMethods = new ArrayList<>();

        cu.accept(new ASTVisitor() {

            //Visit all method body
            @Override
            public boolean visit(final MethodDeclaration methodDecNode) {
                final Block block = methodDecNode.getBody();
                if (block != null) {

                    block.accept(new ASTVisitor() {

                        //visit every method invocation
                        @Override
                        public boolean visit(MethodInvocation invocationnode) {
                            Expression expression = invocationnode.getExpression();
                            if (expression != null) {
                                ITypeBinding typeBinding = expression.resolveTypeBinding();
                                if (typeBinding != null && !typeBinding.isFromSource() && Config.isAllowedPackage(typeBinding.getPackage().getName())) {
                                    String apiMethod = invocationnode.getName().getIdentifier();
                                    String recieverVariable = expression.toString();
                                    int linenumber = cu.getLineNumber(invocationnode.getStartPosition());
                                    int methodbodyStartLineNumber = cu.getLineNumber(block.getStartPosition());
                                    if(linenumber<= methodbodyStartLineNumber){
                                        return true;
                                    }

                                    int noOfPrevLine = linenumber - methodbodyStartLineNumber;
                                    String FQN = reformatFQN(typeBinding.getQualifiedName());
                                    APIMethod methodInvocation = new APIMethod(apiMethod,compileUnit.getFilePath(),linenumber,FQN);
                                    getMethodContext(methodInvocation,block,recieverVariable,noOfPrevLine);
                                    apiMethods.add(methodInvocation);
                                    System.gc();
                                }
                            }
                            return true;
                        }

                    });
                }
                return true;
            }
        });
        return apiMethods;
    }


    /**
     * The method collect context for each of the method invocation.
     * @param apiMethod: The object of the method invocation node. We store all sort of data regarding the method invocation.
     * @param block: The whole block of Method's body.
     * @param recieverVaraible: the reciever variable
     * @param noOfPreviousLine: the integer value that contains how many previous lines are there in the body of the method
     */

    private static void getMethodContext(APIMethod apiMethod, Block block, String recieverVaraible, int noOfPreviousLine) {

        //1. Fully Qualified Name of Reciever Variable
        List<String> context = new ArrayList<>(Collections.singletonList(apiMethod.getFQN()));

        try{
            String apiStatement = recieverVaraible+"."+apiMethod.getName()+"(";
            List<String> prevCode = collectPreviousCode(block,noOfPreviousLine);
            Collections.reverse(prevCode);
            prevCode = filterPreviousCode(prevCode,apiStatement);
            String inLineCode = prevCode.get(0);

            //2. Methods invoked on reciever Variable
            context.addAll(colelctPreviousMethod(prevCode,recieverVaraible));

            //3. Tokens in the line of method invocation
            context.addAll(collectContext(inLineCode.substring(0,inLineCode.indexOf(apiStatement))));

            //4. Tokens in the previous four lines
            if(prevCode.size() > 1){
                prevCode = prevCode.subList(1,prevCode.size());
                context.addAll(collectTokeninPrevfourLines(prevCode));
            }
        }catch (Exception ignored){}

        apiMethod.setContext(context);
    }

    /**
     * Collect only the previous code of the method invocation
     * @param block: The whole body of the method
     * @param noOfPreviousLine: Number of previous line
     * @return List of string where each string is one previous statement
     */
    private static List<String> collectPreviousCode(Block block, int noOfPreviousLine){
        List<String>code = new ArrayList<>();
        int lineCount = 0;
        for (int i = 0; i<block.statements().size();i++) {
            String eachStmt = block.statements().get(i).toString();
            String[] token = eachStmt.split("\n");
            for(String eachLine:token){
                lineCount++;
                code.add(eachLine.replace(";",""));
                if(lineCount == noOfPreviousLine)
                    break;
            }
            if(lineCount == noOfPreviousLine)
                break;
        }
        return code;
    }

    /**
     * The method filter if unexpected statement is added in the previous code list
     * @param prevcode: list of previous code statement
     * @param apiStatement: the method invocation statement
     * @return the filtered list of code statements
     */
    private static List<String> filterPreviousCode(List<String> prevcode, String apiStatement){
        List<String> finalPrevCode = new ArrayList<>();
        int flag = 0;
        for(String eachLine:prevcode){
            if(eachLine.contains(apiStatement)){
                flag = 1;
                finalPrevCode.add(eachLine);
            }
            else if(flag == 1)
                finalPrevCode.add(eachLine);
        }
        return finalPrevCode;
    }

    /**
     * Colelct the code tokens of the precious four statements
     * @param prevCode: list of previous code statement
     * @return list of tokens of these precious code statements
     */
    private static List<String> collectTokeninPrevfourLines(List<String> prevCode){
        List<String> tokenPrev = new ArrayList<>();
        for(int i = 0; i<prevCode.size();i++){
            if(i>=4)
                break;
            tokenPrev.addAll(collectContext(prevCode.get(i)));
        }
        return tokenPrev;
    }

    /**
     * Mehtods colelcts all methods previously invocked on the reciever variable
     * @param prevcode: list of previous code statements
     * @param recievervariable: the reciever variable
     * @return the list of methods invoked on the reciever varaible
     */
    private static List<String> colelctPreviousMethod(List<String> prevcode, String recievervariable) {
        List<String> prevMethodStream = new ArrayList<>();
        for(String eachLine:prevcode){
            if(eachLine.contains(recievervariable+".")){
                try {
                    eachLine = eachLine.substring(eachLine.indexOf(recievervariable+"."));
                    eachLine = eachLine.replace(recievervariable,"");
                    String methodName = eachLine.substring(eachLine.indexOf(".")+1,eachLine.indexOf("("));
                    prevMethodStream.add(methodName);
                }catch (Exception ignored){}

            }
        }
        return prevMethodStream;
    }

    /**
     * The method that collect the context from a line of code
     * @param line: the string representing the line of the code
     * @return the list of code token that we will consider as the context
     */
    private static List<String> collectContext(String line)
    {
        List<String> tokenStream = new ArrayList<>();
        try{
            String[] tokens = line.split(" ");
            for(String eachToken:tokens){
                if(eachToken.trim().equals(""))
                    continue;
                if(Config.JAVA_KEYWORDS.contains(eachToken))
                    tokenStream.add(eachToken);
                else if(eachToken.charAt(0) == Character.toUpperCase(eachToken.charAt(0))){
                    if(eachToken.contains("("))
                        tokenStream.add(eachToken.substring(0, eachToken.indexOf("(")));
                    else
                        tokenStream.add(eachToken);
                }
                else if(eachToken.contains(".") && eachToken.contains("("))
                    tokenStream.add(eachToken.substring(eachToken.indexOf(".")+1, eachToken.indexOf("(")));

            }
            Collections.reverse(tokenStream);
        }catch (Exception ignored){}
        return tokenStream;
    }




    public static void main(String[] args) {
        System.setProperty("logfilename",Config.LOG_PATH);
        PropertyConfigurator.configure("src/log4j.properties");

        List<APIMethod> apiMethods = MethodCallCollector.extractfromSource(new File(Config.REPOSITORY_PATH));
        logger.info("Total Number of API method invocation found: " + apiMethods.size());
        Collections.shuffle(apiMethods);

        for(int i=1;i<=10;i++)
            FileUtil.writeToFile(Config.DATSET_PATH+"fold-"+i+".csv",Config.DATASET_HEAD);

        logger.info("Dividing the dataset into 10 folds and writting in " + Config.DATSET_PATH);
        int fileNumber = 1;
        for(APIMethod apiMethod:apiMethods) {
            String line = apiMethod.getFileName()+","+apiMethod.getLineNumber()+","+apiMethod.getFQN()+","+apiMethod.getContextInString()+","+apiMethod.getName();
            FileUtil.appendLineToFile(Config.DATSET_PATH+"fold-"+fileNumber+".csv",line);

            if(fileNumber == 10)
                fileNumber  = 1;
            else
                fileNumber++;
        }



    }

}
