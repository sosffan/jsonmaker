package com.ffanonline.testing;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.testing.utils.Common;

import javax.management.monitor.CounterMonitor;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonSample {
    private final JsonMoldContext context;
    private JsonNode jsonRootNode;

    public JsonSample(JsonMoldContext context, JsonNode node) {
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



    public Map<String, JsonNode> generateJsonCollectionBasedOnData(InputStream configStream) throws IOException {

        JsonNode configRootNode = context.getMapper().readTree(configStream);
        Iterator<String> dataPathIterator = configRootNode.fieldNames();

        Map<String, JsonNode> result = new HashMap<>();

        while (dataPathIterator.hasNext()) {
            String dataPath = dataPathIterator.next();
            JsonNode fieldPathArray = configRootNode.findValue(dataPath);

            InputStream dataStream = this.getClass().getResourceAsStream(dataPath);
            JsonNode dataNode = context.getMapper().readTree(dataStream);
            Iterator<JsonNode> sampleDataArray = dataNode.elements();

            while (sampleDataArray.hasNext()) {
                JsonNode sampleData = sampleDataArray.next();

                for (JsonNode fieldPath : fieldPathArray) {
                    JsonNode value = jsonRootNode.deepCopy();

                    JsonNode parentNode = Common.getParentNode(value, fieldPath.asText());
                    ObjectNode oNode;
                    if (parentNode instanceof ObjectNode) {
                        oNode = (ObjectNode) parentNode;
                    } else break;
                    oNode.set(Common.getFieldNameFromJsonPath(fieldPath.asText()), sampleData);

                    String key = dataPath + "&" + sampleData + "&" + fieldPath.asText();
                    result.put(key, value); // if deepCopy root node here, and put value into result, each field would have one item in result.
                }

                // if deepCopy root node here, and put value into result, all fields which have the same sample data would have one item.
            }
        }
        return result;
    }


}
