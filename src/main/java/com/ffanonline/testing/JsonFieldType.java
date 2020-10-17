package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.entity.*;

import java.lang.reflect.Constructor;

public enum JsonFieldType {
    OBJECT("object", ObjectJsonMaker.class),
    ARRAY("array", ArrayJsonMaker.class),
    STRING("string", StringJsonMaker.class),
    NUMBER("number", NumberJsonMaker.class),
    INTEGER("integer", IntegerJsonMaker.class),
    BOOLEAN("boolean", BooleanJsonMaker.class),
    NULL("null", StringJsonMaker.class),
    UNDEFINED("undefined", StringJsonMaker.class);

    private final String name;
    private final Class maker;

    JsonFieldType(String type, Class maker) {
        this.name = type;
        this.maker = maker;
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

    public BaseJsonMaker newJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold currentMold, JsonMoldContext context, Boolean isRequired) throws Exception {
        Constructor<BaseJsonMaker> c = ((Class<BaseJsonMaker>) maker).getConstructor(String.class, JsonNode.class, JsonMold.class, JsonMoldContext.class);

        return c.newInstance(schemaPath, schemaNode, currentMold, context);
    }
}
