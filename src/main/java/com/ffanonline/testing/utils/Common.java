package com.ffanonline.testing.utils;

public class Common {

    public static String getFieldNameFromJsonPath(String jsonPath) {
        if (jsonPath == null || !jsonPath.contains("/") || jsonPath.endsWith("[]")) return null;
        return jsonPath.substring(jsonPath.lastIndexOf("/") + 1);
    }
}
