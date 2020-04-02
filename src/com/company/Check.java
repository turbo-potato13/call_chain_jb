package com.company;

import java.util.ArrayList;

public class Check {

    public static boolean ftBrackets(String str) {
        int j = 0;

        ArrayList<String> stack = new
                ArrayList<String>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(' || str.charAt(i) == '{' || str.charAt(i) == '[') {
                stack.add(String.valueOf(str.charAt(i)));
                j++;
            }
            if (str.charAt(i) == ')' || str.charAt(i) == '}' || str.charAt(i) == ']')
                if ((stack.get(j--) != "[" && str.charAt(i) == ']') || (stack.get(j--) != "{" && str.charAt(i) == '}') || (stack.get(j--) != "(" && str.charAt(i) == ')')) {
                    return false;
                }
        }
        if (j == 0) {
            return true;
        }
        else {
            return false;
        }
    }
}
