package com.ffanonline.json.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.ffanonline.json.JsonSchemaModel;
import com.ffanonline.json.JsonSchemaModelContext;
import com.ffanonline.json.Keyword;
import com.ffanonline.json.constraint.NumberBaseConstraint;
import com.ffanonline.json.creator.JsonDataCreator;

public class IntegerJsonGenerator extends BaseJsonGenerator {
    NumberBaseConstraint constraint;

    public IntegerJsonGenerator(String schemaPath, JsonNode schemaNode, JsonSchemaModel currentJsonSchemaModel, JsonSchemaModelContext context) {
        super(schemaPath, schemaNode, currentJsonSchemaModel, context);

        JsonNode minimumNode = schemaNode.get(Keyword.MINIMUM.getName());
        JsonNode maximumNode = schemaNode.get(Keyword.MAXIMUM.getName());
        JsonNode multipleOfNode = schemaNode.get(Keyword.MULTIPLE_OF.getName());

        int minimum = minimumNode == null ? 0 : minimumNode.intValue();
        int maximum = maximumNode == null ? -1 : maximumNode.intValue();
        int multipleOf = multipleOfNode == null ? -1 : multipleOfNode.intValue();

        constraint = new NumberBaseConstraint(minimum, maximum, multipleOf, getRequired(), getNullable());
    }

    @Override
    public JsonNode create(JsonDataCreator creator, JsonNode originalValue) throws Exception {

        Long value = creator.generateIntegerField(constraint, getFieldName(), getSchemaPath(), originalValue==null?null:originalValue.asInt() );

        if (getFieldName() == null) {
            return LongNode.valueOf(value);
        } else {
            return getContext().getMapper().createObjectNode().put(getFieldName(), value);
        }
    }
}
