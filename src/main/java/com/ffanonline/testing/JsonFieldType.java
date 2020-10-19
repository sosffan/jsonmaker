package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.entity.*;

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
    private final Class generator;

    JsonFieldType(String type, Class generator) {
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

    public BaseJsonGenerator newJsonGenerator(String schemaPath, JsonNode schemaNode, JsonMold currentMold, JsonMoldContext context, Boolean isRequired) throws Exception {
        Constructor<BaseJsonGenerator> c = ((Class<BaseJsonGenerator>) generator).getConstructor(String.class, JsonNode.class, JsonMold.class, JsonMoldContext.class);

        return c.newInstance(schemaPath, schemaNode, currentMold, context);
    }
}
