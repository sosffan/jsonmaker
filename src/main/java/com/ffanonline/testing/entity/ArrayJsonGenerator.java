package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.constraints.ArrayBaseConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;
import org.apache.commons.lang3.RandomUtils;

public class ArrayJsonGenerator extends BaseJsonGenerator {

    private final JsonMold itemSchema;
    private final ArrayBaseConstraint constraint;

    public ArrayJsonGenerator(String schemaPath, JsonNode schemaNode, JsonMold currentJsonMold, JsonMoldContext context) throws Exception {
        super(schemaPath, schemaNode, currentJsonMold, context);

        JsonNode minItemsNode = schemaNode.get(Keyword.MIN_ITEMS.getName());
        JsonNode maxItemsNode = schemaNode.get(Keyword.MAX_ITEMS.getName());

        int minItems = minItemsNode == null ? 0 : minItemsNode.intValue();
        int maxItems = maxItemsNode == null ? -1 : maxItemsNode.intValue();

        constraint = new ArrayBaseConstraint(minItems, maxItems, getRequired(), getNullable());

        JsonNode itemsNode = schemaNode.get(Keyword.ITEMS.getName());

        // isRequired in below JsonMold should be false?
        this.itemSchema = new JsonMold(context, schemaPath + "[]", itemsNode, currentJsonMold, true);  // should this be the same as isRequired.
//        this.itemSchema = new JsonSchema(context, null, itemsNode, currentJsonMold);
        this.itemSchema.initialize();
    }

    @Override
    public JsonNode create(JsonDataCreator creator, int operationType, String jsonPath) throws Exception {
        if (jsonPath != null && jsonPath.equals(getSchemaPath())) {
            getContext().markAsTraversed(jsonPath);

            switch (operationType) {
                case 1:
                    if (!getRequired()) return null;
                    break;
                case 2:
                    if (getNullable()) return generateNullNode();
                    break;
            }
        }

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
            arrayNode.add(itemSchema.buildJson(creator, operationType, jsonPath));
        }

        return node;
    }
}
