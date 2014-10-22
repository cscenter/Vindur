package ru.csc.vindur.column;

import ru.csc.vindur.document.ValueType;

/**
 * @author Andrey Kokorev
 *         Created on 15.10.2014.
 */
public class ColumnHelper {

    public static Column getColumn(ValueType valueType) {
        Column column;
        switch (valueType) {
            case NUMERIC:
                column = new ColumnNumerics();
                break;
            case ENUM:
                column = new ColumnEnums();
                break;
            case STRING:
            default:
                column = new ColumnStrings();
        }
        return column;
    }
}
