package ru.csc.vindur.transactions;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by edgar on 06.04.15.
 */
public class Transaction {
    private List<Operation> operations;

    public List<Operation> getOperations() {
        return operations;
    }

    public Transaction() {
        this.operations = new LinkedList<>();
    }
}
