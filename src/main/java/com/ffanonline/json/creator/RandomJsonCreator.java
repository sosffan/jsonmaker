package com.ffanonline.json.creator;

import com.ffanonline.json.constraint.BaseConstraint;
import com.ffanonline.json.constraint.NumberBaseConstraint;
import com.ffanonline.json.constraint.StringBaseConstraint;
import com.github.curiousoddman.rgxgen.RgxGen;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Set;

public class RandomJsonCreator implements JsonDataCreator {

    @Override
    public String generateStringField(StringBaseConstraint constraint, String fieldName, String fieldPath, String originalValue) {
        String pattern = constraint.getPattern();
        int maxLength = constraint.getMaxLength();
        int minLength = constraint.getMinLength();
        if (minLength == 0) {
            minLength++;
        }

        Set<String> enumValues = constraint.getEnumSet();

        if (enumValues != null) {
            int index = RandomUtils.nextInt(0, enumValues.size());
            return enumValues.toArray()[index].toString();
        }

        String result;

        if (maxLength <= 0) {
            maxLength = 100;
        }

        int count = RandomUtils.nextInt(minLength, maxLength+1);

        if (pattern != null) {
            RgxGen rgxGen = new RgxGen(pattern);
            result = rgxGen.generate();
            if (result.length() > maxLength) {
                result = result.substring(0, count);
            }
        } else {
            result = RandomStringUtils.randomAscii(count);
        }
        return result;
    }

    @Override
    public Boolean generateBooleanField(BaseConstraint constraint, String fieldName, String fieldPath, Boolean originalValue) {
        return RandomUtils.nextBoolean();
    }

    @Override
    public Double generateNumberField(NumberBaseConstraint constraint, String fieldName, String fieldPath, Double originalValue) {
        int maximum = constraint.getMaximum();
        int minimum = constraint.getMinimum();

        if (maximum < 0) {
            maximum = 99999999;
        }

        return RandomUtils.nextDouble(minimum, maximum);
    }

    @Override
    public Long generateIntegerField(NumberBaseConstraint constraint, String fieldName, String fieldPath, Integer originalValue) {
        int maximum = constraint.getMaximum();
        int minimum = constraint.getMinimum();
        if (maximum < 0) {
            maximum = 99999999;
        }

        return RandomUtils.nextLong(minimum, maximum + 1);
    }

}
