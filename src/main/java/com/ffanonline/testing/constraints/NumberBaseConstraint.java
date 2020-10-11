package com.ffanonline.testing.constraints;

public class NumberBaseConstraint extends BaseConstraint {
    private final int minimum;
    private final int maximum;
    private final int multipleOf;

    public NumberBaseConstraint(int minimum, int maximum, int multipleOf, Boolean isRequired) {
        super(isRequired);
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
