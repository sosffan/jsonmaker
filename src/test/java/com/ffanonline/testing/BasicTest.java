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
    private static JsonGenFactory factory;
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void setup() {
        factory = JsonGenFactory.getInstance(SpecVersion.VersionFlag.V4);
    }

    @Test
    public void test01() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/schema00.jsd");


        JsonMold schema = JsonGenFactory.getInstance(SpecVersion.VersionFlag.V4).getJsonMold(inputStream).initialize();
        String nodeString = schema.generateJsonString(new RandomJsonCreator());

        logger.info(nodeString);
    }

    @Test
    public void testGenerateObjectJsonWithProperties() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/objectWithPropertiesSchema.jsd");
//        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/schema03.jsd");

        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        JsonNode nodeResult = schema.buildJson(new RandomJsonCreator());
        logger.info(mapper.writeValueAsString(nodeResult));

        com.github.fge.jsonschema.main.JsonSchema jsonSchema = (com.github.fge.jsonschema.main.JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schema.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

    @Test
    public void testBasicTypes() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/basicTypes.jsd");

        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        JsonNode nodeResult = schema.buildJson(new RandomJsonCreator());
        logger.info(mapper.writeValueAsString(nodeResult));

        com.github.fge.jsonschema.main.JsonSchema jsonSchema = (com.github.fge.jsonschema.main.JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schema.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

    @Test
    public void testArrayField() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/arrayType.jsd");
        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        JsonNode nodeResult = schema.buildJson(new RandomJsonCreator());
        logger.info(mapper.writeValueAsString(nodeResult));

        com.github.fge.jsonschema.main.JsonSchema jsonSchema = (com.github.fge.jsonschema.main.JsonSchemaFactory.newBuilder()).freeze().getJsonSchema(schema.getSchemaNode());
        Assertions.assertTrue(jsonSchema.validate(nodeResult).isSuccess());
    }

    @Test
    public void testIteratorOptionalField() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/requiredAndOptionalFields.jsd");

        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        Map<String, JsonNode> nodeResults = schema.generateJsonCollection(new RandomJsonCreator(), 1);
        System.out.println();

    }

    @Test
    public void testIteratorOptionalField_Container() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/requiredAndOptionalFields_ContainerFIeld.jsd");

        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        Map<String, JsonNode> nodeResults = schema.generateJsonCollection(new RandomJsonCreator(), 1);
        System.out.println();
    }

    @Test
    public void testIteratorNullField() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/schemas/nullableIterator.jsd");

        JsonMold schema = factory.getJsonMold(inputStream).initialize();
        Map<String, JsonNode> nodeResults = schema.generateJsonCollection(new RandomJsonCreator(), 2);
        System.out.println();

    }


    @Test
    public void testGenerateJsonCollectionThroughSampleForUnRequiredChecking() throws Exception {

        InputStream sample = this.getClass().getResourceAsStream("/json/sample01.json");
        InputStream schema = this.getClass().getResourceAsStream("/schemas/schema01.jsd");

        JsonMold jsonMode = factory.getJsonMold(schema).initialize();
        Map<String, JsonNode> result = jsonMode.generateJsonCollectionForUnRequiredField(sample);

        for (Map.Entry<String, JsonNode> item: result.entrySet()) {
            String path = item.getKey();
            JsonNode node = item.getValue();

            Assertions.assertTrue(node.at(path).isMissingNode());
        }

        Assertions.assertEquals(5, result.size());

    }
}
