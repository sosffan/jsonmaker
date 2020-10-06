package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.constraints.NumberConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;
import com.ffanonline.testing.utils.Common;

public class IntegerJsonMaker extends BaseJsonMaker {
    NumberConstraint constraint = null;
    String fieldName;

    public IntegerJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold parentSchema, JsonMoldContext context) {
        super(schemaPath, schemaNode, parentSchema, context);

        JsonNode minimumNode = schemaNode.get(Keyword.MINIMUM.getName());
        JsonNode maximumNode = schemaNode.get(Keyword.MAXIMUM.getName());
        JsonNode multipleOfNode = schemaNode.get(Keyword.MULTIPLE_OF.getName());

        int minimum = minimumNode == null ? 0 : minimumNode.intValue();
        int maximum = maximumNode == null ? -1 : maximumNode.intValue();
        int multipleOf = multipleOfNode == null ? -1 : multipleOfNode.intValue();

        constraint = new NumberConstraint(minimum, maximum, multipleOf);
        fieldName = Common.getFieldNameFromJsonPath(schemaPath);
    }

    @Override
    public Object create(JsonDataCreator creator) {

        Long value = creator.generateIntegerField(constraint, fieldName, getSchemaPath());
        return value;
    }
}
