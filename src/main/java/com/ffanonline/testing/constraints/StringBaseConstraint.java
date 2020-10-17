package com.ffanonline.testing.constraints;

import java.util.Set;

public class StringBaseConstraint extends BaseConstraint {
    private final int minLength;
    private final int maxLength;
    private final String pattern;
    private final Set<String> enumSet;

    public StringBaseConstraint(int minLength, int maxLength, String pattern, Set<String> enumSet,
                                Boolean isRequired, Boolean isNullable) {
        super(isRequired, isNullable);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.pattern = pattern;
        this.enumSet = enumSet;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getPattern() {
        return pattern;
    }

    public Set<String> getEnumSet() {
        return enumSet;
    }
}
