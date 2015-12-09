package com.random.pomodoro;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String FORMAT = "%02d:%02d:%02d";

    int seconds , minutes;
 long time = 120000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.textView);
        Button button = (Button) findViewById(R.id.button);
        textView.setText("00:00:00");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CountDownTimer(time, 1000) {

                    public void onTick(long millisUntilFinished) {
     //                   textView.setText("00: " + ((time -millisUntilFinished ) / 1000) );

                        textView.setText(""+String.format(FORMAT,
                                TimeUnit.MILLISECONDS.toHours(time - millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(time - millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                        TimeUnit.MILLISECONDS.toHours(time - millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(time - millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(time - millisUntilFinished))));

                    }

                    public void onFinish() {
                        textView.setText("00:00:00");
                    }
                }.start();
            }
        });

    }



}
