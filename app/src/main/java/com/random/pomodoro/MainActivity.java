package com.random.pomodoro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.random.pomodoro.PomodoroOnClickListener.PomodoroTimerTask.BROADCAST_TYPE;
import static com.random.pomodoro.PomodoroOnClickListener.PomodoroTimerTask.FINISH;
import static com.random.pomodoro.PomodoroOnClickListener.PomodoroTimerTask.TICK;

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
        listener = new PomodoroOnClickListener(this);
        button.setOnClickListener(listener);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                String broadcastType = intent.getStringExtra(BROADCAST_TYPE);
                switch (broadcastType) {
                    case TICK:
                        textView.setText(String.format("%02d:%02d", intent.getLongExtra("elapsed_time_mins", 0), intent.getLongExtra("elapsed_time_secs", 0)));
                        break;
                    case FINISH:
                        Toast.makeText(MainActivity.this, "Pomodoro Successful", Toast.LENGTH_LONG).show();
                        listener.complete();
                        break;
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
                new IntentFilter(PomodoroOnClickListener.PomodoroTimerTask.TIMER_ACTION));
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
}
