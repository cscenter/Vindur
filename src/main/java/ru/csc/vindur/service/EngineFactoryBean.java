package ru.csc.vindur.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import ru.csc.vindur.Engine;
import ru.csc.vindur.executor.Executor;
import ru.csc.vindur.storage.Storage;

import java.util.Map;

/**
 * @author Andrey Kokorev
 *         Created on 23.04.2015.
 */
public class EngineFactoryBean implements FactoryBean<Engine>
{
    private Executor executor;
    private Map<String, Storage> storageMap;

    @Autowired
    public void setExecutor(Executor executor)
    {
        this.executor = executor;
    }

    @Autowired
    public void setStorageMap(Map<String, Storage> storageMap)
    {
        this.storageMap = storageMap;
    }

    @Override
    public Engine getObject() throws Exception
    {
        Engine.Builder builder = Engine.build();
        builder.executor(executor);
        for(Map.Entry<String, Storage> entry : storageMap.entrySet())
        {
            builder.storage(entry.getKey(), entry.getValue());
        }
        return builder.init();
    }

    @Override
    public Class<?> getObjectType()
    {
        return Engine.class;
    }

    @Override
    public boolean isSingleton()
    {
        return false;
    }
}
