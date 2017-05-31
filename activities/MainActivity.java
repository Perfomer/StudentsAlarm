package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.drivemode.android.typeface.TypefaceHelper;

import java.util.ArrayList;

import managers.AlarmHandler;
import managers.Algorithms;
import managers.firebase.DatabaseManager;
import managers.firebase.ItemDatabaseManager;
import managers.receivers.AlarmService;
import objects.Alarm;
import objects.AlarmModel;
import views.AlarmAdapter;
import volkovmedia.perfo.studentsalarm.R;

public class MainActivity extends AppCompatActivity {

    public static final int ALARM_SETTINGS = 100, LOGIN = 101;

    private ItemDatabaseManager mDatabaseManager;

    private ArrayList<AlarmModel> sAlarms;

    private RecyclerView sRecyclerView;
    private AlarmAdapter sAlarmAdapter;
    private ProgressBar sProgressBar;
    private LinearLayout mNoAlarmsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(TypefaceHelper.getInstance().setTypeface(this, R.layout.activity_main, "fonts/bender_light.otf"));
        TypefaceHelper.getInstance().setTypeface(this, ApplicationWrapper.FONT_MAIN_LIGHT);

        sAlarms = new ArrayList<>();

        initFirebaseManager();
        initViews();
    }

    private void initViews() {
        mNoAlarmsLayout = (LinearLayout) findViewById(R.id.main_noalarms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sRecyclerView = (RecyclerView) findViewById(R.id.alarm_recyclerview);
        sRecyclerView.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlarmSettingsActivity.class);
                startActivityForResult(intent, ALARM_SETTINGS);
            }
        });

        sProgressBar = (ProgressBar) findViewById(R.id.alarm_progressBar);
        sProgressBar.setVisibility(View.VISIBLE);
        sProgressBar.setProgress(50);

        showNoAlarmsLayout(false);
    }

    public boolean initRecyclerView() {
        sRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sAlarmAdapter = new AlarmAdapter(sAlarms, this);
        sRecyclerView.setAdapter(sAlarmAdapter);
        sRecyclerView.setItemAnimator(new DefaultItemAnimator());
        sProgressBar.setVisibility(View.GONE);
        sRecyclerView.setVisibility(View.VISIBLE);
        return false;
    }

    public void showNoAlarmsLayout(boolean show) {
        int noAlarmsVisibility = show ? View.VISIBLE : View.GONE,
                recyclerViewVisibility = show ? View.GONE : View.VISIBLE;

        mNoAlarmsLayout.setVisibility(noAlarmsVisibility);
        sRecyclerView.setVisibility(recyclerViewVisibility);
    }

    private void initFirebaseManager() {
        mDatabaseManager = new ItemDatabaseManager(this, sAlarms);
    }

    public ItemDatabaseManager getDatabaseManager() {
        return mDatabaseManager;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case ALARM_SETTINGS:
                        mDatabaseManager.saveAlarm((Alarm) data.getParcelableExtra(Alarm.ALARM_KEY));
                        break;
                    case LOGIN:
                        initFirebaseManager();
                        break;
                }
                break;
            case RESULT_CANCELED:
                switch (requestCode) {
                    case LOGIN:
                        finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}