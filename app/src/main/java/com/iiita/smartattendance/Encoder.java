package com.iiita.smartattendance;

import java.util.HashMap;
import java.util.Map;

public class Encoder{
    private Map <String, String> ourMapping = new HashMap<>() , ourMappingInv = new HashMap<>();

    Encoder(){
        String []unmapped = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "_", "$", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String []mapped = {"V", "_", "3", "7", "G", "I", "W", "E", "Z", "4", "K", "B", "H", "T", "J", "6", "1", "P", "U", "8", "S", "C", "O", "N", "M", "F", "5", "2", "R", "0", "D", "Y", "X", "A", "L", "Q", "$", "9"};
        for(int i = 0; i < mapped.length; ++i){
            ourMapping.put(unmapped[i], mapped[i]);
            ourMappingInv.put(mapped[i], unmapped[i]);
        }
    }

    // encrypting on student's side
    public String Encode(String toMod){
        String ret = "";
        toMod = toMod.toUpperCase();
        for(int i = 0; i < toMod.length(); ++i){
            ret = ret + ourMapping.get(Character.toString(toMod.charAt(i)));
        }
        return ret;
    }
    // decrypting on professor's side
    public String Decode(String toMod){
        String ret = "";
        toMod = toMod.toUpperCase();
        for(int i = 0; i < toMod.length(); ++i){
            ret = ret + ourMappingInv.get(Character.toString(toMod.charAt(i)));
        }
        return ret;
    }
}