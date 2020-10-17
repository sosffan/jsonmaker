package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.creator.JsonDataCreator;
import com.ffanonline.testing.entity.BaseJsonMaker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonMold {

    private final Set<String> types = new HashSet<String>();
    private final Set<String> requiredFields = new HashSet<String>();
    private final JsonMoldContext context;
    private final Boolean isRequired;
    private BaseJsonMaker maker;
    private JsonNode schemaNode; // Value of the property
    private String schemaPath = "#"; // Json Path
    private JsonMold parentSchema = null;
    private Map<String, JsonMold> propertiesNode = new HashMap<String, JsonMold>();

    public JsonMold(JsonMoldContext context, JsonNode schemaNode) {
        this(context, schemaNode, null);

    }

    public JsonMold(JsonMoldContext context, JsonNode schemaNode, JsonMold parentSchema) {
        this(context, "#", schemaNode, parentSchema, false);
    }

    public JsonMold(JsonMoldContext context, String schemaPath, JsonNode schemaNode, JsonMold parentSchema, Boolean isRequired) {
        this.schemaPath = schemaPath;
        this.schemaNode = schemaNode;
        this.parentSchema = parentSchema;
        this.context = context;
        this.isRequired = isRequired;
    }

    public JsonMold initialize() throws Exception {
        if (this.schemaNode.get("$ref") != null) {
            String refPath = this.schemaNode.get("$ref").textValue();
            this.schemaNode = this.context.getRootNode().at(refPath.substring(1));
        }

        fetchTypeProperties(this.schemaNode);
        fetchRequiredFields(this.schemaNode);

        Boolean isNullable = false;
        if (this.types.contains("null")) {
            isNullable = true;
        }

        context.addFieldInfo(schemaPath, isRequired, isNullable);

        if (this.types.contains(JsonFieldType.OBJECT.getName())) {
            this.maker = JsonFieldType.OBJECT.newJsonMaker(this.schemaPath, this.schemaNode, this, this.context, isRequired);
        } else if (this.types.contains(JsonFieldType.ARRAY.getName())) {
            this.maker = JsonFieldType.ARRAY.newJsonMaker(this.schemaPath, this.schemaNode, this, this.context, isRequired);
        } else {
            for (String a : this.types) {
                if (!a.equals("null")) {
                    this.maker = JsonFieldType.getByValue(a).newJsonMaker(this.schemaPath, this.schemaNode, this, this.context, isRequired);
                }
            }
        }

        return this;
    }


    public JsonNode assembleJson(JsonDataCreator creator) throws Exception {
        return this.maker.create(creator);
    }

    public String assembleJsonString(JsonDataCreator creator) throws Exception {
        JsonNode node = assembleJson(creator);
        return context.getMapper().writeValueAsString(node);
    }

    private void fetchRequiredFields(JsonNode schemaNode) {
        JsonNode node = schemaNode.get(Keyword.REQUIRED.getName());
        if (node != null) {
            for (JsonNode a : node) {
                this.requiredFields.add(a.textValue());
            }
        }
    }

    private void fetchTypeProperties(JsonNode schemaNode) {
        JsonNode node = schemaNode.get(Keyword.TYPE.getName());
        if (node.size() == 0) {
            this.types.add(node.textValue());
        } else {
            for (JsonNode a : node) {
                this.types.add(a.textValue());
            }
        }
    }


    public String getSchemaPath() {
        return schemaPath;
    }

    public void setSchemaPath(String schemaPath) {
        this.schemaPath = schemaPath;
    }

    public JsonMold getParentSchema() {
        return parentSchema;
    }

    public void setParentSchema(JsonMold parentSchema) {
        this.parentSchema = parentSchema;
    }

    public JsonNode getSchemaNode() {
        return schemaNode;
    }

    public Map<String, JsonMold> getPropertiesNode() {
        return propertiesNode;
    }

    public void setPropertiesNode(Map<String, JsonMold> propertiesNode) {
        this.propertiesNode = propertiesNode;
    }

    public Set<String> getRequiredFields() {
        return requiredFields;
    }
}
