package com.ffanonline.testing;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.testing.utils.Common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonBase {
    private final JsonMoldContext context;
    private JsonNode jsonRootNode;

    public JsonBase(JsonMoldContext context, JsonNode node) {
        this.context = context;
        this.jsonRootNode = node;
    }

    public JsonMoldContext getContext() {
        return context;
    }

    public JsonNode getJsonRootNode() {
        return jsonRootNode;
    }

    public void setJsonRootNode(JsonNode jsonRootNode) {
        this.jsonRootNode = jsonRootNode;
    }



    public Map<String, JsonNode> generateJsonCollection(String fieldFilePath) throws IOException {
        InputStream fieldStream = this.getClass().getResourceAsStream(fieldFilePath);

        JsonNode fieldPathRootNode = context.getMapper().readTree(fieldStream);
        Iterator<String> fieldNamesIterator = fieldPathRootNode.fieldNames();

        Map<String, JsonNode> result = new HashMap<>();


        while (fieldNamesIterator.hasNext()) {
            String fieldType = fieldNamesIterator.next();
            List<JsonNode> fieldPathNodeList = fieldPathRootNode.findValues(fieldType);

            InputStream dataStream = this.getClass().getResourceAsStream(fieldType + ".json");
            JsonNode dataNode = context.getMapper().readTree(dataStream);
            Iterator<JsonNode> sampleDataArray = dataNode.elements();

            while (sampleDataArray.hasNext()) {

                JsonNode value = jsonRootNode;
                JsonNode sampleData = sampleDataArray.next();

                for (JsonNode fieldPathNode : fieldPathNodeList) {
                    JsonNode node = value.at(fieldPathNode.textValue());
                    node = sampleData; // TODO: should be set value from parent.
                }

                String key = fieldType + sampleData;
                result.put(key, value);

            }
        }
        return result;
    }


}
