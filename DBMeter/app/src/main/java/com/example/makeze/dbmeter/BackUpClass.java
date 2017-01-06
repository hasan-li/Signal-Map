package com.example.makeze.dbmeter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by maksudg on 06/01/2017.
 */

public class BackUpClass {
    List<String> array;
    FileWriter fw;
    BufferedWriter bw;
    public BackUpClass(List<String> array){
        this.array=array;
        backUp();
    }

    private void backUp(){
        try {
            this.fw = new FileWriter("/storage/emulated/0/DBMeter/backup.dat");
            this.bw = new BufferedWriter(fw);
            for (int i=0;i<array.size();i++){
                String line = array.get(i);
                bw.write(array.get(i)+"\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}