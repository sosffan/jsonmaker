package com.ffanonline.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffanonline.testing.creator.RandomJsonCreator;
import com.ffanonline.testing.entity.OutcomeData;
import com.ffanonline.testing.utils.Common;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class GenerateJsonCollectionWithSampleTest {
    private static final Logger logger = LoggerFactory.getLogger(BasicTest.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonGenFactory factory;

    @BeforeAll
    public static void setup() {
        factory = JsonGenFactory.getInstance(SpecVersion.VersionFlag.V4);
    }

    @Test
    void testGenerateJsonBundleThroughSampleForUnRequiredChecking() throws Exception {

        InputStream sample = this.getClass().getResourceAsStream("/json/sample01.json");
        InputStream schema = this.getClass().getResourceAsStream("/schemas/schema01.jsd");

        JsonSchemaModel schemaModel = factory.getJsonSchemaModel(schema).initialize();
        Map<String, JsonNode> result = schemaModel.generateJsonCollectionForEachUnRequiredField(sample);

        for (Map.Entry<String, JsonNode> item : result.entrySet()) {
            String path = item.getKey();
            JsonNode node = item.getValue();
            logger.info("\n For field:" + path);
            logger.info(mapper.writeValueAsString(node));

            Assertions.assertTrue(node.at(path).isMissingNode());
        }

        Assertions.assertEquals(9, result.size()); // currently  /awards[] is not in result.
    }

    @Test
    void testGenerateJsonBundleThroughSampleForNullChecking() throws Exception {
        InputStream sample = this.getClass().getResourceAsStream("/json/sample01.json");
        InputStream schema = this.getClass().getResourceAsStream("/schemas/schema01.jsd");

        JsonSchemaModel schemaModel = factory.getJsonSchemaModel(schema).initialize();
        Map<String, JsonNode> result = schemaModel.generateJsonCollectionForEachNullField(sample);

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


//    @Test
//    void test001() throws Exception {
//        InputStream sampleStream = this.getClass().getResourceAsStream("/json/sample01.json");
//        InputStream modelStream = this.getClass().getResourceAsStream("/schemas/schema01.jsd");
//        JsonSchemaModel model = factory.getJsonSchemaModel(modelStream).initialize();
//
//        Map<String, JsonNode> result = model.generateJsonCollectionForEachFields(sampleStream, new RandomJsonCreator());
//
//        Assertions.assertEquals(11, result.size());
//    }
    @Test
    void test002() throws Exception {
        InputStream sampleStream = this.getClass().getResourceAsStream("/json/sample01.json");
        InputStream modelStream = this.getClass().getResourceAsStream("/schemas/schema01.jsd");
        JsonSchemaModel model = factory.getJsonSchemaModel(modelStream).initialize();

        Map<String, OutcomeData> result = model.generateJsonCollectionForEachFields(sampleStream, new RandomJsonCreator());

        Assertions.assertEquals(11, result.size());
    }

    @Test
    public void testValueArray() throws Exception {
        InputStream sampleStream = this.getClass().getResourceAsStream("/json/arrayType.json");
        InputStream modelStream = this.getClass().getResourceAsStream("/schemas/arrayType.jsd");
        JsonSchemaModel model = factory.getJsonSchemaModel(modelStream).initialize();

        Map<String, OutcomeData> result = model.generateJsonCollectionForEachFields(sampleStream, new RandomJsonCreator());
        Assertions.assertEquals(6, result.size());
    }

    @Test
    public void testValueArrayWithEmptyArray() throws Exception {
        InputStream sampleStream = this.getClass().getResourceAsStream("/json/arrayType_withEmptyArray.json");
        InputStream modelStream = this.getClass().getResourceAsStream("/schemas/arrayType.jsd");
        JsonSchemaModel model = factory.getJsonSchemaModel(modelStream).initialize();

        Map<String, OutcomeData> result = model.generateJsonCollectionForEachFields(sampleStream, new RandomJsonCreator());
        Assertions.assertEquals(6, result.size());
    }
}
