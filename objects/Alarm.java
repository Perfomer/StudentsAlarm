package objects;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;

import managers.Algorithms;
import managers.TimeHandler;
import managers.receivers.AlarmBroadcastReceiver;

public class Alarm implements Parcelable {

    public static final String ALARM_KEY = "alarm_key";

    private GregorianCalendar alarmTime;

    private boolean
            alarmDays[],
            alarmWeekOdd = false, alarmWeekEven = false,
            alarmVibrates = false, alarmEnabled = true;

    private int alarmHours = 10, alarmMinutes = 0, alarmIdentifier;

    private String alarmName  = "";
    private String alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();

    private Alarm(Parcel in) {
        String[] strArr = new String[2];
        int[] intArr = new int[3];
        boolean[] boolArr = new boolean[11];

        in.readStringArray(strArr);
        in.readIntArray(intArr);
        in.readBooleanArray(boolArr);

        alarmName = strArr[0];
        alarmSound = strArr[1];
        alarmIdentifier = intArr[0];
        alarmHours = intArr[1];
        alarmMinutes = intArr[2];
        alarmDays = Arrays.copyOfRange(boolArr, 0, TimeHandler.DAYS_IN_WEEK);
        alarmWeekOdd = boolArr[7];
        alarmWeekEven = boolArr[8];
        alarmVibrates = boolArr[9];
        alarmEnabled = boolArr[10];
    }

    public Alarm(int id) {
        alarmDays = new boolean[TimeHandler.DAYS_IN_WEEK];
        alarmIdentifier = id;
    }

    public Alarm(AlarmModel model) {
        setName(model.name);
        alarmIdentifier = model.id;
        setHours(model.hours);
        setMinutes(model.minutes);
        setAlarmWeekOdd(model.weekOdd);
        setAlarmWeekEven(model.weekEven);
        setDays(Algorithms.castObjectBoolArrayToPrimitive(model.days.toArray(new Boolean[TimeHandler.DAYS_IN_WEEK]))); //сложная строчка, да. костыль костылём погоняет, но чито делать?
        enableAlarm(model.enabled);
        makeVibrate(model.vibrate);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {alarmName, alarmSound});
        parcel.writeIntArray(new int[] {alarmIdentifier, alarmHours, alarmMinutes});
        parcel.writeBooleanArray((Algorithms.concatenateBooleanArrays(alarmDays, new boolean[] {alarmWeekOdd, alarmWeekEven, alarmVibrates, alarmEnabled})));
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    public GregorianCalendar getAlarmTime() {
return null;
    }

    public boolean isWeekly() { return alarmWeekEven == alarmWeekOdd; }
    public boolean isDisposable() { return Algorithms.getActiveDaysCount(getSchedule()) == 0;}
    public void switchAlarm() { alarmEnabled = !alarmEnabled; }

    public AlarmModel getModel() {
        AlarmModel alarmModel = new AlarmModel();

        alarmModel.days = new ArrayList<>(Arrays.asList(Algorithms.castPrimitiveBoolArrayToObject(alarmDays)));
        alarmModel.name = this.alarmName;
        alarmModel.hours = alarmHours;
        alarmModel.minutes = alarmMinutes;
        alarmModel.id = alarmIdentifier;
        alarmModel.weekEven = this.alarmWeekEven;
        alarmModel.weekOdd = this.alarmWeekOdd;
        alarmModel.vibrate = alarmVibrates;
        alarmModel.enabled = alarmEnabled;

        return alarmModel;
    }

    public boolean[] getSchedule() { return alarmDays; }
    public int getHours() { return alarmHours; }
    public int getMinutes() { return alarmMinutes; }
    public int getIdentifier() { return alarmIdentifier; }
    public String getName() { return alarmName; }
    public String getSound() { return alarmSound; }
    public boolean isAlarmWeekOdd() { return alarmWeekOdd; }
    public boolean isAlarmWeekEven() { return alarmWeekEven; }
    public boolean isAlarmVibrates() { return alarmVibrates; }
    public boolean isAlarmEnabled() { return alarmEnabled; }

    public void setAlarmWeekOdd(boolean alarmWeekOdd) { this.alarmWeekOdd = alarmWeekOdd; }
    public void setAlarmWeekEven(boolean alarmWeekEven) { this.alarmWeekEven = alarmWeekEven; }
    public void enableAlarm(boolean enable) { alarmEnabled = enable; }
    public void makeVibrate(boolean vibrate) { alarmVibrates = vibrate; }
    public void setHours(int alarmHours) { this.alarmHours = alarmHours; }
    public void setMinutes(int alarmMinutes) { this.alarmMinutes = alarmMinutes; }
    public void setName(String alarmName) { this.alarmName = alarmName; }
    public void setSound(String path) { alarmSound = path; }
    public void setDays(boolean[] schedule) { alarmDays = schedule; }

    /** Set days by enumeration magic constants from TimeHandler */
    public void setDays(int... args) { for (int a : args) alarmDays[a - 1] = true; }

}
