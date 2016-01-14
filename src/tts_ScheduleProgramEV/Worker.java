package tts_ScheduleProgramEV;

/**
 * Created by xjlm on 1/14/16.
 */
public class Worker {

    public enum Gender {
        M, F
    }

    public String[] DAY = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private String w_name;
    private Gender w_gender;
    private boolean[] w_availableDays = new boolean[7];
    private boolean[] w_workingDays = new boolean[7];

    public Worker(String entry) {
        int bracePos = entry.indexOf("(");
        w_name = entry.substring(0, bracePos);

        w_gender = (entry.contains("(M)")) ? Gender.M : Gender.F;

        for(int i=0; i<DAY.length; i++) {
            w_availableDays[i] = (entry.contains(DAY[i]));
        }
    }

    public String booleanArr2String(boolean[] arr) {
        String result = "";
        for(boolean b : arr) {
                result += b ? "1 " : "0 ";
        }
        return result;
    }

    @Override
    public String toString() {
        return w_name + "\t(" + w_gender + ") \t[ " + booleanArr2String(w_availableDays) + "] -- " + booleanArr2String(w_workingDays);
    }
}
