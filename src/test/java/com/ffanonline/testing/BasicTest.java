package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffanonline.testing.creator.RandomJsonCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

public class BasicTest {
    private static final Logger logger = LoggerFactory.getLogger(BasicTest.class);
    private static JsonMoldFactory factory;
    private static final ObjectMapper mappe = new ObjectMapper();

    @BeforeAll
    public static void setup() {
        factory = JsonMoldFactory.getInstance(SpecVersion.VersionFlag.V4);
    }

    @Test
    public void test01() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/schema00.jsd");


        JsonMold schema = JsonMoldFactory.getInstance(SpecVersion.VersionFlag.V4).getJsonMold(inputStream).initialize();
        String nodeString = schema.assembleJsonString(new RandomJsonCreator());

        logger.info(nodeString);
    }

    @Test
    public void testGenerateObjectJsonWithProperties() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/objectWithPropertiesSchema.jsd");
//        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/schema03.jsd");

        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        JsonNode nodeResult = schema.assembleJson(new RandomJsonCreator());
        logger.info(mappe.writeValueAsString(nodeResult));

        com.github.fge.jsonschema.main.JsonSchema jsonSchema = (com.github.fge.jsonschema.main.JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schema.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

    @Test
    public void testBasicTypes() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/basicTypes.jsd");

        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        JsonNode nodeResult = schema.assembleJson(new RandomJsonCreator());
        logger.info(mappe.writeValueAsString(nodeResult));

        com.github.fge.jsonschema.main.JsonSchema jsonSchema = (com.github.fge.jsonschema.main.JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schema.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

    @Test
    public void testArrayField() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/arrayType.jsd");
        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        JsonNode nodeResult = schema.assembleJson(new RandomJsonCreator());
        logger.info(mappe.writeValueAsString(nodeResult));

        com.github.fge.jsonschema.main.JsonSchema jsonSchema = (com.github.fge.jsonschema.main.JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schema.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

//    @Test
//    public void testType2OnlyRequiredFields() throws Exception {
//        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/requiredAndOptionalFields.jsd");
//
//        JsonMold schema = factory.getJsonMold(inputStream).initialize();
//        JsonNode nodeResult = schema.assembleJson(new RandomJsonCreator());
//        logger.info(mappe.writeValueAsString(nodeResult));
//
//        com.github.fge.jsonschema.main.JsonSchema jsonSchema = (com.github.fge.jsonschema.main.JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schema.getSchemaNode());
//        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
//
//        JsonNode propNode = nodeResult.get("prop");
//        Assertions.assertNotNull(propNode.get("arrayType"));
//        Assertions.assertNotNull(propNode.get("numberType"));
//        Assertions.assertNotNull(propNode.get("booleanType"));
//        Assertions.assertNotNull(propNode.get("integerType"));
//        Assertions.assertNotNull(propNode.get("stringType"));
//        Assertions.assertNotNull(propNode.get("objectType"));
//        Assertions.assertNotNull(propNode.get("objectType").get("A"));
//
//        Assertions.assertNull(propNode.get("arrayTypeOptional"));
//        Assertions.assertNull(propNode.get("numberTypeOptional"));
//        Assertions.assertNull(propNode.get("booleanTypeOptional"));
//        Assertions.assertNull(propNode.get("integerTypeOptional"));
//        Assertions.assertNull(propNode.get("stringTypeOptional"));
//        Assertions.assertNull(propNode.get("objectTypeOptional"));
//        Assertions.assertNull(propNode.get("objectType").get("B"));
//    }

    @Test
    public void testIteratorOptionalField() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/requiredAndOptionalFields.jsd");

        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        Map<String, JsonNode> nodeResults = schema.assembleJsonCollection(new RandomJsonCreator(), 1);
        System.out.println();

    }

    @Test
    public void testIteratorOptionalField_Container() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/requiredAndOptionalFields_ContainerFIeld.jsd");

        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        Map<String, JsonNode> nodeResults = schema.assembleJsonCollection(new RandomJsonCreator(), 1);
        System.out.println();
    }

    @Test
    public void testIteratorNullField() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/nullableIterator.jsd");

        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        Map<String, JsonNode> nodeResults = schema.assembleJsonCollection(new RandomJsonCreator(), 2);
        System.out.println();

    }
}
