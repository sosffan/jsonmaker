package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.creator.JsonDataCreator;
import com.ffanonline.testing.utils.Common;

public abstract class BaseJsonMaker {

    private final String schemaPath;
    private final JsonNode schemaNode;
    private final JsonMold currentJsonMold;
    private final JsonMoldContext context;
    private final String fieldName;

    private final Boolean isRequired;
    private final Boolean isNullable;

    public BaseJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold currentJsonMold, JsonMoldContext context) {
        this.schemaPath = schemaPath;
        this.schemaNode = schemaNode;
        this.currentJsonMold = currentJsonMold;
        this.context = context;

        this.isRequired = context.getFieldInfo(schemaPath).getRequired();
        this.isNullable = context.getFieldInfo(schemaPath).getNullable();

        this.fieldName = Common.getFieldNameFromJsonPath(schemaPath);
    }


    public abstract JsonNode create(JsonDataCreator creator) throws Exception;

    public String getSchemaPath() {
        return schemaPath;
    }

    public JsonNode getSchemaNode() {
        return schemaNode;
    }

    public JsonMold getCurrentJsonMold() {
        return currentJsonMold;
    }

    public JsonMoldContext getContext() {
        return context;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public Boolean getNullable() {
        return isNullable;
    }
}
