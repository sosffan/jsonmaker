package com.ffanonline.json;

import java.util.ArrayList;
import java.util.List;

public enum Keyword {
    TITLE("title"),
    DESCRIPTION("description"),
    PROPERTIES("properties"),

    ONE_OF("oneOf"),
    ANY_OF("anyOf"),

    TYPE("type"),
    REQUIRED("required"),
    ITEMS("items"),
    REF("$ref"),

    PATTERN("pattern"),
    MAX_LENGTH("maxLength"),
    MIN_LENGTH("minLength"),
    ENUM("enum"),
    MAX_ITEMS("maxItems"),
    MIN_ITEMS("minItems"),
    MINIMUM("minimum"),
    MAXIMUM("maximum"),
    MULTIPLE_OF("multipleOf"),

    FIELD("field");

    private final String name;

    Keyword(String name) {
        this.name = name;
    }

    public static List<Keyword> getKeyWords(SpecVersion.VersionFlag versionFlag) {
        List<Keyword> result = new ArrayList<>();
        for (Keyword a : values()) {
            // If matched version
            result.add(a);
        }
        return result;
    }

    public static Keyword getByValue(String value) {
        for (Keyword keyword : values()) {
            if (keyword.getName().equals(value)) {
                return keyword;
            }
        }
        return Keyword.FIELD;
    }

    public String getName() {
        return this.name;
    }
}
