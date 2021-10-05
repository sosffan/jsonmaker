package com.ffanonline.json;

import com.ffanonline.json.constraint.StringBaseConstraint;
import com.ffanonline.json.creator.MinorChangeJsonCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class JsonCreatorUnitTest {

    Logger logger  = LoggerFactory.getLogger(JsonCreatorUnitTest.class);
    @Test
    public void testMinorChangeMethod_NormalString01() {
        MinorChangeJsonCreator creator = new MinorChangeJsonCreator();
        String originalValue = "abcx";
        String value = creator.generateStringField(new StringBaseConstraint(1,2,null, null, false, false)
        , "test", "testpath", originalValue);;
        logger.info("originalValue: " + originalValue);
        logger.info("newValue: " + value);

        Assertions.assertNotEquals(originalValue, value);
        Assertions.assertEquals(originalValue.substring(0, originalValue.length()-2), value.substring(0, value.length()-2));
    }

    @Test
    public void testMinorChangeMethod_NormalString02() {
        MinorChangeJsonCreator creator = new MinorChangeJsonCreator();
        String originalValue = "abcz";
        String value = creator.generateStringField(new StringBaseConstraint(1,2,null, null, false, false)
                , "test", "testpath", originalValue);;
        logger.info("originalValue: " + originalValue);
        logger.info("newValue: " + value);

        Assertions.assertNotEquals(originalValue, value);
        Assertions.assertEquals(originalValue.substring(0, originalValue.length()-2), value.substring(0, value.length()-2));
    }

    @Test
    public void testMinorChangeMethod_EnumString() {
        MinorChangeJsonCreator creator = new MinorChangeJsonCreator();
        String originalValue = "abcz";
        String value1 = "ABC";
        String value3 = "XYZ";
        Set<String> enumSet = new HashSet<>();
        enumSet.add(value1);
        enumSet.add(originalValue);
        enumSet.add(value3);

        for (int i=0;i<5;i++) {
            String value = creator.generateStringField(new StringBaseConstraint(1, 2, null, enumSet, false, false)
                    , "test", "testpath", originalValue);
            ;
            logger.info("originalValue: " + originalValue);
            logger.info("newValue: " + value);

            Assertions.assertTrue(value == value1 || value == value3);
        }
    }

    @Test
    public void testMinorChangeMethod_PatternString() {
        MinorChangeJsonCreator creator = new MinorChangeJsonCreator();
        String pattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        String originalValue = "test@google.com";
        String value = creator.generateStringField(new StringBaseConstraint(1,2,pattern, null, false, false)
                , "test", "testpath", originalValue);;
        logger.info("originalValue: " + originalValue);
        logger.info("newValue: " + value);

        Assertions.assertNotEquals(originalValue, value);
        Assertions.assertEquals(originalValue.substring(0, originalValue.length()-1), value.substring(0, value.length()-1));

    }
}
