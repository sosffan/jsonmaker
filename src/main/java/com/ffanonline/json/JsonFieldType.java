package com.ffanonline.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.json.generator.*;

import java.lang.reflect.Constructor;

public enum JsonFieldType {
    OBJECT("object", ObjectJsonGenerator.class),
    ARRAY("array", ArrayJsonGenerator.class),
    STRING("string", StringJsonGenerator.class),
    NUMBER("number", NumberJsonGenerator.class),
    INTEGER("integer", IntegerJsonGenerator.class),
    BOOLEAN("boolean", BooleanJsonGenerator.class),
    NULL("null", StringJsonGenerator.class),
    UNDEFINED("undefined", StringJsonGenerator.class);

    private final String name;
    private final Class<? extends BaseJsonGenerator> generator;

    JsonFieldType(String type, Class<? extends BaseJsonGenerator> generator) {
        this.name = type;
        this.generator = generator;
    }

    public static JsonFieldType getByValue(String value) {

        for (JsonFieldType fieldType : values()) {
            if (fieldType.getName().equals(value)) {
                return fieldType;
            }
        }
        return JsonFieldType.UNDEFINED;
    }

    public String getName() {
        return this.name;
    }

    public BaseJsonGenerator newJsonGenerator(String schemaPath, JsonNode schemaNode, JsonSchemaModel currentJsonSchemaModel, JsonSchemaModelContext context) throws Exception {
        Constructor<BaseJsonGenerator> c = ((Class<BaseJsonGenerator>) generator).getConstructor(String.class, JsonNode.class, JsonSchemaModel.class, JsonSchemaModelContext.class);

        return c.newInstance(schemaPath, schemaNode, currentJsonSchemaModel, context);
    }
}
