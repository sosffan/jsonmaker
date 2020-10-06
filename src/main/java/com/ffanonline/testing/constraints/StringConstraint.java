package com.ffanonline.testing.constraints;

import java.util.Set;

public class StringConstraint implements Constraint {
    private final int minLength;
    private final int maxLength;
    private final String pattern;
    private final Set<String> enumSet;

    public StringConstraint(int minLength, int maxLength, String pattern, Set<String> enumSet) {
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
