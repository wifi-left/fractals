package net.frosty.fractals;

import it.unimi.dsi.fastutil.Hash;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class LSystemHelper {

    public static String[] checkRule(String symbol, HashMap<String, String[]> rules){
        String[] rule = rules.get(symbol);
        if (rule==null){
            rule = new String[] {symbol};
        }
        return rule;
    }

    public static String[] UpdateSentence(String[] sentence, HashMap<String, String[]> rules){
        List<String> temp = new ArrayList<>();
        for (String symbol:sentence){
            String[] rule = checkRule(symbol,rules);
            for (String s:rule){
                temp.add(s);
            }
        }
        return temp.toArray(new String[0]);
    }

    public static String[] UpdateStochasticSentence(String[] sentence, HashMap<String, String[][]> rules){
        List<String> temp = new ArrayList<>();
        for (String symbol:sentence) {
            if (rules.containsKey(symbol)) {
                int randomIndex = (int)(Math.random() * rules.get(symbol).length);
                for (String s : rules.get(symbol)[randomIndex]) {
                    temp.add(s);
                }
            } else {
                temp.add(symbol);
            }
        }
        return temp.toArray(new String[0]);
    }
}
