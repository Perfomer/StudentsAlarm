package activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import managers.Algorithms;
import managers.CurrentTimeManager;
import objects.Alarm;
import volkovmedia.perfo.studentsalarm.R;

public class AlarmSettingsActivity extends AppCompatActivity {

    private boolean alarmDays[] = {false, false, false, false, false, false, false},
            alarmWeekOdd = false, alarmWeekEven = false,
            editingMode = false, isAlarmEdited = false, isAlarmEnabled = true;

    private int alarmId;

    private Context alarmContext;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private CurrentTimeManager alarmTimeManager;
    private GregorianCalendar alarmCalendar;

    private GridLayout glDays, glWeeks;
    private TextView tvDay, tvWeek;
    private TickerView tvTime;
    private EditText etName;
    private AlertDialog.Builder adQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_alarm);

        alarmContext = getApplicationContext();
        alarmTimeManager = new CurrentTimeManager(alarmContext);
        alarmCalendar = alarmTimeManager.getCalendar();

        initViews();
        handleIncomingIntent(getIntent());
    }

    private void handleIncomingIntent(Intent data) {
        Alarm alarm = data.getParcelableExtra(Alarm.ALARM_KEY);
        editingMode = (alarm != null);

        if (editingMode) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Настройка будильника");
            boolean[] days = alarm.getSchedule();
            for (int i = 0; i < days.length; i++)
                if (days[i]) {
                    alarmDays[i] = days[i];
                    activateDateView(glDays.findViewWithTag(String.valueOf(i + 1)), false);
                }

            tvTime.setText(Algorithms.getTimeText(alarm.getHours(), alarm.getMinutes()));
            etName.setText(alarm.getName());

            alarmCalendar.set(Calendar.HOUR_OF_DAY, alarm.getHours());
            alarmCalendar.set(Calendar.MINUTE, alarm.getMinutes());

            alarmWeekEven = alarm.isAlarmWeekEven();
            if (alarmWeekEven) activateDateView(glWeeks.findViewWithTag("even"), false);

            alarmWeekOdd = alarm.isAlarmWeekOdd();
            if (alarmWeekOdd) activateDateView(glWeeks.findViewWithTag("odd"),  false);

            alarmId = alarm.getIdentifier();
            isAlarmEnabled = alarm.isAlarmEnabled();
        }

    }

    @Override
    public void onBackPressed() {
        if (isAlarmEdited) adQuit.show();
        else finish();
    }

    private Alarm saveAlarm() {
        int id = editingMode ? alarmId : (new Random()).nextInt(1000000);
        Alarm newAlarm = new Alarm(id);

        newAlarm.setAlarmWeekEven(alarmWeekEven);
        newAlarm.setAlarmWeekOdd(alarmWeekOdd);
        newAlarm.setDays(alarmDays);
        newAlarm.setHours(alarmTimeManager.getCurrentHour());
        newAlarm.setMinutes(alarmTimeManager.getCurrentMinute());
        newAlarm.setName(etName.getText().toString());
        newAlarm.makeVibrate(true); // FIXME: 28.03.2017 ДОБАВЬ ВОЗМОЖНОСТЬ УКАЗЫВАТЬ ВИБРАЦИЮ
        newAlarm.enableAlarm(isAlarmEnabled);

        return newAlarm;
    }

    private void saveAlarmAndExit() {
        Intent intent = new Intent();
        intent.putExtra(Alarm.ALARM_KEY, saveAlarm());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void refreshCurrentTimeViews() {
        tvDay.setText(getString(R.string.stn_today) + " " + alarmTimeManager.getCurrentDayName());
        tvWeek.setText(getString(R.string.stn_now) + " " + alarmTimeManager.getCurrentWeekParityName() + " " + getString(R.string.stn_week));
        setInitialDateTime();
    }

    private void setInitialDateTime() {
        tvTime.setText(Algorithms.getTimeText(alarmTimeManager.getCurrentHour(), alarmTimeManager.getCurrentMinute()));
    }

    public void onDayButtonClick(View v) {
        isAlarmEdited = true;
        int day = Integer.parseInt(v.getTag().toString()) - 1;

        activateDateView(v, alarmDays[day]);
        alarmDays[day] = !alarmDays[day];
    }

    public void onWeekButtonClick(View v) {
        isAlarmEdited = true;
        boolean isWeekEven = v.getTag().toString().equals("even");

        activateDateView(v, isWeekEven ? alarmWeekEven : alarmWeekOdd);

        if (isWeekEven) alarmWeekEven = !alarmWeekEven;
        else alarmWeekOdd = !alarmWeekOdd;
    }

    /**
     * ACTIVATION TEXTCHECKBOXVIEW METHOD
     * [Changes state of view to normal or active]
     *
     * @param v      — View, которое будет изменяться.
     * @param normal — если необходимо перевести View в нормальное состояние, то 1, если в активное то 0.
     */
    private void activateDateView(View v, boolean normal) {
        TransitionDrawable transition = (TransitionDrawable) v.getBackground();
        int animSpeed = getResources().getInteger(R.integer.animspeed_short);

        if (normal) transition.reverseTransition(animSpeed);
        else transition.startTransition(animSpeed);

        ((TextView) v).setTextColor(
                ContextCompat.getColor(
                        alarmContext,
                        (normal ? android.R.color.tertiary_text_dark : android.R.color.white)));
    }

    public void onDeclineButtonClick(View v) {
        if (isAlarmEdited) adQuit.show();
        else finish();
    }

    public void onAcceptButtonClick(View v) {
        saveAlarmAndExit();
    }

    public void onChangeTimeClick(View v) {
        new TimePickerDialog(this, timeSetListener,
                alarmCalendar.get(Calendar.HOUR_OF_DAY),
                alarmCalendar.get(Calendar.MINUTE), true)
                .show();
    }

    private void initViews() {
        tvTime = (TickerView) findViewById(R.id.alarmstn_time);
        tvTime.setCharacterList(TickerUtils.getDefaultNumberList());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(alarmContext, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvDay   = (TextView)   findViewById(R.id.alarmstn_day);
        tvWeek  = (TextView)   findViewById(R.id.alarmact_week);
        etName  = (EditText)   findViewById(R.id.alarmstn_name);
        glWeeks = (GridLayout) findViewById(R.id.alarmstn_weeks);
        glDays  = (GridLayout) findViewById(R.id.alarmstn_days);

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                alarmCalendar.set(Calendar.MINUTE, minute);
                setInitialDateTime();
                isAlarmEdited = true;
            }
        };

        refreshCurrentTimeViews();

        adQuit = new AlertDialog.Builder(this);
        adQuit.setTitle(getString(R.string.stn_confirmation));  // заголовок
        adQuit.setMessage(getString(R.string.stn_confirmation_txt)); // сообщение
        adQuit.setCancelable(true);

        adQuit.setPositiveButton(getString(R.string.stn_save_alarm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(alarmContext, "Сохранено)0)", Toast.LENGTH_LONG).show();
                saveAlarmAndExit();
            }
        });
        adQuit.setNegativeButton(getString(R.string.stn_quit_without_saving), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(alarmContext, "Не сохранено((99((", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }


}
