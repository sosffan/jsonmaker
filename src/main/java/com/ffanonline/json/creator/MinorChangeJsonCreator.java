package com.ffanonline.json.creator;

import com.ffanonline.json.constraint.BaseConstraint;
import com.ffanonline.json.constraint.NumberBaseConstraint;
import com.ffanonline.json.constraint.StringBaseConstraint;
import com.github.curiousoddman.rgxgen.RgxGen;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;

public class MinorChangeJsonCreator implements JsonDataCreator {
    Logger logger = LoggerFactory.getLogger(MinorChangeJsonCreator.class);

    @Override
    public String generateStringField(StringBaseConstraint constraint, String fieldName, String fieldPath, String originalValue) {
        Set<String> enumValues = constraint.getEnumSet();
        if (enumValues != null) {
            String[] restValues = Arrays.stream(enumValues.toArray(new String[0])).filter(s -> !s.equals(originalValue)).toArray(String[]::new);
            int index = RandomUtils.nextInt(0, restValues.length);
            return restValues[index];
        }

        if (constraint.getPattern() == null || constraint.getPattern().isEmpty()) {
            char lastChar = originalValue.charAt(originalValue.length() - 1);
            StringBuilder stringBuilder = new StringBuilder(originalValue);
            char newLastChar;
            if (lastChar == 'z' || lastChar == 'Z' || lastChar == 9) {
                newLastChar = (char) (lastChar - 1);
            } else {
                newLastChar = (char) (lastChar + 1);
            }

            stringBuilder.replace(originalValue.length() - 1, originalValue.length(), Character.toString(newLastChar));
            return stringBuilder.toString();
        } else {
            RgxGen rgxGen = new RgxGen(constraint.getPattern());
            String vgxValue = rgxGen.generate();
//            Generex rgxGen = new Generex(constraint.getPattern());
//            String vgxValue = rgxGen.random();

            logger.info("rgxGen Value: " + vgxValue);
            String lastChar = vgxValue.substring(vgxValue.length() - 1);
            return originalValue.substring(0, originalValue.length() - 1) + lastChar;
        }

    }

    @Override
    public Boolean generateBooleanField(BaseConstraint constraint, String fieldName, String fieldPath, Boolean originalValue) {
        return !originalValue;
    }

    @Override
    public Double generateNumberField(NumberBaseConstraint constraint, String fieldName, String fieldPath, Double originalValue) {
        return null;
    }

    @Override
    public Long generateIntegerField(NumberBaseConstraint constraint, String fieldName, String fieldPath, Integer originalValue) {
        return null;
    }
}
