package com.ffanonline.testing;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.testing.creator.JsonDataCreator;
import com.ffanonline.testing.entity.BaseJsonGenerator;
import com.ffanonline.testing.utils.Common;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JsonSchemaModel {

    private final Set<String> types = new HashSet<>();
    private final Set<String> requiredFields = new HashSet<>();
    private final JsonSchemaModelContext context;
    private final Boolean isRequired;
    private BaseJsonGenerator generator;
    private JsonNode schemaNode; // Value of the property
    private String schemaPath = ""; // Json Path, root node path is empty string - "".
    private JsonSchemaModel parentSchema = null;

    public JsonSchemaModel(JsonSchemaModelContext context, JsonNode schemaNode) {
        this(context, schemaNode, null);

    }

    public JsonSchemaModel(JsonSchemaModelContext context, JsonNode schemaNode, JsonSchemaModel parentSchema) {
        this(context, "", schemaNode, parentSchema, false);
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

        boolean isNullable = false;
        if (this.types.contains("null")) {
            isNullable = true;
        }
        context.addFieldInfo(schemaPath, isRequired, isNullable, this);

        if (this.types.contains(JsonFieldType.OBJECT.getName())) {
            this.generator = JsonFieldType.OBJECT.newJsonGenerator(this.schemaPath, this.schemaNode, this, this.context);
        } else if (this.types.contains(JsonFieldType.ARRAY.getName())) {
            this.generator = JsonFieldType.ARRAY.newJsonGenerator(this.schemaPath, this.schemaNode, this, this.context);
        } else {
            for (String type : this.types) {
                if (!type.equals("null")) {
//                    if (this.parentSchema !=null && this.parentSchema.types.contains(JsonFieldType.ARRAY.getName())) {
//                        context.addFieldInfo(schemaPath + "/", isRequired, isNullable, this);
//                    }
                    this.generator = JsonFieldType.getByValue(type).newJsonGenerator(this.schemaPath, this.schemaNode, this, this.context);
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

    public Set<String> getRequiredFields() {
        return requiredFields;
    }

    // Generate Json bundle, which to crawl the json schema to generate multiple json node that matches the operationType. (one match field one Json)
    // operationType: 1-operational field to be removed, 2-nullable filed to be null
    public Map<String, JsonNode> generateJsonCollection(int operationType, JsonDataCreator creator) throws Exception {

        Map<String, JsonNode> results = new HashMap<>();
        for (Map.Entry<String, JsonSchemaModelContext.FieldInformation> item : context.getFieldsInfo().entrySet()) {

            JsonNode resultNode = this.generator.create(creator);
            if (null == updateJsonBasedOnOperationType(operationType, resultNode, item.getValue())) {continue;}

            results.put(item.getKey(), resultNode);
        }

        return results;
    }

    public Map<String, JsonNode> generateJsonCollection(int operationType, JsonNode sampleJsonNode) {
        Map<String, JsonNode> results = new HashMap<>();
        for (Map.Entry<String, JsonSchemaModelContext.FieldInformation> item : context.getFieldsInfo().entrySet()) {

            JsonNode resultNode = sampleJsonNode.deepCopy();
            if (null == updateJsonBasedOnOperationType(operationType, resultNode, item.getValue())) {continue;}

            results.put(item.getKey(), resultNode);
        }
        return results;
    }

    private JsonNode updateJsonBasedOnOperationType(int operationType, JsonNode resultNode, JsonSchemaModelContext.FieldInformation fieldInfo) {
        String jsonPath = fieldInfo.getJsonPath();
        //If it is any properties that under array, only the first one would be updated. so will just select the first array item.
        if (Boolean.TRUE.equals(Common.isUnderArray(jsonPath))) {
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
                if (Boolean.FALSE.equals(fieldInfo.getRequired())) {
                    oNode.remove(fieldName);
                } else return null;
                break;
            case 2:
                if (Boolean.TRUE.equals(fieldInfo.getNullable())) {
                    oNode.putNull(fieldName);
                } else return null;
                break;
        }

        return resultNode;
    }


    public Map<String, JsonNode> generateJsonCollection(int operationType, String sampleJsonString) throws JsonProcessingException {
        JsonNode node = context.getMapper().readTree(sampleJsonString);
        return generateJsonCollection(operationType, node);
    }

    public Map<String, JsonNode> generateJsonCollection(int operationType, InputStream sample) throws IOException {
        JsonNode node = context.getMapper().readTree(sample);
        return generateJsonCollection(operationType, node);
    }


    public Map<String, JsonNode> generateJsonCollectionForEachUnRequiredField(InputStream sample) throws IOException {
        return generateJsonCollection(1, sample);
    }

    public Map<String, JsonNode> generateJsonCollectionForEachNullField(InputStream sample) throws IOException {
        return generateJsonCollection(2, sample);
    }

    public Map<String, JsonNode> generateJsonCollectionForEachFields(InputStream sample, JsonDataCreator creator) throws Exception {
        JsonNode sampleJsonNode = context.getMapper().readTree(sample);
        Map<String, JsonNode> results = new HashMap<>();
        for (Map.Entry<String, JsonSchemaModelContext.FieldInformation> item : context.getFieldsInfo().entrySet()) {

            Set<String> types = item.getValue().getSchemaModel().types;
            if (types.contains(JsonFieldType.ARRAY.getName()) || types.contains(JsonFieldType.OBJECT.getName())){continue;}
            JsonNode resultNode = sampleJsonNode.deepCopy();
            JsonNode newValue = item.getValue().getSchemaModel().generator.create(creator);



            //if not object or array, then this.generator.create(creator).  But if array item is not object, then add value to array.
            if (null == updateJsonThroughJsonPath(resultNode, item.getValue(), newValue)) {continue;}

            results.put(item.getKey(), resultNode);
        }

        return results;
    }

    private Object updateJsonThroughJsonPath(JsonNode resultNode, JsonSchemaModelContext.FieldInformation fieldInfo, JsonNode newValue) {
        String jsonPath = fieldInfo.getJsonPath();
        //If it is any properties that under array, only the first one would be updated. so will just select the first array item.
        if (Boolean.TRUE.equals(Common.isUnderArray(jsonPath))) {
            jsonPath = jsonPath.replace("[]", "/0");
        }

        JsonPointer pointer = JsonPointer.compile(jsonPath);

        String fieldName = Common.getFieldNameFromJsonPath(jsonPath);

        if (null == pointer.head()) { return null;} // Skip root element.
        JsonNode parentNode = resultNode.at(pointer.head());

        if (parentNode instanceof ObjectNode) {
            ObjectNode oNode = (ObjectNode) parentNode;
            oNode.setAll((ObjectNode) newValue);
        } else if (parentNode instanceof ArrayNode) {
            ArrayNode aNode = (ArrayNode) parentNode;
            aNode.set(0, newValue);
        } else return null;



        return resultNode;
    }
}
