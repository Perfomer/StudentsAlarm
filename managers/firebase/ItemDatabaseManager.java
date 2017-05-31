package managers.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import activities.LoginActivity;
import activities.MainActivity;
import managers.AlarmHandler;
import objects.Alarm;
import objects.AlarmModel;

import static activities.MainActivity.LOGIN;

public class ItemDatabaseManager extends DatabaseManager {

    private List<AlarmModel> idmItems;
    private boolean idmNeedInitializeRecyclerView = true;

    private MainActivity idmMainActivity;

    private Alarm idmSearchable;

    public ItemDatabaseManager(MainActivity activity, List<AlarmModel> items) {
        super(activity);
        idmMainActivity = activity;
        idmItems = items;
        authAndInitCurrentAlarm();
    }

    private void authAndInitCurrentAlarm() {
        if (auth()) {
            initCurrentAlarm();
            createListener(false);
        }
    }

    @Override
    protected boolean auth() {
        super.auth();
        fbAuth = FirebaseAuth.getInstance();
        try {
            dmUserIdentifier = fbAuth.getCurrentUser().getUid();
            dmAlarmsReference = getUserReference().child("alarms");
            return true;
        } catch (NullPointerException e) {
            Intent intent = new Intent(fbContext, LoginActivity.class);
            ((Activity)fbContext).startActivityForResult(intent, LOGIN);
            if (dmUserIdentifier == null || dmUserIdentifier.isEmpty()) idmMainActivity.finish();
            return false;
        }
    }

    public ItemDatabaseManager(Context alarmActivity) {
        super(alarmActivity);
        idmItems = new ArrayList<>();
        idmNeedInitializeRecyclerView = false;
        authAndInitCurrentAlarm();
        createListener(true);
    }

    private void createListener(boolean single) {
        AlarmDatabaseListener alarmListener = new AlarmDatabaseListener();

        if (single) dmAlarmsReference.addListenerForSingleValueEvent(alarmListener);
        else dmAlarmsReference.addValueEventListener(alarmListener);
    }

    public void saveAlarm(Alarm alarm) {
        dmAlarmsReference.child(String.valueOf(alarm.getIdentifier())).setValue(alarm.getModel());
    }

    public void deleteAlarm(Alarm alarm) {
        dmAlarmsReference.child(String.valueOf(alarm.getIdentifier())).removeValue();
    }


    public void setCurrentAlarmReferenceById(final AlarmHandler aHandler, final int id, final int action) {
        DatabaseReference dr = getUserReference().child("alarms").child(String.valueOf(id));
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AlarmModel model = getAlarmFromDataSnapshotById(dataSnapshot);
                if (model != null) {
                    idmSearchable = new Alarm(model);
                    aHandler.setCurrentAlarm(idmSearchable, action);
                } else {
                    aHandler.setCurrentAlarm(null, 2);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(fbContext, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private AlarmModel getAlarmFromDataSnapshotById(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(AlarmModel.class);
    }

    public List<AlarmModel> getItemsList() {
        return idmItems;
    }

    public void enableAlarm(int id, boolean enable) {
        DatabaseReference reference = dmAlarmsReference.child(String.valueOf(id)).child("enabled");
        reference.setValue(enable);
    }

    public boolean isListHasEnabledAlarms() {
        if (idmItems.isEmpty()) return false;
        for (AlarmModel model : idmItems)
            if (model.enabled) return true;
        return false;
    }

    public Alarm getAlarmById(int id) {
        for (AlarmModel alarm : idmItems) {
            if (alarm.id == id) return new Alarm(alarm);
        }
        return null;
    }

    private class AlarmDatabaseListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            idmItems.clear();
            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

            while (iterator.hasNext())
                idmItems.add(iterator.next().getValue(AlarmModel.class));

            if (idmNeedInitializeRecyclerView) {
                idmNeedInitializeRecyclerView = !idmMainActivity.initRecyclerView();
            }

            if (idmMainActivity != null) {
                idmMainActivity.showNoAlarmsLayout(idmItems.size() == 0);
            }

            AlarmHandler aHandler = new AlarmHandler(fbContext, ItemDatabaseManager.this);
            aHandler.startNearestAlarm();
        }

        @Override
        public void onCancelled(DatabaseError error) {
            Toast.makeText(fbContext, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

