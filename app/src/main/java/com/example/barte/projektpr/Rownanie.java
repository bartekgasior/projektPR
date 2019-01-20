package com.example.barte.projektpr;

import com.udojava.evalex.Expression;

import java.math.BigDecimal;

public class Rownanie {
    //BigDecimal result = null;
    String wyr = "";
    public static BigDecimal rown(String str){
        BigDecimal wynik = null;
        //Expression exp = new Expression(str);
        wynik = new Expression(str).eval();
        return wynik;
    }
}