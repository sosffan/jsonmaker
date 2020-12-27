package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonGenFactory {

    private final ObjectMapper mapper;

    private JsonGenFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static JsonGenFactory getInstance(SpecVersion.VersionFlag versionFlag) {
        return new Builder().build();
    }

    public JsonSchemaModel getJsonSchemaModel(String jsonSchemaString) {
        JsonNode node = null;
        try {
            node = this.mapper.readTree(jsonSchemaString);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return getJsonSchemaModel(node);
    }

    public JsonSchemaModel getJsonSchemaModel(InputStream inputStream) {
        JsonNode node = null;
        try {
            node = this.mapper.readTree(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getJsonSchemaModel(node);

    }

    public JsonSchemaModel getJsonSchemaModel(JsonNode schemaNode) {
        return newJsonSchemaModel(schemaNode);
    }

    private JsonSchemaModel newJsonSchemaModel(JsonNode schemaNode) {

        JsonSchemaModelContext context = new JsonSchemaModelContext(schemaNode, this.mapper);

        return new JsonSchemaModel(context, schemaNode);
    }

    public JsonTemplateModel getJsonTemplateModel(InputStream inputStream) {
        JsonNode node = null;
        try {
            node = this.mapper.readTree(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getJsonTemplateModel(node);
    }

    public JsonTemplateModel getJsonTemplateModel(JsonNode node) {
        JsonSchemaModelContext context = new JsonSchemaModelContext(node, this.mapper);

        return new JsonTemplateModel(context, node);
    }

    private static class Builder {
        private final ObjectMapper objectMapper = new ObjectMapper();

        private Builder() {
        }

        private JsonGenFactory build() {
            return new JsonGenFactory(objectMapper);
        }
    }


}
