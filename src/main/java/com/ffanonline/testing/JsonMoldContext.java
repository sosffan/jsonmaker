package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JsonMoldContext {
    private final JsonNode rootNode;
    private final ObjectMapper mapper;

    private final Map<String, FieldInformation> fieldsInfo = new HashMap<>();

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

    public void addFieldInfo(String jsonPath, Boolean isRequired, Boolean isNullable) {
        FieldInformation fieldInfo = new FieldInformation(jsonPath, isRequired, isNullable);
        fieldsInfo.put(jsonPath, fieldInfo);
    }

    public FieldInformation getFieldInfo(String jsonPath) {
        return fieldsInfo.get(jsonPath);
    }

    public void markAsTraversed(String path) {
        fieldsInfo.get(path).isTraversed = true;
    }

    public Map<String, FieldInformation> getFieldsInfo() {
        return this.fieldsInfo;
    }

    public class FieldInformation {
        private final Boolean isRequired;
        private final Boolean isNullable;
        private Boolean isTraversed = false;
        private final String jsonPath;

        FieldInformation(String jsonPath, Boolean isRequired, Boolean isNullable) {
            this.jsonPath = jsonPath;
            this.isRequired = isRequired;
            this.isNullable = isNullable;
        }

        public Boolean getRequired() {
            return isRequired;
        }

        public Boolean getNullable() {
            return isNullable;
        }

        public Boolean getTraversed() {
            return isTraversed;
        }

        public String getJsonPath() {
            return jsonPath;
        }
    }
}
