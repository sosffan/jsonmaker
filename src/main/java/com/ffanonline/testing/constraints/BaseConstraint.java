package com.ffanonline.testing.constraints;

public class BaseConstraint {
    private final Boolean isNullable;
    private final Boolean isRequired;

    public BaseConstraint(Boolean isRequired, Boolean isNullable) {
        this.isRequired = isRequired;
        this.isNullable = isNullable;
    }
}
