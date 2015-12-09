package com.random.pomodoro;

import android.os.Bundle;
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

    int seconds, minutes;
    long time = 60000;
    TextView textView;
    static final String START_TIME = "start_time";
    static final String IS_RUNNING = "is_running";
    private  long startTime;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        Button button = (Button) findViewById(R.id.button);
        textView.setText("00:00");
        button.setOnClickListener(new PomodoroOnClickListener());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d("save", "saveinstance");
        // Save custom values into the bundle
        savedInstanceState.putLong(START_TIME, startTime);
        savedInstanceState.putBoolean(IS_RUNNING, isRunning);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {

        Log.d("restore", "isrestore");

        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        startTime = savedInstanceState.getLong(START_TIME);
        isRunning = savedInstanceState.getBoolean(IS_RUNNING);
        if(isRunning){
            Timer timer = new Timer();
            TimerTask tasknew = new PomodoroTimerTask(startTime);
            timer.scheduleAtFixedRate(tasknew, 0, 1000);
        }
    }

    private class PomodoroTimerTask extends TimerTask {

        public static final int NUMBER_OF_MILLISECONDS_IN_A_SECOND = 1000;
        public static final int NUMBER_OF_SECONDS_IN_MIN = 60;


        public PomodoroTimerTask(long startTimeParam) {
            if (startTimeParam == 0)
                startTime = new Date().getTime();
            else startTime = startTimeParam;

        }

        @Override
        public void run() {
            long currentTime = new Date().getTime();
            long elapsedTimeMillis = currentTime - startTime;
            Log.d("APP_LOG", "time " + elapsedTimeMillis);
            if (elapsedTimeMillis <= time) {
                final long elapsedTimeMins = elapsedTimeMillis / NUMBER_OF_MILLISECONDS_IN_A_SECOND / NUMBER_OF_SECONDS_IN_MIN;
                final long elapsedTimeSecs = (elapsedTimeMillis / NUMBER_OF_MILLISECONDS_IN_A_SECOND) % NUMBER_OF_SECONDS_IN_MIN;
                Log.d("APP_LOG", String.format("%02d:%02d", elapsedTimeMins, elapsedTimeSecs));
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textView.setText(String.format("%02d:%02d", elapsedTimeMins, elapsedTimeSecs));
                    }
                });
            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Pomodoro Successful", Toast.LENGTH_LONG).show();
                        isRunning = false;
                        textView.setText("00:00");
                    }
                });
                cancel();

            }
        }
    }

    private class PomodoroOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            isRunning = true;
            Timer timer = new Timer();
            TimerTask tasknew = new PomodoroTimerTask(startTime);
            timer.scheduleAtFixedRate(tasknew, 0, 1000);
        }
    }
}
