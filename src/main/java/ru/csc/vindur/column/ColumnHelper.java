package ru.csc.vindur.column;

import ru.csc.vindur.document.ValueType;

/**
 * @author Andrey Kokorev
 *         Created on 15.10.2014.
 */
public class ColumnHelper {
    public static final int DEFAULT_VOLUME = 100;

    public static IColumn getColumn(ValueType valueType) {
        return getColumn(valueType, DEFAULT_VOLUME);
    }

    public static IColumn getColumn(ValueType valueType, int volume) {
        IColumn column;
        switch (valueType) {
            case NUMERIC:
                column = new ColumnNumerics(volume);
                break;
            case ENUM:
                column = new ColumnEnums(volume);
                break;
            case STRING:
            default:
                column = new ColumnStrings(volume);
        }
        return column;
    }
}
