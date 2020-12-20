package com.ffanonline.testing;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.testing.creator.JsonDataCreator;
import com.ffanonline.testing.entity.BaseJsonGenerator;
import com.ffanonline.testing.utils.Common;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JsonMold {

    private final Set<String> types = new HashSet<String>();
    private final Set<String> requiredFields = new HashSet<String>();
    private final JsonMoldContext context;
    private final Boolean isRequired;
    private BaseJsonGenerator generator;
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
        return buildJson(creator, 0, null);
    }

    public JsonNode buildJson(JsonDataCreator creator, int type, String jsonPath) throws Exception {
        return this.generator.create(creator, type, jsonPath);
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

    // Generate Json collection, which to crawl the json schema to generate multiple json node that matchs the operationType. (one match one Json)
    // operationType: 1-operational field to be removed, 2-nullable filed to be null
    public Map<String, JsonNode> generateJsonCollection(JsonDataCreator creator, int operationType) throws Exception {

        Map<String, JsonNode> results = new HashMap<>();

        // todo: filter only required jsonNode, if jsonNode not match any operationType, then skill

        for (Map.Entry<String, JsonMoldContext.FieldInformation> fieldInfo : context.getFieldsInfo().entrySet()) {

            JsonNode node = this.generator.create(creator, operationType, fieldInfo.getKey());

            results.put(fieldInfo.getKey(), node);

        }

        return results;
    }

    public Map<String, JsonNode> generateJsonCollection(int operationType, JsonNode sampleJsonNode) {
        Map<String, JsonNode> results = new HashMap<>();
        for (Map.Entry<String, JsonMoldContext.FieldInformation> fieldInfo : context.getFieldsInfo().entrySet()) {
            String jsonPath = fieldInfo.getKey().replace("#", ""); //TODO: should "#" removed for root node?
            JsonPointer pointer = JsonPointer.compile(jsonPath);
            JsonNode resultNode = sampleJsonNode.deepCopy();
            JsonNode node = resultNode.at(pointer);
            String fieldName = Common.getFieldNameFromJsonPath(jsonPath);

            switch (operationType) {
                case 1:
                    if (!fieldInfo.getValue().getRequired() && !jsonPath.isEmpty()) {
                        JsonNode parentNode = resultNode.at(pointer.head());
                        if (parentNode instanceof ObjectNode) {
                            ObjectNode oNode = (ObjectNode) parentNode;
                            oNode.remove(fieldName);
                        }
                    } else continue;
                    break;
                case 2:
                    if (fieldInfo.getValue().getNullable()) {
                        node = NullNode.getInstance(); // TODO: should be set value from parent.
                    }
                    break;
            }

            results.put(jsonPath, resultNode);
        }
        return results;
    }


    public Map<String, JsonNode> generateJsonCollection(int operationType, String sampleJsonString) throws JsonProcessingException {
        JsonNode node = context.getMapper().readTree(sampleJsonString);
        return generateJsonCollection(operationType, node);
    }

    public Map<String, JsonNode> generateJsonCollection(int operationType, InputStream sample) throws IOException {
        JsonNode node = context.getMapper().readTree(sample);
        return generateJsonCollection(operationType, node);
    }


    public Map<String, JsonNode> generateJsonCollectionForUnRequiredField(InputStream sample) throws IOException {
        return generateJsonCollection(1, sample);
        //TODO: support array type
    }
}
