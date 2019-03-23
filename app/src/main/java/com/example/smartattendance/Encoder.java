package com.example.smartattendance;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Encoder{
    private int ctAscii , ctOur;
    private Map<Integer, Integer> asciiMapping = new HashMap<>() , asciiMappingInv = new HashMap<>();
    private Map<Integer, Integer> ourMapping = new HashMap<>() , ourMappingInv = new HashMap<>();
    Encoder(){
        int ct = 0 , ct1 = 0;
        // if you want to allow more characters in encryption, do it here
        for(int i=33;i<=255;i++){
            // range of valid characters that get printed in JAVA , apne hisaab se change karle
            if((i > 32 && i < 127) || (i > 160 && i < 256)){
                asciiMapping.put(i, ct);
                asciiMappingInv.put(ct, i);
                ct++;
            }
            //characters we are using
            if((i > 47 && i < 58) || (i > 64 && i < 91) || i == 36 || i == 95){
                ourMapping.put(i, ct1);
                //System.out.println(ourMapping.get(i));
                ourMappingInv.put(ct1, i);
                ct1++;
            }
        }
        //System.out.print(ct + "  " + ct1);
        this.ctAscii = ct;
        this.ctOur = ct1;
    }

    // encrypting on student's side
    public String Encode(String toMod){
        BigInteger bg1 = BigInteger.ZERO , bgtemp = BigInteger.ONE;
        String ret = "";
        //converts to uppercase , better to reduce the number of characters
        toMod = toMod.toUpperCase();
        for(int i=0;i<toMod.length();i++){
            int var = (int)toMod.charAt(i);
            var = ourMapping.get(var);
            bg1 = bg1.add(bgtemp.multiply(BigInteger.valueOf(var)));
            bgtemp = bgtemp.multiply(BigInteger.valueOf(ctOur));
        }
        while(bg1.compareTo(BigInteger.ZERO) > 0){
            BigInteger tmp1 = bg1.mod(BigInteger.valueOf(ctAscii));
            int inc = asciiMappingInv.get(tmp1.intValue());
            ret = ret + (char)inc;
            bg1 = bg1.divide(BigInteger.valueOf(ctAscii));
        }
        return ret;
    }
    // decrypting on professor's side
    public String Decode(String toMod){
        BigInteger bg1 = BigInteger.ZERO , bgtemp = BigInteger.ONE;
        String ret = "";
        for(int i=0;i<toMod.length();i++){
            int var = (int)toMod.charAt(i);
            var = asciiMapping.get(var);
            bg1 = bg1.add(bgtemp.multiply(BigInteger.valueOf(var)));
            bgtemp = bgtemp.multiply(BigInteger.valueOf(ctAscii));
        }
        while(bg1.compareTo(BigInteger.ZERO) > 0){
            BigInteger tmp1 = bg1.mod(BigInteger.valueOf(ctOur));
            int inc = ourMappingInv.get(tmp1.intValue());
            ret = ret + (char)inc;
            bg1 = bg1.divide(BigInteger.valueOf(ctOur));
        }
        return ret;
    }
}