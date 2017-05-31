package managers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import managers.firebase.ItemDatabaseManager;
import managers.receivers.AlarmBroadcastReceiver;
import objects.Alarm;
import objects.AlarmModel;

public class AlarmHandler {

    private AlarmManager aAlarmManager;
    private Context aContext;

    private PendingIntent aSender;
    private Alarm ahCurrentAlarm, ahNearestAlarm;

    private ItemDatabaseManager ahDBManager;
    private Handler ahHandler;
    private Runnable ahDBChecker;

    private int count = 0;

    public AlarmHandler(Context context, ItemDatabaseManager idb) {
        aContext = context;
        ahDBManager = idb;
        aAlarmManager = (AlarmManager) aContext.getSystemService(Context.ALARM_SERVICE);
        ahHandler = new Handler();

        ahDBChecker = new Runnable() {
            @Override
            public void run() {
                count++;
                if (ahDBManager.getCurrentAlarmId() == ItemDatabaseManager.ERROR_NOT_INITIALIZED_YET && count <= 10) {
                    ahHandler.postDelayed(this, 100);
                    return;
                }
                else if (count > 10) {
                    ahDBManager.setCurrentAlarm(0);
                }

                if (ahNearestAlarm.getIdentifier() != ahDBManager.getCurrentAlarmId()) {
                    ahDBManager.setCurrentAlarmReferenceById(AlarmHandler.this, ahDBManager.getCurrentAlarmId(), 0);
                } else {
                    ahDBManager.setCurrentAlarmReferenceById(AlarmHandler.this, ahDBManager.getCurrentAlarmId(), 1);
                }
            }
        };

    }

    public void setCurrentAlarm(Alarm alarm, int action) {
        ahCurrentAlarm = alarm;
        switch (action) {
            case 0:
                initIntents(ahCurrentAlarm);
                aAlarmManager.cancel(aSender);
                break;
        }
        ahCurrentAlarm = ahNearestAlarm;
        ahDBManager.setCurrentAlarm(ahCurrentAlarm.getIdentifier());

        initIntents(ahCurrentAlarm);

        long timeDifference = TimeHandler.getClosestTimeDifference(ahCurrentAlarm),
                time = System.currentTimeMillis() + timeDifference;

        aAlarmManager.set(
                AlarmManager.RTC_WAKEUP,
                time,
                aSender
        );
        Toast.makeText(
                aContext,
                "Будильник " + (ahCurrentAlarm.getName().isEmpty() ? "" : (ahCurrentAlarm.getName()) + " ") + "зазвонит через " + (timeDifference / 1000 / 60) + " минут.",
                Toast.LENGTH_LONG
        ).show();

        Log.e("WOW3", ahCurrentAlarm.getModel().toString());
    }

    public boolean startNearestAlarm() {
        List<AlarmModel> list = ahDBManager.getItemsList();
        if (ahDBManager.isListHasEnabledAlarms()) {
            ahNearestAlarm = findNearestAlarm(list);
            ahHandler.post(ahDBChecker);
            return true;
        }
        else {
            return false;
        }

    }

    private void initIntents(Alarm alarm) {
        Intent intent = new Intent(aContext, AlarmBroadcastReceiver.class);
        intent.putExtra(Alarm.ALARM_KEY, alarm);
        aSender = PendingIntent.getBroadcast(aContext, alarm.getIdentifier(), intent, 0);
    }

    private static Alarm findNearestAlarm(List<AlarmModel> alarmList) {

        AlarmModel nearestAlarm = null;
        int position = 0;

        for (; position < alarmList.size(); position++) {
            AlarmModel currentAlarm = alarmList.get(position);
            if (currentAlarm.enabled) {
                nearestAlarm = currentAlarm;
                break;
            }
        }

        long nearestTimeDifference = TimeHandler.getClosestTimeDifference(nearestAlarm);

        for (position++; position < alarmList.size(); position++) {
            AlarmModel currentAlarm = alarmList.get(position);
            if (!currentAlarm.enabled) continue;

            long currentTimeDifference = TimeHandler.getClosestTimeDifference(currentAlarm);

            if (nearestTimeDifference > currentTimeDifference) {
                nearestAlarm = currentAlarm;
                nearestTimeDifference = currentTimeDifference;
            }
        }
        Log.e("WOW3", "Нашли: " + Algorithms.getTimeText(nearestAlarm.hours, nearestAlarm.minutes));
        return new Alarm(nearestAlarm);
    }
}