package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.constraints.NumberBaseConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;

public class NumberJsonGenerator extends BaseJsonGenerator {
    NumberBaseConstraint constraint;

    public NumberJsonGenerator(String schemaPath, JsonNode schemaNode, JsonMold currentJsonMold, JsonMoldContext context) {
        super(schemaPath, schemaNode, currentJsonMold, context);

        JsonNode minimumNode = schemaNode.get(Keyword.MINIMUM.getName());
        JsonNode maximumNode = schemaNode.get(Keyword.MAXIMUM.getName());
        JsonNode multipleOfNode = schemaNode.get(Keyword.MULTIPLE_OF.getName());

        int minimum = minimumNode == null ? 0 : minimumNode.intValue();
        int maximum = maximumNode == null ? -1 : maximumNode.intValue();
        int multipleOf = multipleOfNode == null ? -1 : multipleOfNode.intValue();

        constraint = new NumberBaseConstraint(minimum, maximum, multipleOf, getRequired(), getNullable());
    }

    @Override
    public JsonNode create(JsonDataCreator creator) throws Exception {

        Double value = creator.generateNumberField(constraint, getFieldName(), getSchemaPath());

        if (getFieldName() == null) {
            return DoubleNode.valueOf(value);
        } else {
            return getContext().getMapper().createObjectNode().put(getFieldName(), value);
        }
    }
}
