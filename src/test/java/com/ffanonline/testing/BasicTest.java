package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffanonline.testing.creator.RandomJsonCreator;
import com.ffanonline.testing.utils.Common;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class BasicTest {
    private static final Logger logger = LoggerFactory.getLogger(BasicTest.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonGenFactory factory;

    @BeforeAll
    public static void setup() {
        factory = JsonGenFactory.getInstance(SpecVersion.VersionFlag.V4);
    }

    @Test
    void test01() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/schema00.jsd");

        JsonSchemaModel schema = JsonGenFactory.getInstance(SpecVersion.VersionFlag.V4).getJsonSchemaModel(inputStream).initialize();
        String nodeString = schema.generateJsonString(new RandomJsonCreator());

        logger.info(nodeString);
    }

    @Test
    void testGenerateObjectJsonWithProperties() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/objectWithPropertiesSchema.jsd");

        JsonSchemaModel schemaModel = factory.getJsonSchemaModel(inputStream).initialize();
        JsonNode nodeResult = schemaModel.buildJson(new RandomJsonCreator());
        logger.info(mapper.writeValueAsString(nodeResult));

        JsonSchema jsonSchema = (JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schemaModel.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

    @Test
    void testBasicTypes() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/basicTypes.jsd");

        JsonSchemaModel schemaModel = factory.getJsonSchemaModel(inputStream).initialize();
        JsonNode nodeResult = schemaModel.buildJson(new RandomJsonCreator());
        logger.info(mapper.writeValueAsString(nodeResult));

        JsonSchema jsonSchema = (JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schemaModel.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

    @Test
    void testArrayField() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/arrayType.jsd");
        JsonSchemaModel schemaModel = factory.getJsonSchemaModel(inputStream).initialize();
        JsonNode nodeResult = schemaModel.buildJson(new RandomJsonCreator());
        logger.info(mapper.writeValueAsString(nodeResult));

        JsonSchema jsonSchema = (JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schemaModel.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

    @Test
    void testIteratorOptionalField() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/requiredAndOptionalFields.jsd");

        JsonSchemaModel schema = factory.getJsonSchemaModel(inputStream).initialize();
        Map<String, JsonNode> nodeResults = schema.generateJsonBundle(new RandomJsonCreator(), 1);

        for (Map.Entry<String, JsonNode> item : nodeResults.entrySet()) {
            String path = item.getKey();
            JsonNode node = item.getValue();
            logger.info("\n For field:" + path);
            logger.info(mapper.writeValueAsString(node));

            if (Common.isUnderArray(path)) {
                path = path.replace("[]", "/0");
            }
            Assertions.assertTrue(node.at(path).isMissingNode());
        }
    }

    @Test
    void testIteratorOptionalField_Container() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/requiredAndOptionalFields_ContainerFIeld.jsd");

        JsonSchemaModel schema = factory.getJsonSchemaModel(inputStream).initialize();
        Map<String, JsonNode> nodeResults = schema.generateJsonBundle(new RandomJsonCreator(), 1);

        for (Map.Entry<String, JsonNode> item : nodeResults.entrySet()) {
            String path = item.getKey();
            JsonNode node = item.getValue();
            logger.info("\n For field:" + path);
            logger.info(mapper.writeValueAsString(node));

            if (Common.isUnderArray(path)) {
                path = path.replace("[]", "/0");
            }
            Assertions.assertTrue(node.at(path).isMissingNode());
        }
    }

    @Test
    void testIteratorNullField() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/nullableIterator.jsd");

        JsonSchemaModel schema = factory.getJsonSchemaModel(inputStream).initialize();
        Map<String, JsonNode> nodeResults = schema.generateJsonBundle(new RandomJsonCreator(), 2);

        for (Map.Entry<String, JsonNode> item : nodeResults.entrySet()) {
            String path = item.getKey();
            JsonNode node = item.getValue();
            logger.info("\n For field:" + path);
            logger.info(mapper.writeValueAsString(node));

            if (Common.isUnderArray(path)) {
                path = path.replace("[]", "/0");
            }
            Assertions.assertTrue(node.at(path).isNull());
        }
    }


    @Test
    void testGenerateJsonBundleThroughSampleForUnRequiredChecking() throws Exception {

        InputStream sample = this.getClass().getResourceAsStream("/json/sample01.json");
        InputStream schema = this.getClass().getResourceAsStream("/schemas/schema01.jsd");

        JsonSchemaModel schemaModel = factory.getJsonSchemaModel(schema).initialize();
        Map<String, JsonNode> result = schemaModel.generateJsonBundleForUnRequiredField(sample);

        for (Map.Entry<String, JsonNode> item : result.entrySet()) {
            String path = item.getKey();
            JsonNode node = item.getValue();
            logger.info("\n For field:" + path);
            logger.info(mapper.writeValueAsString(node));

            Assertions.assertTrue(node.at(path).isMissingNode());
        }

        Assertions.assertEquals(8, result.size());
    }

    @Test
    void testGenerateJsonBundleThroughSampleForNullChecking() throws Exception {
        InputStream sample = this.getClass().getResourceAsStream("/json/sample01.json");
        InputStream schema = this.getClass().getResourceAsStream("/schemas/schema01.jsd");

        JsonSchemaModel schemaModel = factory.getJsonSchemaModel(schema).initialize();
        Map<String, JsonNode> result = schemaModel.generateJsonBundleForNullField(sample);

        for (Map.Entry<String, JsonNode> item : result.entrySet()) {
            String path = item.getKey();
            JsonNode node = item.getValue();
            logger.info("\n For field:" + path);
            logger.info(mapper.writeValueAsString(node));

            if (Common.isUnderArray(path)) {
                path = path.replace("[]", "/0");
            }

            Assertions.assertTrue(node.at(path).isNull());
        }

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void testGenerateJsonBundleWithSampleDataForField_BasedOnSampleJson() throws IOException {
        InputStream sampleStream = this.getClass().getResourceAsStream("/json/sample01.json");
        InputStream dataStream = this.getClass().getResourceAsStream("/FieldsConfig.json");

        JsonTemplateModel templateModel = factory.getJsonTemplateModel(sampleStream);
        Map<String, JsonNode> result = templateModel.generateJsonBundleBasedOnData(dataStream);

        for (Map.Entry<String, JsonNode> item : result.entrySet()) {
            String path = item.getKey();
            JsonNode node = item.getValue();
            logger.info("\n For field:" + path);
            logger.info(mapper.writeValueAsString(node));
            Assertions.assertEquals(node.at(path.split("&")[2]).asText(), path.split("&")[1].replace("\"", ""));
        }
        Assertions.assertEquals(12, result.size());

    }
}
