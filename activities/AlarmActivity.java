package activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import managers.AlarmHandler;
import managers.CurrentTimeManager;
import managers.firebase.ItemDatabaseManager;
import managers.receivers.AlarmBroadcastReceiver;
import managers.Algorithms;
import managers.TimeHandler;
import managers.receivers.AlarmService;
import objects.Alarm;
import objects.AlarmModel;
import volkovmedia.perfo.studentsalarm.R;

import static objects.Alarm.ALARM_KEY;

public class AlarmActivity extends AppCompatActivity {

    private Alarm aAlarm;

    private PendingIntent aPendingIntent;
    private AlarmManager aAlarmManager;

    private MediaPlayer aMediaPlayer;
    private Vibrator aVibrator;

    ItemDatabaseManager aIDManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aIDManager = new ItemDatabaseManager(this);

        turnOnScreen();
        setContentView(R.layout.activity_alarm);

        aAlarm = getIntent().getParcelableExtra(Alarm.ALARM_KEY);
        if (aAlarm.isDisposable()) {
            Log.d("WOW4", "It's worked!");
            aIDManager.enableAlarm(aAlarm.getIdentifier(), false);
            //aIDManager.saveAlarm(aAlarm);
        }

        Intent aIntent = new Intent(this, AlarmBroadcastReceiver.class);
        aPendingIntent = PendingIntent.getBroadcast(this, aAlarm.getIdentifier(), aIntent, 0);
        aAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        initViews();
        //initPlayer();
        //prepareForCalls();

        aAlarmManager.cancel(aPendingIntent);
    }

    private void initViews() {
        TextView timeView = (TextView) findViewById(R.id.alarmact_time),
                nameView = (TextView) findViewById(R.id.alarmact_name);

        String name = aAlarm.getName();
        if (name.isEmpty()) nameView.setVisibility(View.GONE);
        else nameView.setText(name);

        CurrentTimeManager currentTimeManager = new CurrentTimeManager();

        timeView.setText(Algorithms.getTimeText(currentTimeManager.getCurrentHour(), currentTimeManager.getCurrentMinute()));
        timeView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_flashing));
    }

    private void initPlayer() {
        aMediaPlayer = new MediaPlayer();

        if (aAlarm.isAlarmVibrates()) {
            aVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {1000, 200, 200, 200};
            aVibrator.vibrate(pattern, 0);
        }
        try {
            aMediaPlayer.setVolume(1.0f, 1.0f);
            aMediaPlayer.setDataSource(this, Uri.parse(aAlarm.getSound()));
            aMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            aMediaPlayer.setLooping(true);
            aMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                }

            });
            aMediaPlayer.prepare();
            aMediaPlayer.start();

        } catch (Exception e) {
            aMediaPlayer.release();
        }


    }

    private void turnOnScreen() {
        final Window window = getWindow();
        window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        );
    }

    private void finishActivity() {
        finish();
    }

    public void onAlarmTurnOffClick(View v) {
        finishActivity();
    }

    public void onAlarmSetAsideClick(View v) {
        aAlarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 5 * TimeHandler.SECONDS_IN_MINUTE * TimeHandler.MILLISECONDS_IN_SECOND,
                aPendingIntent);
        finishActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if (aVibrator != null) aVibrator.cancel();
            aMediaPlayer.stop();
            aMediaPlayer.release();
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    private void prepareForCalls() {
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        try {
                            aMediaPlayer.pause();
                        } catch (IllegalStateException e) {
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        try {
                            aMediaPlayer.start();
                        } catch (IllegalStateException e) {
                        }
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }
}