package net.frosty.fractals;

import java.lang.reflect.Array;
import java.util.ArrayList;
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
}
