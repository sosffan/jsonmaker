package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.creator.JsonDataCreator;
import com.ffanonline.testing.utils.Common;

public class BooleanJsonMaker extends BaseJsonMaker {
    String fieldName;
    public BooleanJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold parentSchema, JsonMoldContext context) {
        super(schemaPath, schemaNode, parentSchema, context);

        fieldName = Common.getFieldNameFromJsonPath(schemaPath);
    }

    @Override
    public Object create(JsonDataCreator creator) {
        Boolean value = creator.generateBooleanField(fieldName, getSchemaPath());
        return value;
    }
}
