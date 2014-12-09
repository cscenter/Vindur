package ru.csc.vindur.executor;

import java.util.*;

import com.google.common.collect.Lists;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.ROBitArray;

/**
 * Created by Edgar on 27.11.2014.
 */
public class TinyExecutor implements Executor {
    @Override
    @SuppressWarnings({ "unchecked" })
    public BitArray execute(Query query, Engine engine)
    {
//        Map<String, Object> queryParts = new TreeMap<>(
//                (a, b) -> Integer.compare(engine.getStorages().get(a)
//                        .documentsCount(), engine.getStorages().get(b)
//                        .documentsCount()));
//        queryParts.putAll(query.getQueryParts());

        Comparator<String> compare =
                (a, b) -> Integer.compare(engine.getStorages().get(a)
                        .documentsCount(), engine.getStorages().get(b)
                        .documentsCount());

        List<String> reqs = Lists.newArrayList(query.getQueryParts().keySet());
        Collections.sort(reqs,compare);
        BitArray resultSet = null;

        for (String key : reqs)
        {
            ROBitArray stepResult = engine.getStorages().get(key)
                    .findSet(query.getQueryParts().get(key));
            if (resultSet == null)
            {
                resultSet = stepResult.copy();
                continue;
            }

            resultSet = resultSet.and(stepResult);
            if (resultSet.cardinality() == 0)
                       return null;

        }

        return resultSet;
    }

}
