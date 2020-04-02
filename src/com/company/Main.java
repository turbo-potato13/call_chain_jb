package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.company.FunctionType.FILTER;
import static com.company.FunctionType.MAP;

public class Main {

    public static void executor(String validStr) {
        String[] stringFunctions = validStr.split("%>%");
        if (stringFunctions.length == 0) {
            return;
        }
        List<Function> functions = Stream.of(stringFunctions)
                .map(Main::readFunction)
                .collect(Collectors.toList());
        List<Function> finalResult = new ArrayList<>();
        Function firstFunction = functions.get(0);
        Function firstMap = null;
        if (firstFunction.getType() == MAP && functions.size() > 1) {
             firstMap = firstFunction;
        } else {
            finalResult.add(firstFunction);
        }

        for (int i = 1; i < functions.size(); i++) {
            Function current = functions.get(i);
            Optional<Function> prev = getLast(finalResult);
            if (prev.map(Function::getType).map(t -> t == FILTER && t == current.getType()).orElse(false)) {
                removeLast(finalResult);
                Function functionToAdd = new Function(FILTER, prev.get().getExpression() + "&" + current.getExpression());
                finalResult.add(handleFunction(functionToAdd, firstMap));
                continue;
            }
            if (current.getType() == MAP && firstMap == null) {
                firstMap = current;
            }
            finalResult.add(handleFunction(current, firstMap));
        }
        if (firstMap == null) {
            finalResult.add(new Function(MAP, "element"));
        }
        String stringResult = finalResult.stream().map(Function::toString).collect(Collectors.joining("%>%"));
        System.out.println(stringResult);
    }

    private static Function handleFunction(Function functionToAdd, Function firstMap) {
        if (firstMap == null) {
            return functionToAdd;
        }
        final var newExpression = functionToAdd.getExpression()
                .replaceAll("element", "(" + firstMap.getExpression() + ")");
        return new Function(functionToAdd.getType(), newExpression);
    }

    public static Function readFunction(String checkedStr) {
        final var indexMap = checkedStr.indexOf("map");
        final var indexFilter = checkedStr.indexOf("filter");
        if (indexFilter > 0 || indexMap > 0) {
            throw new TypeErrorException();
        }
        final var patternFilter = Pattern.compile("filter\\Q{(\\Eelement[\\Q+-*><=&|\\E0-9]+?\\Q)}\\E");
        final var patternMap = Pattern.compile("map\\Q{(\\Eelement[\\Q+-*><=&|\\E0-9element]+?\\Q)}\\E");
        final var matcherFilter = patternFilter.matcher(checkedStr);
        final var matcherMap = patternMap.matcher(checkedStr);
        final var contentRegEx = "\\Q(\\E.+\\Q)\\E";
        if (matcherMap.matches()) {
            final var matcherEx = Pattern.compile(contentRegEx).matcher(checkedStr);
            if (matcherEx.find()) {
                final var expression = checkedStr.substring(matcherEx.start(), matcherEx.end());
                return new Function(MAP, expression);
            }
            return new Function(MAP, "");
        }
        if (matcherFilter.matches()) {
            final var matcherEx = Pattern.compile(contentRegEx).matcher(checkedStr);
            if (matcherEx.find()) {
                final var expression = checkedStr.substring(matcherEx.start(), matcherEx.end());
                return new Function(FILTER, expression);
            }
            return new Function(FILTER, "");
        }
        throw new SyntaxErrorException("SYNTAX_ERROR");
    }

    private static <T> Optional<T> getLast(List<T> list) {
        if (!list.isEmpty()) {
            return Optional.ofNullable(list.get(list.size() - 1));
        }
        return Optional.empty();
    }

    private static <T> void removeLast(List<T> list) {
        if (!list.isEmpty()) {
            list.remove(list.size() - 1);
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("IN");
        String str = scan.nextLine();
        String[] strMas = str.split("%>%");
        //for (String i : strMas) {
        //  Function a = readFunction(i);
        //System.out.println(i);
        //   }
        executor(str);
    }
}
