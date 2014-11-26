package ru.csc.vindur.test;

import ru.csc.vindur.storage.StorageType;

import java.util.List;
import java.util.Map;

/**
 * Created by jetbrains on 07.11.2014.
 */
public interface TestBuilder
{
    /**
     * All storages for EngineConfig
     */
    Map<String, StorageType> getTypes();

    //

    /**
     * @return All storages names
     */
    List<String> getStorages();

    //

    /**
     * @param key Storage name
     * @return Available values for storage @key
     */
    Object[] getValues(String key);

    /**
     * @param key Storage name
     * @return Probability of existing @key storage in document
     */
    Double getProbability(String key);
}
