package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.constraints.BaseConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;

public class BooleanJsonGenerator extends BaseJsonGenerator {
    BaseConstraint constraint;

    public BooleanJsonGenerator(String schemaPath, JsonNode schemaNode, JsonMold currentJsonMold, JsonMoldContext context) {
        super(schemaPath, schemaNode, currentJsonMold, context);

        constraint = new BaseConstraint(getRequired(), getNullable());
    }

    @Override
    public JsonNode create(JsonDataCreator creator, int operationType, String jsonPath) throws Exception {
        if (jsonPath != null && jsonPath.equals(getSchemaPath())) {
            try {
                return handleOperationType(operationType, jsonPath);
            } catch (Exception ignored) {
                System.out.println("Not matched field.");
            }
        }

        Boolean value = creator.generateBooleanField(constraint, getFieldName(), getSchemaPath());


        if (getFieldName() == null) {
            return BooleanNode.valueOf(value);
        } else {
            return getContext().getMapper().createObjectNode().put(getFieldName(), value);
        }
    }
}
