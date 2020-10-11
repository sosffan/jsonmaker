package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.creator.JsonDataCreator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ObjectJsonMaker extends BaseJsonMaker {
    private final Map<String, JsonMold> propertiesMap = new HashMap<>();
    private Set<String> requiredFields = null;

    public ObjectJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold currentJsonMold, JsonMoldContext context, Boolean isRequired) throws Exception {
        super(schemaPath, schemaNode, currentJsonMold, context, isRequired);
        JsonNode propertiesNode = schemaNode.get(Keyword.PROPERTIES.getName());

        requiredFields = currentJsonMold.getRequiredFields();

        Iterator<String> properties = propertiesNode.fieldNames();
        while (properties.hasNext()) {
            String propertyName = properties.next();

            Boolean propertyIsRequired = requiredFields.contains(propertyName);

            String path = schemaPath + "/" + propertyName;
            JsonNode node = propertiesNode.get(propertyName);
            JsonMold jsonMold = new JsonMold(context, path, node, currentJsonMold, propertyIsRequired);

            propertiesMap.put(propertyName, jsonMold.initialize());
        }

    }

    @Override
    public JsonNode create(JsonDataCreator creator) throws Exception {

        ObjectNode on = getContext().getMapper().createObjectNode();

        if (getFieldName() != null) {
            on = on.putObject(getFieldName());
        }

        for (String propertyName : propertiesMap.keySet()) {
            JsonMold schema = propertiesMap.get(propertyName);
            ObjectNode node = (ObjectNode) schema.assembleJson(creator);
            if (node != null) {
                on.setAll(node);
            }
        }

        return on;
    }
}
