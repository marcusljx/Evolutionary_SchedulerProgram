package tts_ScheduleProgramEV;

import java.util.Comparator;
import java.util.Random;
import java.util.Vector;

/**
 * Created by xjlm on 1/14/16.
 */
public class SolutionInstance implements Comparable<SolutionInstance> {
    Random RNG = new Random(System.nanoTime());

    private Pool WHOLE_POOL = null;
    private Pool HIRE_POOL = new Pool();
    private int HIRE_MAX;
    private int SLOTS_WEEKDAY;
    private int SLOTS_WEEKEND;

    public SolutionInstance(int weekdaySlots, int weekendSlots, int maximumHires, Pool pool) {
        HIRE_MAX = maximumHires;
        SLOTS_WEEKDAY = weekdaySlots;
        SLOTS_WEEKEND = weekendSlots;
        WHOLE_POOL = pool.clone();
    }



    public void setWholePool(Vector<Worker> wholePool) {
        for(Worker W : wholePool) {
            Worker newW = W.clone();
            WHOLE_POOL.add(newW);
        }
    }

    public void setHirePool(Pool hirePool) {
        HIRE_POOL = hirePool.clone();
    }

    public void repickHirePool() {
        HIRE_POOL.clear();
        Worker toHire;
        boolean gotMale = false;
        while(HIRE_POOL.size() < HIRE_MAX) {
            Worker.Gender genderToPick = (gotMale) ? Worker.Gender.F : Worker.Gender.M;
            do {
                toHire = WHOLE_POOL.get(RNG.nextInt(WHOLE_POOL.size()));
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
                        toAdd = HIRE_POOL.get(RNG.nextInt(HIRE_POOL.size()));
                    } while((toAdd.isWorking(i)) ||  (toAdd.getGender() != genderToPick));
                } else {
                    do {
                        toAdd = HIRE_POOL.get(RNG.nextInt(HIRE_POOL.size()));
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

    public void debug_HirePool() {
        for(Worker W : HIRE_POOL) {
            System.out.println(W);
        }
    }

    @Override
    public SolutionInstance clone() {
        SolutionInstance SI= new SolutionInstance(SLOTS_WEEKDAY, SLOTS_WEEKEND, HIRE_MAX, WHOLE_POOL);
        SI.setHirePool(HIRE_POOL);

        return SI;
    }

    public String getCodeHash() {
        String result = "";
        for(Worker W : HIRE_POOL) {
            result += W.getName().substring(0,1);
        }
        return result;
    }

    public double getFitness() {
        double fitness;

        // All days must have 1Male & 1F (MAJOR)
        int MF_SCORE = 1;
        boolean[] maleWorkers = new boolean[7];
        boolean[] femaleWorkers = new boolean[7];

        int UNHAPPY = 0;


        for(Worker W : HIRE_POOL) {
            // Evaluate total unhappy worker-day slots
            for(int i=0; i<7; i++) {
                if(W.isWorking(i)) {
                    UNHAPPY += (W.getAvailableDays()[i]) ? 0 : 1;   // add 1 if worker is working on a day he is off
                }
            }
        }

//        System.out.println("UNHAPPY = " + UNHAPPY);
        // overall score based on all parameters
        fitness =  -UNHAPPY;

        return fitness;
    }

    // Mutator
    public void mutate_swapSlots(int n) { //swap a slot between two workers of same gender n times
        for(int i=0; i<n; i++) {
            Worker.Gender genderToPick = (RNG.nextBoolean()) ? Worker.Gender.M : Worker.Gender.F;
            Vector<Worker> swapping = new Vector<>();

            Worker workerToPick;
            while(swapping.size()<2) {
                do {
                    workerToPick = HIRE_POOL.get(RNG.nextInt(HIRE_POOL.size()));
                } while ((workerToPick.getGender() != genderToPick) || (swapping.contains(workerToPick)));
                swapping.add(workerToPick);
            }
            Worker swap1 = swapping.get(0);
            Worker swap2 = swapping.get(1);

            // find two days where shifts can be swapped (maintains MF condition)

            int day1;
            int day2;

            boolean TL;
            boolean TR;
            boolean BL;
            boolean BR;

            int reps = 0;
            do {
                day1 = RNG.nextInt(7);
                day2 = RNG.nextInt(7);

                TL = swap1.isWorking(day1);
                TR = swap1.isWorking(day2);
                BL = swap2.isWorking(day1);
                BR = swap2.isWorking(day2);

                reps++;

            } while(((day1==day2) || (TL==TR) || (BL==BR) || (TL==BL) || (TR==BR)) && (reps < 100));

            //debug
//            System.out.println("GenderToSwap = "+genderToPick);
//            System.out.println("day1 = "+day1+"\nday2 = "+day2);

            // swap duty days
            swap1.flipWorkingDay(day1);
            swap1.flipWorkingDay(day2);
            swap2.flipWorkingDay(day1);
            swap2.flipWorkingDay(day2);
        }
    }

    public void mutate_giveShift() {    // for balancing shifts
        Worker.Gender genderToPick = (RNG.nextBoolean()) ? Worker.Gender.M : Worker.Gender.F;
        Vector<Worker> swapping = new Vector<>();

        Worker workerToPick;
        while(swapping.size()<2) {
            do {
                workerToPick = HIRE_POOL.get(RNG.nextInt(HIRE_POOL.size()));
            } while ((workerToPick.getGender() != genderToPick) || (swapping.contains(workerToPick)));
            swapping.add(workerToPick);
        }

        Worker less;
        Worker more;

        // find who has unbalanced number of shifts
        if(swapping.get(0).getNumberOfWorkingDays() < swapping.get(1).getNumberOfWorkingDays()) {
            less = swapping.get(0);
            more = swapping.get(1);
        } else if(swapping.get(0).getNumberOfWorkingDays() > swapping.get(1).getNumberOfWorkingDays()){
            less = swapping.get(1);
            more = swapping.get(0);
        } else {
            less = null;
            more = null;
        }

        // balance it out
        if((less!=null)&&(more!=null)) {
            int dayToPick;
            int rep = 0;
            do {
                dayToPick = RNG.nextInt(7);
                rep++;
            } while(!( (more.isWorking(dayToPick)) && (!less.isWorking(dayToPick)) ) && rep < 10);

            less.flipWorkingDay(dayToPick);
            more.flipWorkingDay(dayToPick);
        }

    }

    // Crossover
    public void crossover_swapWorker() {    // swaps out one worker from HIRE_POOL with a different one in WHOLE_POOL, keeping the working days
        // pick a random worker in HIRE_POOL
        Worker toRemove = HIRE_POOL.get(RNG.nextInt(HIRE_POOL.size()));

        // pick a random worker in WHOLE_POOL that is not already in HIRE_POOL
        Worker toAdd;
        do {
            toAdd = WHOLE_POOL.get(RNG.nextInt(WHOLE_POOL.size()));
        } while(HIRE_POOL.contains(toAdd) || (toAdd.getGender()!=toRemove.getGender()));
        toAdd.setWorkingDays(toRemove.getWorkingDays());

        HIRE_POOL.remove(toRemove);
        HIRE_POOL.add(toAdd);
    }

    @Override
    public int compareTo(SolutionInstance o) {
        if(getFitness() < o.getFitness()) return -1;
        if(getFitness() > o.getFitness()) return 1;

        return 0;
    }
}
