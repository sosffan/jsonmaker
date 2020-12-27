package com.ffanonline.testing;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.testing.creator.JsonDataCreator;
import com.ffanonline.testing.entity.BaseJsonGenerator;
import com.ffanonline.testing.utils.Common;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JsonSchemaModel {

    private final Set<String> types = new HashSet<String>();
    private final Set<String> requiredFields = new HashSet<String>();
    private final JsonSchemaModelContext context;
    private final Boolean isRequired;
    private BaseJsonGenerator generator;
    private JsonNode schemaNode; // Value of the property
    private String schemaPath = "#"; // Json Path
    private JsonSchemaModel parentSchema = null;
    private Map<String, JsonSchemaModel> propertiesNode = new HashMap<String, JsonSchemaModel>();

    public JsonSchemaModel(JsonSchemaModelContext context, JsonNode schemaNode) {
        this(context, schemaNode, null);

    }

    public JsonSchemaModel(JsonSchemaModelContext context, JsonNode schemaNode, JsonSchemaModel parentSchema) {
        this(context, "#", schemaNode, parentSchema, false);
    }

    public JsonSchemaModel(JsonSchemaModelContext context, String schemaPath, JsonNode schemaNode, JsonSchemaModel parentSchema, Boolean isRequired) {
        this.schemaPath = schemaPath;
        this.schemaNode = schemaNode;
        this.parentSchema = parentSchema;
        this.context = context;
        this.isRequired = isRequired;
    }

    public JsonSchemaModel initialize() throws Exception {
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
            this.generator = JsonFieldType.OBJECT.newJsonGenerator(this.schemaPath, this.schemaNode, this, this.context, isRequired);
        } else if (this.types.contains(JsonFieldType.ARRAY.getName())) {
            this.generator = JsonFieldType.ARRAY.newJsonGenerator(this.schemaPath, this.schemaNode, this, this.context, isRequired);
        } else {
            for (String type : this.types) {
                if (!type.equals("null")) {
                    this.generator = JsonFieldType.getByValue(type).newJsonGenerator(this.schemaPath, this.schemaNode, this, this.context, isRequired);
                }
            }
        }

        return this;
    }



    public JsonNode buildJson(JsonDataCreator creator) throws Exception {
        return this.generator.create(creator);
    }

    public String generateJsonString(JsonDataCreator creator) throws Exception {
        JsonNode node = buildJson(creator);
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

    public JsonSchemaModel getParentSchema() {
        return parentSchema;
    }

    public void setParentSchema(JsonSchemaModel parentSchema) {
        this.parentSchema = parentSchema;
    }

    public JsonNode getSchemaNode() {
        return schemaNode;
    }

    public Map<String, JsonSchemaModel> getPropertiesNode() {
        return propertiesNode;
    }

    public void setPropertiesNode(Map<String, JsonSchemaModel> propertiesNode) {
        this.propertiesNode = propertiesNode;
    }

    public Set<String> getRequiredFields() {
        return requiredFields;
    }

    // Generate Json bundle, which to crawl the json schema to generate multiple json node that matchs the operationType. (one match one Json)
    // operationType: 1-operational field to be removed, 2-nullable filed to be null
    public Map<String, JsonNode> generateJsonBundle(JsonDataCreator creator, int operationType) throws Exception {

        Map<String, JsonNode> results = new HashMap<>();
        for (Map.Entry<String, JsonSchemaModelContext.FieldInformation> item : context.getFieldsInfo().entrySet()) {

            JsonNode resultNode = this.generator.create(creator);
            if (null == updateJsonBasedOnOperationType(operationType, resultNode, item.getValue())) {continue;}

            results.put(item.getKey(), resultNode);
        }

        return results;
    }

    public Map<String, JsonNode> generateJsonBundle(int operationType, JsonNode sampleJsonNode) {
        Map<String, JsonNode> results = new HashMap<>();
        for (Map.Entry<String, JsonSchemaModelContext.FieldInformation> item : context.getFieldsInfo().entrySet()) {

            JsonNode resultNode = sampleJsonNode.deepCopy();
            if (null == updateJsonBasedOnOperationType(operationType, resultNode, item.getValue())) {continue;}

            results.put(item.getKey(), resultNode);
        }
        return results;
    }

    private JsonNode updateJsonBasedOnOperationType(int operationType, JsonNode resultNode, JsonSchemaModelContext.FieldInformation fieldInfo) {
        String jsonPath = fieldInfo.getJsonPath().replace("#", ""); //TODO: should "#" removed for root node?
        //If it is any properties that under array, only the first one would be updated. so will just select the first array item.
        if (Common.isUnderArray(jsonPath)) {
            jsonPath = jsonPath.replace("[]", "/0");
        }

        JsonPointer pointer = JsonPointer.compile(jsonPath);

        String fieldName = Common.getFieldNameFromJsonPath(jsonPath);

        if (null == pointer.head()) { return null;} // Skip root element.
        JsonNode parentNode = resultNode.at(pointer.head());
        ObjectNode oNode;
        if (parentNode instanceof ObjectNode) {
            oNode = (ObjectNode) parentNode;
        } else return null;

        // TODO: switch first, then loop to get all parent node for each elements.
        switch (operationType) {
            case 1:
                if (!fieldInfo.getRequired() && !jsonPath.isEmpty()) {
                    oNode.remove(fieldName);
                } else return null;
                break;
            case 2:
                if (fieldInfo.getNullable()) {
                    oNode.putNull(fieldName);
                } else return null;
                break;
        }

        return resultNode;
    }


    public Map<String, JsonNode> generateJsonBundle(int operationType, String sampleJsonString) throws JsonProcessingException {
        JsonNode node = context.getMapper().readTree(sampleJsonString);
        return generateJsonBundle(operationType, node);
    }

    public Map<String, JsonNode> generateJsonBundle(int operationType, InputStream sample) throws IOException {
        JsonNode node = context.getMapper().readTree(sample);
        return generateJsonBundle(operationType, node);
    }


    public Map<String, JsonNode> generateJsonBundleForUnRequiredField(InputStream sample) throws IOException {
        return generateJsonBundle(1, sample);
    }

    public Map<String, JsonNode> generateJsonBundleForNullField(InputStream sample) throws IOException {
        return generateJsonBundle(2, sample);
    }
}
