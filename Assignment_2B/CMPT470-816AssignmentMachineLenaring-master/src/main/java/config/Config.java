/*
 * Author: C M Khaled Saifullah
 * nsid: cms500
 * Assignment for CMPT470/816
 */

package config;

import java.util.ArrayList;
import java.util.Arrays;

public class Config {
    private static final String ROOT_PATH = "./";
    public static final String REPOSITORY_PATH = ROOT_PATH+"data/repository/";
    public static final String DATSET_PATH = ROOT_PATH+"data/dataset/";
    public static final String LOG_PATH = ROOT_PATH+"jaw845_codeparser.log";


    private static final String[] PACKAGE_NAME ={"java"};
    public static final String DATASET_HEAD ="FilePath,LineNumber,RecieverVaribale,Context,Label\n";

    public static final ArrayList<String> JAVA_KEYWORDS = new ArrayList<>(Arrays.asList("abstract","boolean","break","byte",
            "case","catch","char","class","const","continue", "default","do","double","else","extends","final","finally","float",
            "for","goto","if","implements","import","instanceof","int","interface","long","native","new","null","package","private",
            "protected","public","return","short","static","super","switch","synchronized","this","throw","throws","transient","try",
            "void","volatile","while","assert","enum","strictfp","null","true","false","=","<","<=",">",">=","=="));



    //Deep Learning related parameter
    public static final int LSTM_LAYER_SIZE = 128;					                        //Number of units in each GravesLSTM layer
    public static final int BATCH_SIZE = 32;						                        //Size of mini batch to use when  training
    public static final int NO_EPOCHES = 10;							                    //Total number of training epoch
    public static final int MAX_LENGTH_INPUT = 10;							                //Maximum length in input
    public static final String DL4J_LOG_PATH = ROOT_PATH+"cms500_deeplearning4j_lstm.log";  //Log file path









    public static final boolean isAllowedPackage(String packagename) {
        if(packagename == null)
                return false;

        for(String each_package: PACKAGE_NAME)
            if (packagename.trim().startsWith(each_package))
                return true;

        return false;
    }
}
