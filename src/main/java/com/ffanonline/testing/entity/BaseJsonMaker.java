package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    public JsonNode create(JsonDataCreator creator) throws Exception {
        return create(creator, 0, null);
    }


    /***
     *
     * @param creator
     * @return
     * @throws Exception
     */
    public abstract JsonNode create(JsonDataCreator creator, int type, String jsonPath) throws Exception;

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


    public JsonNode generateNullNode() throws Exception {
        if (this.getClass().isInstance(ArrayJsonMaker.class)) {
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

        } else if (this.getClass().isInstance(ObjectJsonMaker.class)) {
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
}
