package com.company.callchain.function;

import com.company.callchain.exception.SyntaxErrorException;
import com.company.callchain.exception.TypeErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.company.callchain.function.FunctionType.FILTER;
import static com.company.callchain.function.FunctionType.MAP;

public class FunctionExecutor {

    private final String inputExpression;

    public FunctionExecutor(String inputExpression) {
        this.inputExpression = inputExpression;
    }

    public String execute() {
        final var stringFunctions = inputExpression.split("%>%");
        if (stringFunctions.length == 0) {
            return "";
        }
        return handleAllFunctions(stringFunctions).stream()
                .map(Function::toString)
                .collect(Collectors.joining("%>%"));
    }

    private List<Function> handleAllFunctions(String[] stringFunctions) {
        final var functions = readAllFunctions(stringFunctions);
        final var finalResult = new ArrayList<Function>();
        Function firstFunction = functions.get(0);
        Function firstMap = null;
        if (firstFunction.getType() == MAP && functions.size() > 1) {
            firstMap = firstFunction;
        } else {
            finalResult.add(firstFunction);
        }
        for (int i = 1; i < functions.size(); i++) {
            final var current = functions.get(i);
            final var prev = getLast(finalResult);
            final var filterCondition = prev.map(Function::getType)
                    .map(t -> t == FILTER && t == current.getType())
                    .orElse(false);
            if (filterCondition) {
                removeLast(finalResult);
                Function functionToAdd = new Function(FILTER, prev.get().getExpression() + "&" + current.getExpression());
                finalResult.add(handleFunction(functionToAdd, firstMap));
                continue;
            }
            finalResult.add(handleFunction(current, firstMap));
            if (current.getType() == MAP && firstMap == null) {
                firstMap = current;
            }
        }
        if (firstMap == null
                || (finalResult.size() == 2
                && finalResult.get(0).getType() == FILTER
                && finalResult.get(1).getType() == MAP)) {
            finalResult.add(new Function(MAP, "element"));
        }
        return finalResult;
    }

    private List<Function> readAllFunctions(String[] stringFunctions) {
        return Stream.of(stringFunctions)
                .map(this::readFunction)
                .collect(Collectors.toList());
    }

    private Function handleFunction(Function functionToAdd, Function firstMap) {
        if (firstMap == null) {
            return functionToAdd;
        }
        final var newExpression = functionToAdd.getExpression()
                .replaceAll("element", "(" + firstMap.getExpression() + ")");
        return new Function(functionToAdd.getType(), newExpression);
    }

    private Function readFunction(String checkingStr) {
        if (!checkingStr.contains("element")) {
            throw new SyntaxErrorException(checkingStr);
        }
        final var indexMap = checkingStr.indexOf("map");
        final var indexFilter = checkingStr.indexOf("filter");
        if (indexFilter > 0 || indexMap > 0) {
            throw new TypeErrorException();
        }
        final var patternFilter = Pattern.compile("filter\\Q{\\E\\Q(\\E*(element)*[\\Q+-*><=&|\\E0-9]+?(element)*\\Q)\\E*\\Q}\\E");
        final var patternMap = Pattern.compile("map\\Q{\\E\\Q(\\E*(element)*[\\Q+-*><=&|\\E0-9]+(element)*\\Q)\\E*\\Q}\\E");
        final var matcherFilter = patternFilter.matcher(checkingStr);
        final var matcherMap = patternMap.matcher(checkingStr);
        final var contentRegEx = "\\Q{\\E.+\\Q}\\E";
        if (matcherMap.matches()) {
            final var matcherEx = Pattern.compile(contentRegEx).matcher(checkingStr);
            if (matcherEx.find() ) {
                final var expression = checkingStr.substring(matcherEx.start() + 1, matcherEx.end() - 1);
                if (containsStupidBrackets(expression)) {
                    return new Function(
                            MAP,
                            expression.replace("(", "").replace(")", "")
                    );
                }
                return new Function(MAP, expression);
            }
            return new Function(MAP, "");
        }
        if (matcherFilter.matches()) {
            final var matcherEx = Pattern.compile(contentRegEx).matcher(checkingStr);
            if (matcherEx.find()) {
                final var expression = checkingStr.substring(matcherEx.start() + 1, matcherEx.end() - 1);
                return new Function(FILTER, expression);
            }
            return new Function(FILTER, "");
        }
        throw new SyntaxErrorException(checkingStr);
    }

    private boolean containsStupidBrackets(String expression) {
        return expression.startsWith("(")
        && expression.endsWith(")")
        && expression.lastIndexOf("(") == 0
        && expression.indexOf(")") == (expression.length() - 1);
    }

    private <T> Optional<T> getLast(List<T> list) {
        if (!list.isEmpty()) {
            return Optional.ofNullable(list.get(list.size() - 1));
        }
        return Optional.empty();
    }

    private <T> void removeLast(List<T> list) {
        if (!list.isEmpty()) {
            list.remove(list.size() - 1);
        }
    }
}
