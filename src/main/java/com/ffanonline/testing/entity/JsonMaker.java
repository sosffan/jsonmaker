package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.ffanonline.testing.creator.JsonDataCreator;

public interface JsonMaker {
    JsonNode create(JsonDataCreator creator) throws Exception;
}
