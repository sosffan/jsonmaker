package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.ffanonline.testing.creator.JsonDataCreator;
import com.ffanonline.testing.entity.*;
import com.ffanonline.testing.utils.Common;

import java.util.*;

public class JsonMold {

    private final Set<String> types = new HashSet<String>();
    private final Set<String> requiredFields = new HashSet<String>();
    private final JsonMoldContext context;
    private final Map<String, JsonMaker> makers = new HashMap<>();
    private JsonNode schemaNode; // Value of the property
    private String schemaPath = "#"; // Json Path
    private JsonMold parentSchema = null;
    private Map<String, JsonMold> propertiesNode = new HashMap<String, JsonMold>();

    public JsonMold(JsonMoldContext context, JsonNode schemaNode) {
        this(context, schemaNode, null);

    }

    public JsonMold(JsonMoldContext context, JsonNode schemaNode, JsonMold parentSchema) {
        this(context, "#", schemaNode, parentSchema);
    }

    public JsonMold(JsonMoldContext context, String schemaPath, JsonNode schemaNode, JsonMold parentSchema) {
        this.schemaPath = schemaPath;
        this.schemaNode = schemaNode;
        this.parentSchema = parentSchema;
        this.context = context;
    }

    public JsonMold initialize() throws Exception {
        if (this.schemaNode.get("$ref") != null) {
            String refPath = this.schemaNode.get("$ref").textValue();
            this.schemaNode = this.context.getRootNode().at(refPath.substring(1));
        }

        fetchTypeProperties(this.schemaNode);
        fetchRequiredFields(this.schemaNode);

        if (this.types.contains(JsonFieldType.OBJECT.getName())) {

            JsonMaker maker = JsonFieldType.OBJECT.newJsonMaker(this.schemaPath, this.schemaNode, this, this.context);
            this.makers.put(this.schemaPath, maker);

        } else if (this.types.contains(JsonFieldType.ARRAY.getName())) {

            JsonMaker maker = JsonFieldType.ARRAY.newJsonMaker(this.schemaPath, this.schemaNode, this, this.context);
            this.makers.put(this.schemaPath, maker);
        } else {
            for (String a : this.types) {
                if (!a.equals("null")) {
                    JsonMaker maker = JsonFieldType.getByValue(a).newJsonMaker(this.schemaPath, this.schemaNode, this, this.context);
                    this.makers.put(this.schemaPath, maker);
                }
            }
        }

        return this;
    }


    public JsonNode assembleJson(JsonDataCreator creator, ObjectNode resultParnetnode) throws Exception {
        if (resultParnetnode == null) {
            resultParnetnode = context.getMapper().createObjectNode();
            context.setResultRootNode(resultParnetnode);
        }

        for (String path : makers.keySet()) {

            JsonMaker maker = makers.get(path);
            Object value = maker.create(creator);
            String fieldName = Common.getFieldNameFromJsonPath(schemaPath);

            if (fieldName == null) {
                if (maker.getClass().equals(StringJsonMaker.class)) {
                    return TextNode.valueOf((String) value);
                } else if (maker.getClass().equals(NumberJsonMaker.class)) {
                    return DoubleNode.valueOf((Double) value);
                } else if (maker.getClass().equals(IntegerJsonMaker.class)) {
                    return LongNode.valueOf((Long) value);
                } else if (maker.getClass().equals(BooleanJsonMaker.class)) {
                    return BooleanNode.valueOf((Boolean) value);
                } else if (maker.getClass().equals(ArrayJsonMaker.class)) {
//                    resultParnetnode.putArray(fieldName).addAll((List<JsonNode>) value);
                } else if (maker.getClass().equals(ObjectJsonMaker.class)) {
                    ObjectNode on = context.getMapper().createObjectNode();
                    Map<String, ObjectNode> propertyMap = (Map<String, ObjectNode>) value;
                    for (String a : propertyMap.keySet()) {
                        on.setAll(propertyMap.get(a));
                    }
                    return on;
                }
            }

            if (maker.getClass().equals(StringJsonMaker.class)) {
                resultParnetnode.put(fieldName, (String) value);
            } else if (maker.getClass().equals(NumberJsonMaker.class)) {
                resultParnetnode.put(fieldName, (Double) value);
            } else if (maker.getClass().equals(IntegerJsonMaker.class)) {
                resultParnetnode.put(fieldName, (Long) value);
            } else if (maker.getClass().equals(BooleanJsonMaker.class)) {
                resultParnetnode.put(fieldName, (Boolean) value);
            } else if (maker.getClass().equals(ArrayJsonMaker.class)) {
                resultParnetnode.putArray(fieldName).addAll((List<JsonNode>) value);
            } else if (maker.getClass().equals(ObjectJsonMaker.class)) {
                ObjectNode on = resultParnetnode.putObject(fieldName);
                Map<String, ObjectNode> propertyMap = (Map<String, ObjectNode>) value;
                for (String a : propertyMap.keySet()) {
                    on.setAll(propertyMap.get(a));
                }
            }
        }

        return resultParnetnode;
    }

    public String assembleJsonString(JsonDataCreator creator, ObjectNode resultParnetnode) throws Exception {
        JsonNode node = assembleJson(creator, resultParnetnode);

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


    private void getTypeProperties(String fieldName) {
        JsonNode node = this.getSchemaNode().get(fieldName);
        if (node.size() == 0) {
            this.types.add(node.textValue());
        } else {
            for (JsonNode a : node) {
                this.types.add(a.textValue());
            }
        }
        System.out.println(this.schemaPath + "'s type: " + this.types.size() + "  + " + this.types.toString());
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

}
