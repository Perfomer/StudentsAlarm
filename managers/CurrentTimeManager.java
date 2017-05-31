package managers;

import android.content.Context;
import android.content.res.Resources;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CurrentTimeManager {

    private GregorianCalendar tmCalendar;
    private Resources tmResources;

    public CurrentTimeManager(Context context) {
        this();
        tmResources = context.getResources();
    }

    public CurrentTimeManager() {
        tmCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
    }

    public int getCurrentHour() { return tmCalendar.get(Calendar.HOUR_OF_DAY); }
    public int getCurrentMinute() { return tmCalendar.get(Calendar.MINUTE); }

    public int getCurrentNumber() { return tmCalendar.get(Calendar.DAY_OF_MONTH); }
    public int getCurrentDay() {
        int currentDay = tmCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (currentDay == 0) currentDay += TimeHandler.DAYS_IN_WEEK;

        return currentDay;
    }

    private int getCurrentWeekOfYear() {
        int currentWeek = tmCalendar.get(Calendar.WEEK_OF_YEAR) - 1; //Понятия не имею, почему ГрегоринаскийКалендарь возвращает следующую неделю, а не текущую...
        if (currentWeek == 0) currentWeek += TimeHandler.WEEKS_IN_YEAR;

        return currentWeek; }

    public boolean isCurrentWeekEven() { return TimeHandler.isWeekEven(getCurrentWeekOfYear()); }
    public String getCurrentDayName() { return TimeHandler.getDayName(tmResources, getCurrentDay(), true); }
    public String getCurrentWeekParityName() { return TimeHandler.getWeekParityName(tmResources, getCurrentWeekOfYear()); }

    public GregorianCalendar getCalendar() { return tmCalendar; }

}