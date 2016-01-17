package tts_ScheduleProgramEV;

import java.util.ArrayList;

/**
 * Created by marcusljx on 17/01/16.
 */
public class Pool extends ArrayList<Worker> {

    @Override
    public Pool clone() {
        Pool result = new Pool();
        for(Worker W : this) {
            result.add(W.clone());
        }
        return  result;
    }
}
