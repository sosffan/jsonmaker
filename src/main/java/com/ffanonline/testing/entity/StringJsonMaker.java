package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.constraints.StringConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;
import com.ffanonline.testing.utils.Common;

import java.util.HashSet;
import java.util.Set;

public class StringJsonMaker extends BaseJsonMaker {

    StringConstraint constraint = null;
    String fieldName = null;


    public StringJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold parentSchema, JsonMoldContext context) {
        super(schemaPath, schemaNode, parentSchema, context);

        JsonNode patternNode = schemaNode.get(Keyword.PATTERN.getName());
        JsonNode maxLengthNode = schemaNode.get(Keyword.MAX_LENGTH.getName());
        JsonNode minLengthNode = schemaNode.get(Keyword.MIN_LENGTH.getName());
        JsonNode enumNode = schemaNode.get(Keyword.ENUM.getName());

        String pattern = patternNode == null ? null : patternNode.textValue();
        int maxLength = maxLengthNode == null ? -1 : maxLengthNode.intValue();
        int minLength = minLengthNode == null ? 0 : minLengthNode.intValue();

        Set<String> enumSet = new HashSet<>();
        if (enumNode == null) {
            enumSet = null;
        } else {
            for (JsonNode node : enumNode) {
                enumSet.add(node.textValue());
            }
        }

        constraint = new StringConstraint(minLength, maxLength, pattern, enumSet);
        fieldName = Common.getFieldNameFromJsonPath(schemaPath);
    }

    @Override
    public Object create(JsonDataCreator creator) {
        String value = creator.generateStringField(constraint, fieldName, getSchemaPath());
        return value;
    }
}
