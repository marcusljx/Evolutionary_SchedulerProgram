package tts_ScheduleProgramEV;

import java.util.Random;
import java.util.Vector;

/**
 * Created by xjlm on 1/14/16.
 */
public class SolutionInstance {
    Random RNG = new Random(System.currentTimeMillis());

    private Vector<Worker> WHOLE_POOL = null;
    private Vector<Worker> HIRE_POOL = new Vector<>();
    private int HIRE_MAX;
    private int SLOTS_WEEKDAY;
    private int SLOTS_WEEKEND;

    public SolutionInstance(int weekdaySlots, int weekendSlots, int maximumHires, Vector<Worker> pool) {
        HIRE_MAX = maximumHires;
        SLOTS_WEEKDAY = weekdaySlots;
        SLOTS_WEEKEND = weekendSlots;
        WHOLE_POOL = (Vector<Worker>) pool.clone();
    }



    public void setWholePool(Vector<Worker> wholePool) {
        for(Worker W : wholePool) {
            Worker newW = W.clone();
            WHOLE_POOL.add(newW);
        }
    }

    public void setHirePool(Vector<Worker> hirePool) {
//        for(Worker W : hirePool) {
//            Worker newW = W.clone();
//            HIRE_POOL.add(newW);
//        }
        HIRE_POOL = hirePool;
    }

    public void repickHirePool() {
        HIRE_POOL.clear();
        Worker toHire;
        boolean gotMale = false;
        while(HIRE_POOL.size() < HIRE_MAX) {
            Worker.Gender genderToPick = (gotMale) ? Worker.Gender.F : Worker.Gender.M;
            do {
                toHire = WHOLE_POOL.elementAt(RNG.nextInt(WHOLE_POOL.size()));
            } while( (HIRE_POOL.contains(toHire)) || (toHire.getGender()!=genderToPick) );

            gotMale = (toHire.getGender() == Worker.Gender.M);
            HIRE_POOL.add(toHire);
        }
    }

    public void replanSchedule() {  // generate random solution with only slot and M/F rules
        // invalidate Hiring Pool Workers working day bits
        for(Worker W : HIRE_POOL) {
            W.clearWorkingDays();
        }

        // loop over days and set worker bits
        for(int i=0; i<7; i++) {    // fill weekdays
            int slots = (i<5) ? SLOTS_WEEKDAY : SLOTS_WEEKEND;
            Worker toAdd;

            int slotsFilled = 0;
            boolean gotMale = false;
            boolean gotFemale = false;

            while(slotsFilled < slots) {
                // pick worker
                if(gotMale ^ gotFemale) {   // if one is false while the other is true
                    Worker.Gender genderToPick = (gotFemale) ? Worker.Gender.M : Worker.Gender.F;
                    do {
                        toAdd = HIRE_POOL.elementAt(RNG.nextInt(HIRE_POOL.size()));
                    } while((toAdd.isWorking(i)) ||  (toAdd.getGender() != genderToPick));
                } else {
                    do {
                        toAdd = HIRE_POOL.elementAt(RNG.nextInt(HIRE_POOL.size()));
                    } while(toAdd.isWorking(i));
                }

                // set working day in worker
                toAdd.setWorkingDay(i);

                if(toAdd.getGender() == Worker.Gender.M) {
                    gotMale = true;
                } else {
                    gotFemale = true;
                }

                slotsFilled++;
            }
        }
    }

    @Override
    public String toString() {
        String Output = "";

        for(int i=0; i<Worker.DAY.length; i++) {
            Output += Worker.DAY[i] + "  \t";
            for(Worker W : HIRE_POOL) {
                if(W.isWorking(i)) {
                    Output += W.getName() + ", ";
                }
            }
            Output += "\n";
        }
        return Output;
    }

    @Override
    public SolutionInstance clone() {
        SolutionInstance SI= new SolutionInstance(SLOTS_WEEKDAY, SLOTS_WEEKEND, HIRE_MAX, WHOLE_POOL);
        SI.setHirePool(HIRE_POOL);

        return SI;
    }

    public double getFitness() {
        double fitness;

        // All days must have 1Male & 1F (MAJOR)
        int MF_SCORE = 1;
        boolean[] maleWorkers = new boolean[7];
        boolean[] femaleWorkers = new boolean[7];

        int UNHAPPY = 1;


        for(Worker W : HIRE_POOL) {
            System.out.println(W);

            // Evaluate total unhappy worker-day slots
            for(int i=0; i<7; i++) {
                if(W.isWorking(i)) {
                    UNHAPPY += (W.getAvailableDays()[i]) ? 0 : 1;   // add 1 if worker is working on a day he is off
                }
            }
        }

        System.out.println("UNHAPPY = " + UNHAPPY);
        // overall score based on all parameters
        fitness =  1 / (1 + Math.pow(UNHAPPY, 2));

        return fitness;
    }
}
