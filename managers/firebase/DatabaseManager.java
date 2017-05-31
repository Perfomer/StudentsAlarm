package managers.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import activities.LoginActivity;

import static activities.MainActivity.LOGIN;

public class DatabaseManager extends FirebaseManager {

    protected FirebaseDatabase fbDatabase;

    protected DatabaseReference dmAlarmsReference;
    private DatabaseReference dmCurrentAlarmReference;

    protected Context fbContext;

    protected String dmUserIdentifier;
    private int dmCurrentAlarmIdentifier = ERROR_NOT_INITIALIZED_YET;

    public DatabaseManager(Context context) {
        super(context);
        fbContext = context;
        fbDatabase = FirebaseDatabase.getInstance();

    }

    @Override
    protected boolean auth() {return false;}


    protected void initCurrentAlarm() {
        dmCurrentAlarmReference = getUserReference().child("currentAlarm");

        dmCurrentAlarmReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            dmCurrentAlarmIdentifier = dataSnapshot.getValue(Integer.class);
                        }catch (NullPointerException e) {}
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(fbContext, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public int getCurrentAlarmId() {
        return dmCurrentAlarmIdentifier;
    }

    public void setCurrentAlarm(int id) {
        dmCurrentAlarmReference.setValue(id);
    }

    protected DatabaseReference getUserReference() {
        return fbDatabase.getReference().child("users").child(dmUserIdentifier);
    }

//    public DatabaseReference getAlarmsReference() {
//        return dmAlarmsReference;
//    }

}
