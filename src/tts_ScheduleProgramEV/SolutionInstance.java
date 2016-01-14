package tts_ScheduleProgramEV;

import java.util.Random;
import java.util.Vector;

/**
 * Created by xjlm on 1/14/16.
 */
public class SolutionInstance {
    Random RNG = new Random(System.currentTimeMillis());

    private Vector< Vector<Worker> > Schedule = new Vector<>();

    public SolutionInstance(int weekdaySlots, int weekendSlots, Vector<Worker> pool) {
        // generate random solution with only slot rules
        for(int i=0; i<7; i++) {    // fill weekdays
            int slots = (i<5) ? weekdaySlots : weekendSlots;
            Vector<Worker> day = new Vector<>();
            Worker toAdd;
            while(day.size() < slots) {
                do {
                    toAdd = pool.elementAt(RNG.nextInt(pool.size()));
                } while(day.contains(toAdd));
                day.add(toAdd);
            }
            Schedule.add(day);
        }
    }

    @Override
    public String toString() {
        String Output = "";

        for(int i=0; i<Worker.DAY.length; i++) {
            Output += Worker.DAY[i] + "  \t";
            for(Worker W : Schedule.elementAt(i)) {
                Output += W.getName() + ", ";
            }
            Output += "\n";
        }
        return Output;
    }
}
