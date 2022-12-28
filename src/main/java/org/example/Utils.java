package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {

    public static void main(String[] args) {
        TestCaseGenerator(8,"D:\\1javawork\\software_analysis_projs\\SATsolver\\src\\main\\resources\\Test");
    }
    public static void TestCaseGenerator(int n,String dir){
        String fileName="nby_"+n+".cnf";
        String filePath=dir+ File.separator+fileName;
        File file=new File(filePath);
        try {
            FileWriter fileWriter=new FileWriter(file);

            long bound=(long)Math.pow(2,n);
            fileWriter.write("p cnf "+n+" "+bound+"\n");
            for(long i=0;i<bound;i++){
                //bit operation
                for(int j=1;j<=n;j++){
                    int bit = (int)((i >> j-1) & 1);
                    if(bit==1){
                        fileWriter.write(j+"");
                    }else {
                        fileWriter.write((-j)+"");
                    }
                    fileWriter.write(" ");
                }
                fileWriter.write(0+"\n");
            }
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static int getPositive(int i){
        return i>0?i:-i;
    }
}
