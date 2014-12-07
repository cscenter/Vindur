package ru.csc.vindur.executor;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitSet;

/**
 * Created by Edgar on 26.10.14.
 */
public interface Executor {
    public BitSet execute(Query query, Engine engine);
}
