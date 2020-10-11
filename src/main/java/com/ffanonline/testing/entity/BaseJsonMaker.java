package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.utils.Common;

public abstract class BaseJsonMaker implements JsonMaker {

    private final String schemaPath;
    private final JsonNode schemaNode;
    private final JsonMold currentJsonMold;
    private final JsonMoldContext context;
    private final String fieldName;
    private final Boolean isRequired;

    public BaseJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold currentJsonMold, JsonMoldContext context, Boolean isRequired) {
        this.schemaPath = schemaPath;
        this.schemaNode = schemaNode;
        this.currentJsonMold = currentJsonMold;
        this.context = context;
        this.isRequired = isRequired;

        this.fieldName = Common.getFieldNameFromJsonPath(schemaPath);
    }

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
}
