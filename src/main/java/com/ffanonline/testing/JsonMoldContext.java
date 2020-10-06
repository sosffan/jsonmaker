package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonMoldContext {
    private final JsonNode rootNode;
    private final ObjectMapper mapper;

    private ObjectNode resultRootNode;

    private int maxItems = 20;

    public JsonMoldContext(JsonNode rootNode, ObjectMapper mapper) {
        this.rootNode = rootNode;
        this.mapper = mapper;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public JsonNode getRootNode() {
        return rootNode;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public ObjectNode getResultRootNode() {
        return resultRootNode;
    }

    public void setResultRootNode(ObjectNode resultRootNode) {
        this.resultRootNode = resultRootNode;
    }
}
