package ru.csc.vindur.physics;

import ru.csc.vindur.IndexNumerics;
import ru.csc.vindur.entity.Value;

import java.math.BigDecimal;

/**
 * @author Andrey Kokorev
 *         Created on 27.09.2014.
 */
public class DimensionConverter implements INumericsConverter{
    @Override
    public BigDecimal convertToDecimal(Value value) {
        return new BigDecimal("0"); //todo: logic
    }
}
