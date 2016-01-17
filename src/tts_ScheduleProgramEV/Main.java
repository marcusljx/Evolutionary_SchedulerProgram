package tts_ScheduleProgramEV;

import com.sun.org.apache.bcel.internal.generic.POP;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

public class Main {
    public Random RNG = new Random(System.currentTimeMillis());
    public static int g_weekdaySlots;
    public static int g_weekendSlots;
    public static int g_necessaryFemaleSlots;
    public static int g_necessaryMaleSlots;
    public static int g_hiringLimit;

    public static Vector<Worker> readInputFile(String path) {
        String line;
        Vector<Worker> inputPool = new Vector<>();
        try {
            FileReader FR = new FileReader(path);
            BufferedReader BR = new BufferedReader(FR);

            BR.readLine();
            BR.readLine();

            while((line = BR.readLine()) != null) {
                Worker W = new Worker(line);
                inputPool.add(W);
            }
            BR.close();

        } catch(FileNotFoundException excep) {
            System.out.println("ERROR: File Not Found :: " + path + " ::");
        } catch (IOException e) {
            System.out.println("ERROR: Error while reading file.");
            e.printStackTrace();
        }
        return inputPool;
    }

    public static void printPopulation(Vector<SolutionInstance> Population) {
        int i=0;
        for(SolutionInstance SI : Population) {
            System.out.println("["+SI.getCodeHash()+"] \t("+SI.getFitness()+")" );
            i++;
        }
    }


    public static void runEvolution(Vector<SolutionInstance> Population, int generations) {
        Collections.sort(Population);
        printPopulation(Population);
    }



    public static void main(String[] args) {
	    String inputPath = args[0];

        g_weekdaySlots = Integer.parseInt(args[1]);
        g_weekendSlots = Integer.parseInt(args[2]);
        g_hiringLimit = Integer.parseInt(args[3]);

        Vector<Worker> POOL = readInputFile(inputPath);

        System.out.println("WHOLE_POOL");
        for(Worker W : POOL) {
            System.out.println(W);
        }
        System.out.println("==========================================");

        // init population
        Vector<SolutionInstance> Population = new Vector<>();
        for(int i=0; i<10; i++) {
            SolutionInstance inst = new SolutionInstance(g_weekdaySlots, g_weekendSlots, g_hiringLimit, POOL);
            inst.repickHirePool();
            inst.replanSchedule();

            Population.add(inst);
        }

        // RUN
        runEvolution(Population, 10);
    }
}
