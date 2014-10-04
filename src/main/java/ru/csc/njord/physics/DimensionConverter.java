package ru.csc.njord.physics;

import ru.csc.njord.IndexNumerics;
import ru.csc.njord.entity.Value;

import java.math.BigDecimal;

/**
 * @author Andrey Kokorev
 *         Created on 27.09.2014.
 */
public class DimensionConverter implements IndexNumerics.NumberConverter {
    @Override
    public BigDecimal convertToDecimal(Value value) {
        return new BigDecimal("0"); //todo: logic
    }
}
