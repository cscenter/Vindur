package ru.csc.vindur.transactions;

/**
 * Created by edgar on 06.04.15.
 */
public class Operation {
    public String type;
    public int docID;
    public String attribute;
    public Object value;

    public Operation(String type, int docID, String attribute, Object value) {
        this.type = type;
        this.docID = docID;
        this.attribute = attribute;
        this.value = value;
    }
}
