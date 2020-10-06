package com.ffanonline.testing.creator;

import com.ffanonline.testing.constraints.NumberConstraint;
import com.ffanonline.testing.constraints.StringConstraint;

public interface JsonDataCreator {

    String generateStringField(StringConstraint constraint, String fieldName, String fieldPath);

    Boolean generateBooleanField(String fieldName, String fieldPath);

    Double generateNumberField(NumberConstraint constraint, String fieldName, String fieldPath);

    Long generateIntegerField(NumberConstraint constraint, String fieldName, String fieldPath);
}
