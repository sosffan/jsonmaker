package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.ffanonline.testing.JsonSchemaModel;
import com.ffanonline.testing.JsonSchemaModelContext;
import com.ffanonline.testing.constraints.BaseConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;

public class BooleanJsonGenerator extends BaseJsonGenerator {
    BaseConstraint constraint;

    public BooleanJsonGenerator(String schemaPath, JsonNode schemaNode, JsonSchemaModel currentJsonSchemaModel, JsonSchemaModelContext context) {
        super(schemaPath, schemaNode, currentJsonSchemaModel, context);

        constraint = new BaseConstraint(getRequired(), getNullable());
    }

    @Override
    public JsonNode create(JsonDataCreator creator, JsonNode originalValue) {

        Boolean value = creator.generateBooleanField(constraint, getFieldName(), getSchemaPath(), originalValue==null?null:originalValue.asBoolean() );


        if (getFieldName() == null) {
            return BooleanNode.valueOf(value);
        } else {
            return getContext().getMapper().createObjectNode().put(getFieldName(), value);
        }
    }
}
