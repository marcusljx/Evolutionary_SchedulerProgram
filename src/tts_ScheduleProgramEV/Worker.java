package tts_ScheduleProgramEV;

/**
 * Created by xjlm on 1/14/16.
 */
public class Worker {

    public enum Gender {
        M, F
    }

    public String[] DAY = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private String name;
    private Gender gender;
    private boolean[] availableDays = new boolean[7];
    private boolean[] workingDays = new boolean[7];

    public Worker(String entry) {
        int bracePos = entry.indexOf("(");
        name = entry.substring(0, bracePos);

        gender = (entry.contains("(M)")) ? Gender.M : Gender.F;

        for(int i=0; i<DAY.length; i++) {
            availableDays[i] = (entry.contains(DAY[i]));
        }
    }

    public Worker(String newName, Gender newGender, boolean[] newAvailableDays, boolean[] newWorkingDays) {
        name = newName;
        gender = newGender;
        availableDays = newAvailableDays.clone();
        workingDays = newWorkingDays.clone();
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
        return name + "\t(" + gender + ") \t[ " + booleanArr2String(availableDays) + "] -- " + booleanArr2String(workingDays);
    }

    //------------- getters
    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public boolean[] getAvailableDays() {
        return availableDays;
    }

    public boolean[] getWorkingDays() {
        return workingDays;
    }

    public boolean isHappy() {
        for(int i=0; i<workingDays.length; i++) {
            if( (workingDays[i]) && (!availableDays[i]) ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Worker clone() {
        return new Worker(name, gender, availableDays, workingDays);
    }

    //------------- setters
    public void setWorkingDay(int day) {
        workingDays[day] = true;
    }
}
