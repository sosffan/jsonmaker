package com.ffanonline.json.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.json.JsonSchemaModel;
import com.ffanonline.json.JsonSchemaModelContext;
import com.ffanonline.json.creator.JsonDataCreator;
import com.ffanonline.json.utils.Common;

public abstract class BaseJsonGenerator {

    private final String schemaPath;
    private final JsonNode schemaNode;
    private final JsonSchemaModel currentJsonSchemaModel;
    private final JsonSchemaModelContext context;
    private final String fieldName;

    private final Boolean isRequired;
    private final Boolean isNullable;

    protected BaseJsonGenerator(String schemaPath, JsonNode schemaNode, JsonSchemaModel currentJsonSchemaModel, JsonSchemaModelContext context) {
        this.schemaPath = schemaPath;
        this.schemaNode = schemaNode;
        this.currentJsonSchemaModel = currentJsonSchemaModel;
        this.context = context;

        this.isRequired = context.getFieldInfo(schemaPath).getRequired();
        this.isNullable = context.getFieldInfo(schemaPath).getNullable();

        this.fieldName = Common.getFieldNameFromJsonPath(schemaPath);
    }



    /***
     *
     * @param creator
     * @param originalValue
     * @return
     * @throws Exception
     */
    public abstract JsonNode create(JsonDataCreator creator, JsonNode originalValue) throws Exception;

    public String getSchemaPath() {
        return schemaPath;
    }

    public JsonNode getSchemaNode() {
        return schemaNode;
    }

    public JsonSchemaModel getCurrentJsonSchemaModel() {
        return currentJsonSchemaModel;
    }

    public JsonSchemaModelContext getContext() {
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


    public JsonNode generateNullNode() throws Exception {
        if (this.getClass().isInstance(ArrayJsonGenerator.class)) {
            ObjectNode node = getContext().getMapper().createObjectNode();
            ArrayNode arrayNode;
            if (getFieldName() != null) {
                arrayNode = node.putArray(getFieldName());
            } else {
                throw new Exception("No Field Name for this Array Object.");
                // arrayNode = getContext().getMapper().createArrayNode();
            }
            arrayNode.addNull();
            return node;

        } else if (this.getClass().isInstance(ObjectJsonGenerator.class)) {
            ObjectNode on = getContext().getMapper().createObjectNode();
            ObjectNode propRootNode;

            if (getFieldName() != null) {
                propRootNode = on.putObject(getFieldName());
            } else {
                propRootNode = on;
            }
            propRootNode.nullNode();
            return on;

        } else {
            if (getFieldName() == null) {
                return NullNode.getInstance();
            } else {
                return getContext().getMapper().createObjectNode().putNull(getFieldName());
            }

        }
    }

    JsonNode handleOperationType(int operationType, String jsonPath) throws Exception {
        getContext().markAsTraversed(jsonPath);

        switch (operationType) {
            case 1:
                if (Boolean.FALSE.equals(getRequired())) return null;
                break;
            case 2:
                if (Boolean.TRUE.equals(getNullable())) return generateNullNode();
                break;
            default: throw new Exception("Not valid operation type.");
        }
        throw new Exception("no valid operationType");
    }
}
