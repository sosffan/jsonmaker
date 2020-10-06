package com.ffanonline.testing.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ffanonline.testing.creator.JsonDataCreator;

public interface JsonMaker {

    public Object create(JsonDataCreator creator) throws Exception;

}
