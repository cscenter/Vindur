package ru.csc.vindur.optimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;
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

    public void updatePlan(Plan plan, BitSet currentResult);
    

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static List<Step> requestPartsToSteps( Map<String, Object> requestParts,	ConcurrentMap<String, StorageBase> storages) {
		List<Step> steps = new ArrayList<>(requestParts.size());
        for (Map.Entry<String, Object> requestPart: requestParts.entrySet())
        {
        	steps.add(() -> storages.get(requestPart.getKey()).findSet(requestPart.getValue()));
        }
		return steps;
	}
}
