package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.testing.JsonSchemaModel;
import com.ffanonline.testing.JsonSchemaModelContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.constraints.BaseConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ObjectJsonGenerator extends BaseJsonGenerator {
    private final Map<String, JsonSchemaModel> propertiesMap = new HashMap<>();
    BaseConstraint constraint;

    public ObjectJsonGenerator(String schemaPath, JsonNode schemaNode, JsonSchemaModel currentJsonSchemaModel, JsonSchemaModelContext context) throws Exception {
        super(schemaPath, schemaNode, currentJsonSchemaModel, context);
        JsonNode propertiesNode = schemaNode.get(Keyword.PROPERTIES.getName());

        Set<String> requiredFields = currentJsonSchemaModel.getRequiredFields();

        Iterator<String> properties = propertiesNode.fieldNames();
        while (properties.hasNext()) {
            String propertyName = properties.next();

            Boolean propertyIsRequired = requiredFields.contains(propertyName);

            String path = schemaPath + "/" + propertyName;
            JsonNode node = propertiesNode.get(propertyName);
            JsonSchemaModel jsonSchemaModel = new JsonSchemaModel(context, path, node, currentJsonSchemaModel, propertyIsRequired);

            propertiesMap.put(propertyName, jsonSchemaModel.initialize());
        }

        constraint = new BaseConstraint(getRequired(), getNullable());
    }

    @Override
    public JsonNode create(JsonDataCreator creator, JsonNode originalValue) throws Exception {

        ObjectNode on = getContext().getMapper().createObjectNode();
        ObjectNode propRootNode;

        if (getFieldName() != null) {
            propRootNode = on.putObject(getFieldName());
        } else {
            propRootNode = on;
        }

        for (Map.Entry<String, JsonSchemaModel> item : propertiesMap.entrySet()) {
            JsonSchemaModel schema = item.getValue();
            ObjectNode node = (ObjectNode) schema.buildJson(creator);
            if (node != null) {
                propRootNode.setAll(node);
            }
        }

        return on;
    }
}
