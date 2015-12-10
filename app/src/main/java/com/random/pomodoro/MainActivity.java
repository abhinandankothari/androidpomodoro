package com.random.pomodoro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.random.pomodoro.PomodoroService.PomodoroTimerTask.BROADCAST_TYPE;
import static com.random.pomodoro.PomodoroService.PomodoroTimerTask.FINISH;
import static com.random.pomodoro.PomodoroService.PomodoroTimerTask.TICK;

public class MainActivity extends AppCompatActivity {
    public static final long TOTAL_TIME = 10000;

    TextView textView;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("onCreate", "onCreate on Activity");


        textView = (TextView) findViewById(R.id.textView);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(new Intent(getApplicationContext(), PomodoroService.class));
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                String broadcastType = intent.getStringExtra(BROADCAST_TYPE);
                switch (broadcastType) {
                    case TICK:
                        Log.d("TICK", String.valueOf(intent.getLongExtra("elapsed_time_mins", 0)+intent.getLongExtra("elapsed_time_secs", 0)));
                        textView.setText(String.format("%02d:%02d", intent.getLongExtra("elapsed_time_mins", 0), intent.getLongExtra("elapsed_time_secs", 0)));
                        break;
                    case FINISH:
                        Log.d("FINISH", "FINISH");
                        Toast.makeText(MainActivity.this, "Pomodoro Successful", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
        onNewIntent(getIntent());
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
                new IntentFilter(PomodoroService.PomodoroTimerTask.TIMER_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopService(new Intent(getApplicationContext(), PomodoroService.class));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean lala = getIntent().getBooleanExtra("lala", false);
        Log.d("Timer", ""+lala);
        if(lala)
        Toast.makeText(MainActivity.this, "Pomodoro Successful", Toast.LENGTH_LONG).show();
    }
}
