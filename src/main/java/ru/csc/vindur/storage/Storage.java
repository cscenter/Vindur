package ru.csc.vindur.storage;

import ru.csc.vindur.document.Value;

public interface Storage
{
    public long size();

    public void add(int docId, Value value);

    public long getComplexity();
}
