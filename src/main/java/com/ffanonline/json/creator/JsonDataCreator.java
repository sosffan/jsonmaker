package com.ffanonline.json.creator;

import com.ffanonline.json.constraint.BaseConstraint;
import com.ffanonline.json.constraint.NumberBaseConstraint;
import com.ffanonline.json.constraint.StringBaseConstraint;

public interface JsonDataCreator {

    String generateStringField(StringBaseConstraint constraint, String fieldName, String fieldPath, String originalValue);

    Boolean generateBooleanField(BaseConstraint constraint, String fieldName, String fieldPath, Boolean originalValue);

    Double generateNumberField(NumberBaseConstraint constraint, String fieldName, String fieldPath, Double originalValue);

    Long generateIntegerField(NumberBaseConstraint constraint, String fieldName, String fieldPath, Integer originalValue);
}
