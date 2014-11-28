package ru.csc.vindur.optimizer;

import java.util.concurrent.ConcurrentMap;

import ru.csc.vindur.Request;
import ru.csc.vindur.storage.StorageBase;

/**
 * Created by Edgar on 26.10.14.
 */
public interface Optimizer
{
    /**
     * @param request
     * @param engine
     * @return
     */
    public Plan generatePlan(Request request, @SuppressWarnings("rawtypes") ConcurrentMap<String, StorageBase> storages);

    public void updatePlan(Plan plan, int currentResultSize);
}
