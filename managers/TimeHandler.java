package managers;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import objects.Alarm;
import objects.AlarmModel;
import volkovmedia.perfo.studentsalarm.R;

public class TimeHandler {

    public static final int
            WEEKS_IN_YEAR = 52,
            DAYS_IN_WEEK = 7,
            HOURS_IN_DAY = 24,
            MINUTES_IN_HOUR = 60,
            SECONDS_IN_MINUTE = 60,
            MILLISECONDS_IN_SECOND = 1000;

    public static final int
            DAY_MONDAY = 1,
            DAY_TUESDAY = 2,
            DAY_WEDNESDAY = 3,
            DAY_THURSDAY = 4,
            DAY_FRIDAY = 5,
            DAY_SATURDAY = 6,
            DAY_SUNDAY = 7;

    public static long getClosestTimeDifference(Alarm alarm) {
        CurrentTimeManager manager = new CurrentTimeManager();
        return getTimeDifference(manager, alarm, findClosestDay(manager, alarm));
    }

    public static long getClosestTimeDifference(AlarmModel model) {
        return getClosestTimeDifference(new Alarm(model));
    }

    public static ArrayList<Long> getTimeDifferences(Alarm alarm) {
        return getTimeDifferences(new CurrentTimeManager(), alarm);
    }

    public static ArrayList<Long> getTimeDifferences(CurrentTimeManager now, Alarm alarm) {
        boolean days[] = alarm.getSchedule();
        ArrayList<Long> timeDifferences = new ArrayList<>();

        for (int i = 0; i < DAYS_IN_WEEK; i++)
            if (days[i]) timeDifferences.add(getTimeDifference(now, alarm, i + 1));

        Collections.sort(timeDifferences);

        return timeDifferences;
    }

    private static long getTimeDifference(CurrentTimeManager now, Alarm alarm, int destinationDay) {

        boolean isCurrentWeekEven = now.isCurrentWeekEven(),
                isAlarmWeekEven = alarm.isAlarmWeekEven(),
                isAlarmWeekOdd = alarm.isAlarmWeekOdd(),
                isAlarmWeekly = alarm.isWeekly();

        int today = now.getCurrentDay(), daysCount = 0,
                currentHour = now.getCurrentHour(), currentMinute = now.getCurrentMinute(),
                destinationHour = alarm.getHours(), destinationMinute = alarm.getMinutes(),
                hoursCount = 0, minutesCount = 0;


        if (destinationDay == today && (currentHour * MINUTES_IN_HOUR + currentMinute) < (destinationHour * MINUTES_IN_HOUR + destinationMinute))
            return castToMillis(0, 0, (destinationHour * MINUTES_IN_HOUR + destinationMinute) - (currentHour * MINUTES_IN_HOUR + currentMinute)); //costyle

            if (destinationDay > today) {
                daysCount = destinationDay - today;
                if (!isAlarmWeekly && (isCurrentWeekEven ? isAlarmWeekOdd : isAlarmWeekEven))
                    daysCount += DAYS_IN_WEEK;

            } else if (destinationDay < today) {
                daysCount = DAYS_IN_WEEK - today + destinationDay;
                if (!isAlarmWeekly && (isCurrentWeekEven ? isAlarmWeekEven : isAlarmWeekOdd))
                    daysCount += DAYS_IN_WEEK;
            }

        if (destinationHour > currentHour) {
            hoursCount = destinationHour - currentHour;
        } else {
            hoursCount = HOURS_IN_DAY - currentHour + destinationHour;
            if (hoursCount > HOURS_IN_DAY) {
                hoursCount -= HOURS_IN_DAY;
                daysCount++;
                System.out.println(hoursCount);
            }
            if (daysCount > 0) daysCount--;
        }

        if (destinationMinute > currentMinute)
            minutesCount = destinationMinute - currentMinute;
        else {
            minutesCount = MINUTES_IN_HOUR - currentMinute + destinationMinute;
            if (minutesCount > MINUTES_IN_HOUR) {
                minutesCount -= MINUTES_IN_HOUR;
                hoursCount++;
            }
            if (hoursCount > 0) hoursCount--;
        }


        return castToMillis(daysCount, hoursCount, minutesCount);
    }


    private static int findClosestDay(CurrentTimeManager tm, Alarm alarm) {
        int day = -1, today = tm.getCurrentDay();

        boolean days[] = alarm.getSchedule(),
                isToday = (tm.getCurrentHour() * MINUTES_IN_HOUR + tm.getCurrentMinute()) <= (alarm.getHours() * MINUTES_IN_HOUR + alarm.getMinutes()),
                isAlarmWeekEven = alarm.isAlarmWeekEven(),
                isAlarmWeekOdd = alarm.isAlarmWeekOdd();

        if (!(isAlarmWeekEven && isAlarmWeekOdd) && (tm.isCurrentWeekEven() ? isAlarmWeekOdd : isAlarmWeekEven))
            for (int i = 0; i < days.length; i++) if (days[i]) return i + 1;

        if (days[today - 1] && isToday) return today;
        for (int i = today; i < days.length; i++) if (days[i]) return i + 1;
        for (int i = 0; i < today; i++) if (days[i]) return i + 1;

        return isToday ? today : getNextDay(today);
    }

    private static long castToMillis(int days, int hours, int minutes) {
        return (long) (days * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND +
                hours * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND +
                minutes * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND);
    }

    private static int getNextDay(int currentDay) {
        return (currentDay == DAY_SUNDAY) ? DAY_MONDAY : currentDay + 1;
    }

    public static String getDayName(Resources res, int day, boolean longName) {
        switch (day) {
            case DAY_MONDAY:
                return longName ? res.getString(R.string.monday) : res.getString(R.string.mon);
            case DAY_TUESDAY:
                return longName ? res.getString(R.string.tuesday) : res.getString(R.string.tue);
            case DAY_WEDNESDAY:
                return longName ? res.getString(R.string.wednesday) : res.getString(R.string.wed);
            case DAY_THURSDAY:
                return longName ? res.getString(R.string.thursday) : res.getString(R.string.thu);
            case DAY_FRIDAY:
                return longName ? res.getString(R.string.friday) : res.getString(R.string.fri);
            case DAY_SATURDAY:
                return longName ? res.getString(R.string.saturday) : res.getString(R.string.sat);
            case DAY_SUNDAY:
                return longName ? res.getString(R.string.sunday) : res.getString(R.string.sun);
            default:
                return res.getString(R.string.not_found);
        }
    }

    public static String getWeekParityName(Resources res, int week) {
        return isWeekEven(week) ? res.getString(R.string.even) : res.getString(R.string.odd);
    }

    public static boolean isWeekEven(int week) {
        return (week % 2 == 0);
    }

}
