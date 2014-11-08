package ru.csc.vindur.optimizer;

import ru.csc.vindur.Engine;
import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Edgar on 03.11.14.
 */
public class PatternStep implements Step {
    private Request.RequestPart requestPart;
    private static Plan plan; // todo а static ли?
    private static Engine engine;

    public PatternStep(Request.RequestPart requestPart, Plan plan, EngineConfig config){
        this.requestPart = requestPart;
        PatternStep.plan = plan;
        engine = new Engine(config);
    }

    @Override
    public BitSet perform(BitSet bitSet) {
        BitSet resultSet = bitSet.and(engine.executeRequestPart(this.requestPart));
        plan.setCurrentResultSize(new AtomicInteger(resultSet.cardinality()));
        return resultSet;
    }
}
