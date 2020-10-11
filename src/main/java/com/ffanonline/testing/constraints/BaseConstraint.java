package com.ffanonline.testing.constraints;

public class BaseConstraint {
    Boolean isRequired = false;

    public BaseConstraint(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Boolean getRequiredField() {
        return isRequired;
    }

    public void setRequiredField(Boolean isRequired) {
        this.isRequired = isRequired;
    }
}
