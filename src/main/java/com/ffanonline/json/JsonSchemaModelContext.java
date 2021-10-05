package com.ffanonline.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffanonline.json.utils.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JsonSchemaModelContext {
    Logger logger = LoggerFactory.getLogger(JsonSchemaModelContext.class);

    private final JsonNode rootNode;
    private final ObjectMapper mapper;
    private final Map<String, FieldInformation> fieldsInfo = new HashMap<>();

    private int minItems = 1;
    private int maxItems = 20;

    private int minLength = 1;
    private int maxLength = 100;

    private int minimum = 0;
    private int maximum = 10000;


    public JsonSchemaModelContext(JsonNode rootNode, ObjectMapper mapper) {
        this.rootNode = rootNode;
        this.mapper = mapper;

        Properties properties = Common.getPropertiesFromProjectPath("jsonGen.properties");
        if (properties != null) {
            this.minItems = Integer.parseInt(properties.getProperty("jsonGen.constraints.minItems"));
            this.maxItems = Integer.parseInt(properties.getProperty("jsonGen.constraints.maxItems"));
            this.minLength = Integer.parseInt(properties.getProperty("jsonGen.constraints.minLength"));
            this.maxLength = Integer.parseInt(properties.getProperty("jsonGen.constraints.maxLength"));
            this.minimum = Integer.parseInt(properties.getProperty("jsonGen.constraints.minimum"));
            this.maximum = Integer.parseInt(properties.getProperty("jsonGen.constraints.maximum"));
        }
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

    public int getMinItems() {
        return minItems;
    }

    public void setMinItems(int minItems) {
        this.minItems = minItems;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public void addFieldInfo(String jsonPath, Boolean isRequired, Boolean isNullable, JsonSchemaModel schemaModel) {
//        if (jsonPath.endsWith("[]")){return;} // Array field has already been added without "[]", no need to add twice
        FieldInformation fieldInfo = new FieldInformation(jsonPath, isRequired, isNullable, schemaModel);
        fieldsInfo.put(jsonPath, fieldInfo);
    }

    public FieldInformation getFieldInfo(String jsonPath) {
//        if (jsonPath.endsWith("[]")) {jsonPath = jsonPath.replace("[]", "");} // Array field has already been added without "[]"
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
        private final String jsonPath;
        private Boolean isTraversed = false;
        private JsonSchemaModel schemaModel;

        FieldInformation(String jsonPath, Boolean isRequired, Boolean isNullable, JsonSchemaModel schemaModel) {
            this.jsonPath = jsonPath;
            this.isRequired = isRequired;
            this.isNullable = isNullable;
            this.schemaModel = schemaModel;
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

        public JsonSchemaModel getSchemaModel() {return schemaModel;}
    }
}
