package com.ffanonline.testing.constraints;

public class ArrayBaseConstraint extends BaseConstraint {
    private final int minItems;
    private final int maxItems;

    public ArrayBaseConstraint(int minItems, int maxItems, Boolean isRequiredField, Boolean isNullable) {
        super(isRequiredField, isNullable);
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
