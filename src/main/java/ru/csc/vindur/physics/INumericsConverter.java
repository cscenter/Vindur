package ru.csc.vindur.physics;

import ru.csc.vindur.entity.Value;

import java.math.BigDecimal;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public interface INumericsConverter {
    BigDecimal convertToDecimal(Value value);
}
