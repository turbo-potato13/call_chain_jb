package com.company.callchain.function;

class Function {
    private final FunctionType type;
    private final String expression;

    public Function(FunctionType type, String expression) {
        this.type = type;
        this.expression = expression;
    }

    public FunctionType getType() {
        return type;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return this.type.getName() + "{" + expression + "}";
    }
}
