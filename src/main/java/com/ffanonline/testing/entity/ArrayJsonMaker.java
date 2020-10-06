package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.JsonMold;
import com.ffanonline.testing.JsonMoldContext;
import com.ffanonline.testing.Keyword;
import com.ffanonline.testing.constraints.ArrayConstraint;
import com.ffanonline.testing.creator.JsonDataCreator;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class ArrayJsonMaker extends BaseJsonMaker {

    private final JsonMold itemSchema;
    private ArrayConstraint constraint;

    public ArrayJsonMaker(String schemaPath, JsonNode schemaNode, JsonMold parentSchema, JsonMoldContext context) throws Exception {
        super(schemaPath, schemaNode, parentSchema, context);

        JsonNode minItemsNode = schemaNode.get(Keyword.MIN_ITEMS.getName());
        JsonNode maxItemsNode = schemaNode.get(Keyword.MAX_ITEMS.getName());

        int minItems = minItemsNode == null ? 0 : minItemsNode.intValue();
        int maxItems = maxItemsNode == null ? -1 : maxItemsNode.intValue();

        constraint = new ArrayConstraint(minItems, maxItems);


        JsonNode itemsNode = schemaNode.get(Keyword.ITEMS.getName());
        this.itemSchema = new JsonMold(context, schemaPath + "[]", itemsNode, parentSchema);
//        this.itemSchema = new JsonSchema(context, null, itemsNode, parentSchema);
        this.itemSchema.initialize();
    }

    @Override
    public Object create(JsonDataCreator creator) throws Exception {
        int count = 0;
        if (constraint.getMaxItems() < 0) {
            count = RandomUtils.nextInt(constraint.getMinItems(), getContext().getMaxItems());
        } else {
            count = RandomUtils.nextInt(constraint.getMinItems(), constraint.getMaxItems());
        }
        List<JsonNode> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(itemSchema.assembleJson(creator, null));
        }

        return list;
    }
}
