package com.shivam151990.patterns.interpreter;

public class SquareExpression implements Expression<Integer>{

    private Integer item;

    public SquareExpression(Integer item) {
        this.item = item;
    }

    @Override
    public Integer interpret() {
        return item * item;
    }
}
