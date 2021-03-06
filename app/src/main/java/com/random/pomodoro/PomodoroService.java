package com.random.pomodoro;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PomodoroService extends Service {
    static final String START_TIME = "start_time";
    static final String IS_RUNNING = "is_running";
    public static final String ELAPSETIME = "elapsetime";
    public static final int NOTIFICATION_ID = 111;
    private long startTime;
    private Timer timer;
    private boolean isRunning;
    public static final String PREFS_NAME = "PomodoroPreference";
    SharedPreferences settings;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) return START_STICKY;

        Log.d("onStart", "onStart on Service");
        Log.d("onStart", "" + startId);
        isRunning = true;
        settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.contains(ELAPSETIME))
            startTime = settings.getLong(ELAPSETIME, new Date().getTime());
        else {
            startTime = new Date().getTime();
            settings.edit().putLong(ELAPSETIME, startTime).apply();
        }
        startTimer();

        return START_STICKY;
    }

    private void startTimer() {
        timer = new Timer();
        TimerTask timerTask = new PomodoroTimerTask(startTime, this);
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void onDestroy() {
        Log.d("onDestroy", "onDestroy on Service");
        if (timer != null) {
            timer.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putLong(START_TIME, startTime);
//        savedInstanceState.putBoolean(IS_RUNNING, isRunning);
//    }
//
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        isRunning = savedInstanceState.getBoolean(IS_RUNNING, false);
//        if (isRunning) {
//            startTime = savedInstanceState.getLong(START_TIME);
//            startTimer();
//        }
//    }

//    public void complete() {
//        isRunning = false;
//        timer.cancel();
//    }

    public class PomodoroTimerTask extends TimerTask {
        public static final int NUMBER_OF_MILLISECONDS_IN_A_SECOND = 1000;
        public static final int NUMBER_OF_SECONDS_IN_MIN = 60;
        public static final String TIMER_ACTION = "com.random.pomodoro.timer_broadcast";
        public static final String BROADCAST_TYPE = "update_type";
        public static final String TICK = "tick";
        public static final String FINISH = "finish";

        private final long startTime;
        private final Context context;

        public PomodoroTimerTask(long startTimeParam, Context context) {
            startTime = startTimeParam;
            this.context = context;
        }

        @Override
        public void run() {
            Intent intent = new Intent(TIMER_ACTION);
            long currentTime = new Date().getTime();
            long elapsedTimeMillis = currentTime - startTime;

            Log.d("APP_LOG", "TOTAL_TIME " + elapsedTimeMillis);
            if (elapsedTimeMillis <= MainActivity.TOTAL_TIME) {
                final long elapsedTimeMins = elapsedTimeMillis / NUMBER_OF_MILLISECONDS_IN_A_SECOND / NUMBER_OF_SECONDS_IN_MIN;
                final long elapsedTimeSecs = (elapsedTimeMillis / NUMBER_OF_MILLISECONDS_IN_A_SECOND) % NUMBER_OF_SECONDS_IN_MIN;
                //Log.d("APP_LOG", String.format("%02d:%02d", elapsedTimeMins, elapsedTimeSecs));
                intent.putExtra(BROADCAST_TYPE, TICK);
                intent.putExtra("elapsed_time_mins", elapsedTimeMins);
                intent.putExtra("elapsed_time_secs", elapsedTimeSecs);

                Intent resultIntent = new Intent(PomodoroService.this, MainActivity.class);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(PomodoroService.this, 0, resultIntent, 0);


                Notification notification = new NotificationCompat.Builder(PomodoroService.this)
                        .setSmallIcon(R.mipmap.ic_stat_pomodoro)
                        .setContentTitle("Pomodoro")
                        .setContentText(String.format("%02d:%02d", elapsedTimeMins, elapsedTimeSecs))
                        .setContentIntent(pendingIntent)
                        .build();
                startForeground(NOTIFICATION_ID, notification);
            } else {
                intent.putExtra(BROADCAST_TYPE, FINISH);
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            if (elapsedTimeMillis > MainActivity.TOTAL_TIME) {
                settings.edit().remove(ELAPSETIME).apply();
                stopForeground(false);
                Intent resultIntent = new Intent(PomodoroService.this, MainActivity.class);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.putExtra("lala", true);
                startActivity(resultIntent);
                stopSelf();
                // StopForeground
            }
        }
    }
}
