package com.ffanonline.testing.constraints;

public class ArrayConstraint implements Constraint {
    private final int minItems;
    private final int maxItems;

    public ArrayConstraint(int minItems, int maxItems) {
        this.minItems = minItems;
        this.maxItems = maxItems;
    }

    public int getMinItems() {
        return minItems;
    }

    public int getMaxItems() {
        return maxItems;
    }
}
