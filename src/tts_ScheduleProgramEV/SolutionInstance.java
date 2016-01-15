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
    private int HIRED;
    private int SLOTS_WEEKDAY;
    private int SLOTS_WEEKEND;

    private Vector< Vector<Worker> > Schedule = new Vector<>();

    public SolutionInstance(int weekdaySlots, int weekendSlots, int maximumHires, Vector<Worker> pool) {
        HIRE_MAX = maximumHires;
        SLOTS_WEEKDAY = weekdaySlots;
        SLOTS_WEEKEND = weekendSlots;
        WHOLE_POOL = (Vector<Worker>) pool.clone();

        // pick random group for hire (up to maximum hires)
        repickHirePool();
        replanSchedule();
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

    public void replanSchedule() {
        // generate random solution with only slot and M/F rules
        for(int i=0; i<7; i++) {    // fill weekdays
            int slots = (i<5) ? SLOTS_WEEKDAY : SLOTS_WEEKEND;
            Vector<Worker> day = new Vector<>();
            Worker toAdd;

            boolean gotMale = false;
            while(day.size() < slots) {
                Worker.Gender genderToPick = (gotMale) ? Worker.Gender.F: Worker.Gender.M;
                do {
                    toAdd = HIRE_POOL.elementAt(RNG.nextInt(HIRE_POOL.size()));
                } while((day.contains(toAdd)) || (toAdd.getGender()!=genderToPick));

                // set working day in worker
                toAdd.setWorkingDay(i);
                toAdd.setWorking(true);
                day.add(toAdd);
                gotMale = (toAdd.getGender() == Worker.Gender.M);
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

    public double getFitness() {
        double fitness;

        // All days must have 1Male & 1F (MAJOR)
        int MF_SCORE = 1;
        boolean allDaysMF_ok = true;

        int HIRED = 0;
        int UNHAPPY = 0;

        for(Vector<Worker> day : Schedule) {
            boolean maleWorking = false;
            boolean femaleWorking = false;
            for(Worker W : day) {
                if(W.getGender() == Worker.Gender.M) {
                    maleWorking = true;
                } else if(W.getGender() == Worker.Gender.F) {
                    femaleWorking = true;
                }
            }
            if (!(maleWorking && femaleWorking)) {
                MF_SCORE = -1;
            }

            for(Worker W : day) {
                // Find total number of unhappy worker-days in this instance
                UNHAPPY += (!W.isHappy()) ? 1 : 0;
            }
        }

        System.out.println("UNHAPPY = " + UNHAPPY);
        // overall score based on all parameters
        fitness = MF_SCORE * ( 1 / (1 + Math.pow(UNHAPPY, 2)) );

        return fitness;
    }
}
