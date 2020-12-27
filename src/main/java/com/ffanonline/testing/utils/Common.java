package com.ffanonline.testing.utils;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;

public class Common {

    public static String getFieldNameFromJsonPath(String jsonPath) {
        if (jsonPath == null || !jsonPath.contains("/") || jsonPath.endsWith("[]")) return null;
        return jsonPath.substring(jsonPath.lastIndexOf("/") + 1);
    }

    public static Boolean isUnderArray(String jsonPath) {
        String[] subPathArray = jsonPath.split("/");
        for (int i = 0 ; i < subPathArray.length-1;i++) {
            if (subPathArray[i].endsWith("[]")) {
                return true;
            }
        }
        return false;
    }

    public static JsonNode getParentNode(JsonNode rootNode, String path) {
        //If it is any properties that under array, only the first one would be updated. so will just select the first array item.
        if (Common.isUnderArray(path)) {
            path = path.replace("[]", "/0");
        }

        JsonPointer pointer = JsonPointer.compile(path);

        if (null == pointer.head()) { return null;} // Skip root element.
        return rootNode.at(pointer.head());
    }
}
