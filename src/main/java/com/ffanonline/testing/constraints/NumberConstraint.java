package com.ffanonline.testing.constraints;

public class NumberConstraint implements Constraint {
    private final int minimum;
    private final int maximum;
    private final int multipleOf;

    public NumberConstraint(int minimum, int maximum, int multipleOf) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.multipleOf = multipleOf;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public int getMultipleOf() {
        return multipleOf;
    }
}
