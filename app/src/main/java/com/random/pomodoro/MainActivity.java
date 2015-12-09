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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        Button button = (Button) findViewById(R.id.button);
        textView.setText("00:00");
        button.setOnClickListener(new PomodoroOnClickListener());
    }


    private class PomodoroTimerTask extends TimerTask {

        public static final int NUMBER_OF_MILLISECONDS_IN_A_SECOND = 1000;
        public static final int NUMBER_OF_SECONDS_IN_MIN = 60;
        private final long startTime;

        public PomodoroTimerTask() {
            startTime = new Date().getTime();
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
            Timer timer = new Timer();
            TimerTask tasknew = new PomodoroTimerTask();
            timer.scheduleAtFixedRate(tasknew, 0, 1000);
        }
    }
}
