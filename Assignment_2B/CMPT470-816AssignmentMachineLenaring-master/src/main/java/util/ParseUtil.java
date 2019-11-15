/*
 * Author: C M Khaled Saifullah
 * nsid: cms500
 * Assignment for CMPT470/816
 */

package util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParseUtil {


    public static HashMap<String,List<File>> getFilteredRecursiveFiles(File parentDir, String [] sourceFileExt)
    {
        HashMap<String,List<File>> recursiveFiles = new HashMap<>();

        File[] childFiles = parentDir.listFiles();
        if (childFiles==null)
            return recursiveFiles;
        for (File file:childFiles)
        {
            if (file.isFile())
            {
                String ext=isPassFileReturn(file, sourceFileExt);
                if (!ext.isEmpty())
                {
                    //	print(ext);
                    if(!recursiveFiles.containsKey(ext)){
                        List<File> lstFiles=new ArrayList<>();
                        lstFiles.add(file);
                        recursiveFiles.put(ext,lstFiles);
                    }else{
                        recursiveFiles.get(ext).add(file);
                    }

                }
            }
            else
            {
                HashMap<String,List<File>> subList = getFilteredRecursiveFiles(file, sourceFileExt);
                for(String strKey:subList.keySet()){
                    List<File> lstSubListFile=subList.get(strKey);
                    if(lstSubListFile.size()>0){
                        if(!recursiveFiles.containsKey(strKey)){
//							List<File> lstFiles=new ArrayList<File>();
//							lstFiles.addAll(lstSubListFile);
                            recursiveFiles.put(strKey, lstSubListFile);
                        } else{
                            recursiveFiles.get(strKey).addAll(lstSubListFile);
                        }
                    }

                }

            }
        }
        return recursiveFiles;
    }

    private static String isPassFileReturn(File file, String [] sourceFileExt)
    {
        String name = file.getName();
        for (String fileExt:sourceFileExt)
        {
            if (name.endsWith(fileExt))
            {
                return fileExt;
            }
        }
        return "";
    }


    public static String reformatFQN(String actualFQN){
        if (actualFQN.contains("<"))
            actualFQN = actualFQN.substring(0, actualFQN.indexOf("<"));
        return actualFQN;
    }
}
