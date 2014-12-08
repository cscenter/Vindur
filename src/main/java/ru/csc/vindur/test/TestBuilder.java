package ru.csc.vindur.test;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import ru.csc.vindur.Engine;
import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.executor.Executor;
import ru.csc.vindur.storage.StorageHelper;
import ru.csc.vindur.storage.StorageType;

/**
 * Created by jetbrains on 07.11.2014.
 */
public interface TestBuilder {
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
     * @param key
     *            Storage name
     * @return Available values for storage @key
     */
    Object[] getValues(String key);

    /**
     * @param key
     *            Storage name
     * @return Probability of existing @key storage in document
     */
    Double getProbability(String key);

    default Engine buildEngine(Executor executor)
    {
      Engine.Builder b = Engine.build().executor(executor);
      for (String s : getStorages())
      {
          b.storage(s, StorageHelper.getColumn(getTypes().get(s)));
      }
      return b.init();
    }
}
