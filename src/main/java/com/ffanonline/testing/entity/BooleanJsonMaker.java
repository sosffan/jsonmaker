package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.constraints.BaseConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;

public class BooleanJsonMaker extends BaseJsonMaker {
    BaseConstraint constraint;

    public BooleanJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold currentJsonMold, JsonMoldContext context) {
        super(schemaPath, schemaNode, currentJsonMold, context);

        constraint = new BaseConstraint(getRequired(), getNullable());
    }

    @Override
    public JsonNode create(JsonDataCreator creator, int type, String jsonPath) throws Exception {
        if (jsonPath != null && jsonPath.equals(getSchemaPath())) {
            getContext().markAsTraversed(jsonPath);

            switch (type) {
                case 1:
                    if (!getRequired()) return null;
                    break;
                case 2:
                    if (getNullable()) return generateNullNode();
                    break;
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
