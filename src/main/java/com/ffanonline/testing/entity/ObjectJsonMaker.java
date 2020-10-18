package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.constraints.BaseConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ObjectJsonMaker extends BaseJsonMaker {
    private final Map<String, JsonMold> propertiesMap = new HashMap<>();
    BaseConstraint constraint;

    public ObjectJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold currentJsonMold, JsonMoldContext context) throws Exception {
        super(schemaPath, schemaNode, currentJsonMold, context);
        JsonNode propertiesNode = schemaNode.get(Keyword.PROPERTIES.getName());

        Set<String> requiredFields = currentJsonMold.getRequiredFields();

        Iterator<String> properties = propertiesNode.fieldNames();
        while (properties.hasNext()) {
            String propertyName = properties.next();

            Boolean propertyIsRequired = requiredFields.contains(propertyName);

            String path = schemaPath + "/" + propertyName;
            JsonNode node = propertiesNode.get(propertyName);
            JsonMold jsonMold = new JsonMold(context, path, node, currentJsonMold, propertyIsRequired);

            propertiesMap.put(propertyName, jsonMold.initialize());
        }

        constraint = new BaseConstraint(getRequired(), getNullable());
    }

    @Override
    public JsonNode create(JsonDataCreator creator, int type, String jsonPath) throws Exception {
        if (jsonPath != null && jsonPath.equals(getSchemaPath())) {
            getContext().markAsTraversed(jsonPath);

            switch (type) {
                case 1:
                    if (!getRequired()) return null;
                    break;
                case 2:
                    if (getNullable()) return generateNullNode();
                    break;
            }
        }

        ObjectNode on = getContext().getMapper().createObjectNode();
        ObjectNode propRootNode;

        if (getFieldName() != null) {
            propRootNode = on.putObject(getFieldName());
        } else {
            propRootNode = on;
        }

        for (String propertyName : propertiesMap.keySet()) {
            JsonMold schema = propertiesMap.get(propertyName);
            ObjectNode node = (ObjectNode) schema.assembleJson(creator, type, jsonPath);
            if (node != null) {
                propRootNode.setAll(node);
            }
        }

        return on;
    }
}
