package com.random.pomodoro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final long TOTAL_TIME = 10000;

    TextView textView;
    private PomodoroOnClickListener listener;

    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        Button button = (Button) findViewById(R.id.button);
        listener = new PomodoroOnClickListener();
        button.setOnClickListener(listener);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                if (intent.getStringExtra("update_type") == "tick")
                    textView.setText(String.format("%02d:%02d", intent.getLongExtra("elapsed_time_mins", 0), intent.getLongExtra("elapsed_time_secs", 0)));
                else {
                    Toast.makeText(MainActivity.this, "Pomodoro Successful", Toast.LENGTH_LONG).show();

                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(PomodoroTimerTask.TIMER_ACTION));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        listener.onSaveInstanceState(savedInstanceState);
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listener.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listener.onDestroy();
    }

    private class PomodoroTimerTask extends TimerTask {
        public static final int NUMBER_OF_MILLISECONDS_IN_A_SECOND = 1000;
        public static final int NUMBER_OF_SECONDS_IN_MIN = 60;
        public static final String TIMER_ACTION = "com.random.pomodoro.timer_broadcast";

        private final long startTime;

        public PomodoroTimerTask(long startTimeParam) {
            startTime = startTimeParam;
        }

        @Override
        public void run() {
            Intent intent = new Intent(TIMER_ACTION);
            long currentTime = new Date().getTime();
            long elapsedTimeMillis = currentTime - startTime;
            Log.d("APP_LOG", "TOTAL_TIME " + elapsedTimeMillis);
            if (elapsedTimeMillis <= TOTAL_TIME) {
                final long elapsedTimeMins = elapsedTimeMillis / NUMBER_OF_MILLISECONDS_IN_A_SECOND / NUMBER_OF_SECONDS_IN_MIN;
                final long elapsedTimeSecs = (elapsedTimeMillis / NUMBER_OF_MILLISECONDS_IN_A_SECOND) % NUMBER_OF_SECONDS_IN_MIN;
                //Log.d("APP_LOG", String.format("%02d:%02d", elapsedTimeMins, elapsedTimeSecs));
                intent.putExtra("update_type", "tick");
                intent.putExtra("elapsed_time_mins", elapsedTimeMins);
                intent.putExtra("elapsed_time_secs", elapsedTimeSecs);

            } else {
                intent.putExtra("update_type", "finish");
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "Pomodoro Successful", Toast.LENGTH_LONG).show();
//                    }
                //});
                listener.complete();
            }
            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
        }
    }

    private class PomodoroOnClickListener implements View.OnClickListener {
        static final String START_TIME = "start_time";
        static final String IS_RUNNING = "is_running";
        private long startTime;
        private Timer timer;
        private boolean isRunning;

        @Override
        public void onClick(View v) {
            if (isRunning) return;
            isRunning = true;
            startTime = new Date().getTime();
            startTimer();
        }

        private void startTimer() {
            timer = new Timer();
            TimerTask timerTask = new PomodoroTimerTask(startTime);
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
        }

        public void onDestroy() {
            if (timer != null)
                timer.cancel();
        }

        private void onSaveInstanceState(Bundle savedInstanceState) {
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
    }
}
