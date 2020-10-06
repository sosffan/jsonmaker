package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffanonline.testing.creator.RandomJsonCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicTest {
    private static JsonMoldFactory factory;
    private static ObjectMapper mappe = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(BasicTest.class);


    @BeforeAll
    public static void setup() {
        factory = JsonMoldFactory.getInstance(SpecVersion.VersionFlag.V4);
    }

    @Test
    public void test01() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/schema03.jsd");

        JsonMold schema = JsonMoldFactory.getInstance(SpecVersion.VersionFlag.V4).getJsonMold(inputStream).initialize();
        //JsonNode node =schema.assembleJson(new RandomJsonCreator(), null);
        String nodeString = schema.assembleJsonString(new RandomJsonCreator(), null);


        System.out.println(nodeString);
    }

    @Test
    public void testGenerateObjectJsonWithProperties() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/objectWithPropertiesSchema.jsd");
        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        JsonNode nodeResult = schema.assembleJson(new RandomJsonCreator(), null);
        logger.info(mappe.writeValueAsString(nodeResult));

        com.github.fge.jsonschema.main.JsonSchema jsonSchema = (com.github.fge.jsonschema.main.JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schema.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

    @Test
    public void testBasicTypes() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/basicTypes.jsd");
        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        JsonNode nodeResult = schema.assembleJson(new RandomJsonCreator(), null);
        logger.info(mappe.writeValueAsString(nodeResult));

        com.github.fge.jsonschema.main.JsonSchema jsonSchema = (com.github.fge.jsonschema.main.JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schema.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

    @Test
    public void testArrayField() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/arrayType.jsd");
        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        JsonNode nodeResult = schema.assembleJson(new RandomJsonCreator(), null);
        logger.info(mappe.writeValueAsString(nodeResult));

        com.github.fge.jsonschema.main.JsonSchema jsonSchema = (com.github.fge.jsonschema.main.JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schema.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }
}
