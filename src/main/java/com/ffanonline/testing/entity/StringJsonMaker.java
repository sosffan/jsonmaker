package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.constraints.StringBaseConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;

import java.util.HashSet;
import java.util.Set;

public class StringJsonMaker extends BaseJsonMaker {
    StringBaseConstraint constraint;

    public StringJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold currentJsonMold, JsonMoldContext context) {
        super(schemaPath, schemaNode, currentJsonMold, context);

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

        constraint = new StringBaseConstraint(minLength, maxLength, pattern, enumSet, getRequired(), getNullable());
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

        String value = creator.generateStringField(constraint, getFieldName(), getSchemaPath());

        if (getFieldName() == null) {
            return TextNode.valueOf(value);
        } else {
            return getContext().getMapper().createObjectNode().put(getFieldName(), value);
        }
    }
}
