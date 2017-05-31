package managers.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;

import activities.AlarmActivity;
import managers.AlarmHandler;
import managers.firebase.ItemDatabaseManager;
import objects.AlarmModel;

import static objects.Alarm.ALARM_KEY;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmActivity = new Intent(context, AlarmActivity.class);
        alarmActivity.putExtra(ALARM_KEY, intent.getParcelableExtra(ALARM_KEY));
        alarmActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmActivity);

    }

}
