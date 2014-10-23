package ru.csc.vindur.column;

import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.ValueType;

/**
 * @author Andrey Kokorev
 *         Created on 15.10.2014.
 */
public class ColumnHelper {

    public static Column getColumn(ValueType valueType, BitSetFabric bitSetFabric) {
        Column column;
        switch (valueType) {
            case NUMERIC:
                column = new ColumnNumerics(bitSetFabric);
                break;
            case ENUM:
                column = new ColumnEnums(bitSetFabric);
                break;
            case STRING:
            default:
                column = new ColumnStrings(bitSetFabric);
        }
        return column;
    }
}
