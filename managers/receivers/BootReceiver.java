package managers.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import managers.AlarmHandler;
import managers.firebase.ItemDatabaseManager;
import objects.AlarmModel;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ItemDatabaseManager idm = new ItemDatabaseManager(context);
    }
}