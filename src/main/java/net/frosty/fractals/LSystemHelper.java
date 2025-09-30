package net.frosty.fractals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LSystemHelper {

    public static String[] checkRule(String symbol, HashMap<String, String[]> rules){
        String[] rule = rules.get(symbol);
        if (rule==null){
            rule = new String[] {symbol};
        }
        return rule;
    }

    public static String[] UpdateSentence(String[] sentence, HashMap<String, String[]> rules, boolean b){
        List<String> temp = new ArrayList<>();
        for (String symbol:sentence){
            if (rules.containsKey(symbol) && !(b && (symbol=="F" || symbol=="A"))) {
                String[] rule = rules.get(symbol);
                for (String s : rule) {
                    temp.add(s);
                }
            } else {
                temp.add(symbol);
            }
        }
        return temp.toArray(new String[0]);
    }

    public static String[] UpdateStochasticSentence(String[] sentence, HashMap<String, String[][]> rules, boolean b){
        List<String> temp = new ArrayList<>();
        for (String symbol:sentence) {
            if (rules.containsKey(symbol) && !(b && (symbol=="F" || symbol=="A"))) {
                int randomIndex = (int)(Math.random() * rules.get(symbol).length);
//                System.out.println(randomIndex +"/"+rules.get(symbol).length);
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
