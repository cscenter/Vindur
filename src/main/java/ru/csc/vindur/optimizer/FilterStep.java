package ru.csc.vindur.optimizer;

import ru.csc.vindur.Engine;
import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by Edgar on 03.11.14.
 */
public class FilterStep implements Step {
    private Request.RequestPart requestPart;
    private static Plan plan; // todo а static ли?
    private static Engine engine;

    public FilterStep(Request.RequestPart requestPart, Plan plan, EngineConfig config) {
        this.requestPart = requestPart;
        this.plan = plan;
        engine = new Engine(config);
    }

    @Override
    public BitSet perform(BitSet bitSet) {
        List<Integer> idList = bitSet.toIntList();
        for (int docId : idList) {
            throw new NotImplementedException();
            /*if (requestPart.isExact()) {

            } else {

            }*/
            //todo: manual check here!
        }
        //todo perform step and update current result size for plan
        return null;
    }
}
