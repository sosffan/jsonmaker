package com.ffanonline.json.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.json.JsonSchemaModel;
import com.ffanonline.json.JsonSchemaModelContext;
import com.ffanonline.json.Keyword;
import com.ffanonline.json.constraint.ArrayBaseConstraint;
import com.ffanonline.json.creator.JsonDataCreator;
import org.apache.commons.lang3.RandomUtils;

public class ArrayJsonGenerator extends BaseJsonGenerator {

    private final JsonSchemaModel itemSchema;
    private final ArrayBaseConstraint constraint;

    public ArrayJsonGenerator(String schemaPath, JsonNode schemaNode, JsonSchemaModel currentJsonSchemaModel, JsonSchemaModelContext context) throws Exception {
        super(schemaPath, schemaNode, currentJsonSchemaModel, context);

        JsonNode minItemsNode = schemaNode.get(Keyword.MIN_ITEMS.getName());
        JsonNode maxItemsNode = schemaNode.get(Keyword.MAX_ITEMS.getName());

        int minItems = minItemsNode == null ? 0 : minItemsNode.intValue();
        int maxItems = maxItemsNode == null ? -1 : maxItemsNode.intValue();

        constraint = new ArrayBaseConstraint(minItems, maxItems, getRequired(), getNullable());

        JsonNode itemsNode = schemaNode.get(Keyword.ITEMS.getName());

        // isRequired in below JsonSchemaModel should be false?
        this.itemSchema = new JsonSchemaModel(context, schemaPath + "[]", itemsNode, currentJsonSchemaModel, true);  // should this be the same as isRequired.
        this.itemSchema.initialize();
    }

    @Override
    public JsonNode create(JsonDataCreator creator, JsonNode originalValue) throws Exception {

        ObjectNode node = getContext().getMapper().createObjectNode();
        ArrayNode arrayNode;
        if (getFieldName() != null) {
            arrayNode = node.putArray(getFieldName());
        } else {
            throw new Exception("No Field Name for this Array Object.");
            // arrayNode = getContext().getMapper().createArrayNode();
        }

        int count = 0;
        if (constraint.getMaxItems() < 0) {
            count = RandomUtils.nextInt(constraint.getMinItems(), getContext().getMaxItems());
        } else {
            count = RandomUtils.nextInt(constraint.getMinItems(), constraint.getMaxItems());
        }
        for (int i = 0; i < count; i++) {
            arrayNode.add(itemSchema.buildJson(creator));
        }

        return node;
    }
}
