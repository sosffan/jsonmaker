package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;

public class OutcomeData {
    private String path;
    private JsonNode jsonData;
    private JsonNode originalValue;
    private JsonNode newValue;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JsonNode getJsonData() {
        return jsonData;
    }

    public void setJsonData(JsonNode jsonData) {
        this.jsonData = jsonData;
    }

    public JsonNode getNewValue() {
        return newValue;
    }

    public void setNewValue(JsonNode newValue) {
        this.newValue = newValue;
    }

    public JsonNode getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(JsonNode originalValue) {
        this.originalValue = originalValue;
    }
}
