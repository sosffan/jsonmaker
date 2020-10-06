package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonMoldFactory {

    private final ObjectMapper mapper;

    private JsonMoldFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static JsonMoldFactory getInstance(SpecVersion.VersionFlag versionFlag) {
        return new Builder().build();
    }

    public JsonMold getJsonMold(String jsonSchemaString) {
        JsonNode node = null;
        try {
            node = this.mapper.readTree(jsonSchemaString);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return getJsonMold(node);
    }

    public JsonMold getJsonMold(InputStream inputStream) {
        JsonNode node = null;
        try {
            node = this.mapper.readTree(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getJsonMold(node);

    }

    public JsonMold getJsonMold(JsonNode schemaNode) {
        return newJsonMold(schemaNode);
    }

    private JsonMold newJsonMold(JsonNode schemaNode) {

        JsonMoldContext context = new JsonMoldContext(schemaNode, this.mapper);

        return new JsonMold(context, schemaNode);
    }

    private static class Builder {
        private final ObjectMapper objectMapper = new ObjectMapper();

        private Builder() {
        }

        private JsonMoldFactory build() {
            return new JsonMoldFactory(objectMapper);
        }
    }

}
