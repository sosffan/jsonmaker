package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.creator.JsonDataCreator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ObjectJsonMaker extends BaseJsonMaker {
    private final Map<String, JsonMold> propertiesMap = new HashMap<>();

    public ObjectJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold parentSchema, JsonMoldContext context) throws Exception {
        super(schemaPath, schemaNode, parentSchema, context);
        JsonNode propertiesNode = schemaNode.get(Keyword.PROPERTIES.getName());

        Iterator<String> properties = propertiesNode.fieldNames();
        while (properties.hasNext()) {
            String propertyName = properties.next();

            String path = schemaPath + "/" + propertyName;
            JsonNode node = propertiesNode.get(propertyName);
            JsonMold jsonMold = new JsonMold(context, path, node, parentSchema);

            propertiesMap.put(propertyName, jsonMold.initialize());
        }

    }

    @Override
    public Object create(JsonDataCreator creator) throws Exception {

        Map<String, JsonNode> resultFields = new HashMap<>();

        for (String propertyName : propertiesMap.keySet()) {
            JsonMold schema = propertiesMap.get(propertyName);
            JsonNode node = schema.assembleJson(creator, null);
            resultFields.put(propertyName, node);
        }

        return resultFields;
    }
}
