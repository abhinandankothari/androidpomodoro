package com.random.pomodoro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PomodoroOnClickListener implements View.OnClickListener {
    static final String START_TIME = "start_time";
    static final String IS_RUNNING = "is_running";
    private final Context context;
    private long startTime;
    private Timer timer;
    private boolean isRunning;

    public PomodoroOnClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (isRunning) return;
        isRunning = true;
        startTime = new Date().getTime();
        startTimer();
    }

    private void startTimer() {
        timer = new Timer();
        TimerTask timerTask = new PomodoroTimerTask(startTime, context);
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void onDestroy() {
        if (timer != null)
            timer.cancel();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(START_TIME, startTime);
        savedInstanceState.putBoolean(IS_RUNNING, isRunning);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        isRunning = savedInstanceState.getBoolean(IS_RUNNING, false);
        if (isRunning) {
            startTime = savedInstanceState.getLong(START_TIME);
            startTimer();
        }
    }

    public void complete() {
        isRunning = false;
        timer.cancel();
    }

    public static class PomodoroTimerTask extends TimerTask {
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

            } else {
                intent.putExtra(BROADCAST_TYPE, FINISH);
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }
}
