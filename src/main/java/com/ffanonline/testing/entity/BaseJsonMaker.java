package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.utils.Common;

public abstract class BaseJsonMaker implements JsonMaker {

    private final String schemaPath;
    private final JsonNode schemaNode;
    private final JsonMold parentSchema;
    private final JsonMoldContext context;
    private final String schemaName;

    public BaseJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold parentSchema, JsonMoldContext context) {
        this.schemaPath = schemaPath;
        this.schemaNode = schemaNode;
        this.parentSchema = parentSchema;
        this.context = context;

        this.schemaName = Common.getFieldNameFromJsonPath(schemaPath);
    }

    public String getSchemaPath() {
        return schemaPath;
    }

    public JsonNode getSchemaNode() {
        return schemaNode;
    }

    public JsonMold getParentSchema() {
        return parentSchema;
    }

    public JsonMoldContext getContext() {
        return context;
    }

    public String getSchemaName() {
        return schemaName;
    }
}
