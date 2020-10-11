package com.ffanonline.testing.creator;

import com.ffanonline.testing.constraints.BaseConstraint;
import com.ffanonline.testing.constraints.NumberBaseConstraint;
import com.ffanonline.testing.constraints.StringBaseConstraint;

public interface JsonDataCreator {

    String generateStringField(StringBaseConstraint constraint, String fieldName, String fieldPath);

    Boolean generateBooleanField(BaseConstraint constraint, String fieldName, String fieldPath);

    Double generateNumberField(NumberBaseConstraint constraint, String fieldName, String fieldPath);

    Long generateIntegerField(NumberBaseConstraint constraint, String fieldName, String fieldPath);
}
