package com.ffanonline.json.utils;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class Common {

    static Logger logger = LoggerFactory.getLogger(Common.class);

    private Common() { throw new IllegalStateException("Utility class"); }

    public static String getFieldNameFromJsonPath(String jsonPath) {
        if (jsonPath == null || !jsonPath.contains("/") || jsonPath.endsWith("[]")) return null;
        return jsonPath.substring(jsonPath.lastIndexOf("/") + 1);
    }

    public static Boolean isUnderArray(String jsonPath) {
//        String[] subPathArray = jsonPath.split("/");
//        for (int i = 0 ; i < subPathArray.length-1;i++) {
//            if (subPathArray[i].endsWith("[]")) {
//                return true;
//            }
//        }
//        return false;

        return jsonPath.contains("[]");
    }

    public static JsonNode getParentNode(JsonNode rootNode, String path) {
        //If it is any properties that under array, only the first one would be updated. so will just select the first array item.
        if (Boolean.TRUE.equals(Common.isUnderArray(path))) {
            path = path.replace("[]", "/0");
        }

        JsonPointer pointer = JsonPointer.compile(path);

        if (null == pointer.head()) { return null;} // Skip root element.
        return rootNode.at(pointer.head());
    }

    public static InputStream getFileStreamFromProjectPath(String fileName) {
        InputStream inputStream = null;
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + File.separator + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            logger.info("File path --> " + filePath);

            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("File not exist --> " + filePath);
        }

        return inputStream;
    }

    public static Properties getPropertiesFromProjectPath(String fileName) {
        InputStream inputStream = getFileStreamFromProjectPath(fileName);
        if (inputStream == null) {return null;}

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

}
