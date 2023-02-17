package com.example.exercise_logger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private TextView timerTextView;
    private Button startButton;

    private Button cancelButton;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        cancelButton = findViewById(R.id.cancel_Button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startButton.setEnabled(false);
//                cancelButton.setEnabled(true);
                startButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                countDownTimer = new CountDownTimer(30000, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        System.out.println(millisUntilFinished);
                        timerTextView.setText("" + millisUntilFinished / 1000 + " seconds");
                    }

                    @Override
                    public void onFinish() {
                        timerTextView.setText("Done!");
                        cancelButton.setText("Reset");
                    }

                }.start();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
//                startButton.setEnabled(true);
//                cancelButton.setEnabled(false);
                startButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.GONE);

                timerTextView.setText("30 seconds");

            }
        });
    }
}