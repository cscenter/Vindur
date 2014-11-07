package ru.csc.vindur.test2;

import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.document.Value;

import java.util.List;
import java.util.Map;

/**
 * Created by jetbrains on 07.11.2014.
 */
public interface TestBuilder
{
    Map<String,StorageType> getTypes();

    List<String> getStorages();

    Value[] getValues(String key);
}
