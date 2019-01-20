package com.example.barte.projektpr;

import android.util.Log;

public class Klasa {
    public native static int klasyfikator(String xml, long mat2);
    static {
        try {
            System.loadLibrary("MyLibs");
            Log.d("native","zaladowano hello");
        } catch(UnsatisfiedLinkError e){
            e.printStackTrace();
        }
    }
}
