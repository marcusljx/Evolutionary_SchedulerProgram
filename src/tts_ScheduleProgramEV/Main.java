package tts_ScheduleProgramEV;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Main {
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

    public static void main(String[] args) {
	    String inputPath = args[0];

        g_weekdaySlots = Integer.parseInt(args[1]);
        g_weekendSlots = Integer.parseInt(args[2]);
        g_hiringLimit = Integer.parseInt(args[3]);

        Vector<Worker> POOL = readInputFile(inputPath);

        for(Worker W : POOL) {
            System.out.println(W);
        }
        System.out.println("==========================================");

        SolutionInstance inst = new SolutionInstance(g_weekdaySlots, g_weekendSlots, g_hiringLimit, POOL);
        inst.repickHirePool();
        inst.replanSchedule();

        System.out.println(inst);
        System.out.println("Score is : " + inst.getFitness());


        SolutionInstance SI2 = inst.clone();
        System.out.println(SI2);
        System.out.println("Score is : " + SI2.getFitness());
    }
}
