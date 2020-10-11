package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.constraints.NumberBaseConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;

public class IntegerJsonMaker extends BaseJsonMaker {
    NumberBaseConstraint constraint = null;

    public IntegerJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold currentJsonMold, JsonMoldContext context, Boolean isRequired) {
        super(schemaPath, schemaNode, currentJsonMold, context, isRequired);

        JsonNode minimumNode = schemaNode.get(Keyword.MINIMUM.getName());
        JsonNode maximumNode = schemaNode.get(Keyword.MAXIMUM.getName());
        JsonNode multipleOfNode = schemaNode.get(Keyword.MULTIPLE_OF.getName());

        int minimum = minimumNode == null ? 0 : minimumNode.intValue();
        int maximum = maximumNode == null ? -1 : maximumNode.intValue();
        int multipleOf = multipleOfNode == null ? -1 : multipleOfNode.intValue();

        constraint = new NumberBaseConstraint(minimum, maximum, multipleOf, isRequired);
    }

    @Override
    public JsonNode create(JsonDataCreator creator) {

        Long value = creator.generateIntegerField(constraint, getFieldName(), getSchemaPath());
        if (getFieldName() == null) {
            return LongNode.valueOf(value);
        } else {
            return getContext().getMapper().createObjectNode().put(getFieldName(), value);
        }
    }
}
