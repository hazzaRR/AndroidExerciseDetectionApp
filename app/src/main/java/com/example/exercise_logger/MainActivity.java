package com.example.exercise_logger;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private TextView timerTextView;
    private Button startButton;
    private Button cancelButton;
    private CountDownTimer countDownTimer;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean captureSensorData = false;

    private float[][] accelerometerData = new float[3][];
    private int numSamples = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        cancelButton = findViewById(R.id.cancel_Button);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            accelerometerData[0] = new float[300]; // x-axis
            accelerometerData[1] = new float[300]; // y-axis
            accelerometerData[2] = new float[300]; // z-axis

        }
        else {
            Toast.makeText(this, "Sensor service not detected", Toast.LENGTH_SHORT).show();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                captureSensorData = true;

                onResume();


//                startButton.setEnabled(false);
//                cancelButton.setEnabled(true);
                startButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                countDownTimer = new CountDownTimer(30900, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timerTextView.setText("" + millisUntilFinished / 1000 + " seconds");
                    }

                    @Override
                    public void onFinish() {
                        timerTextView.setText("Done!");
                        cancelButton.setText("Reset");

                        captureSensorData = false;
                        onPause();
                    }

                }.start();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onPause();


                countDownTimer.cancel();
//                startButton.setEnabled(true);
//                cancelButton.setEnabled(false);
                startButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.GONE);

                timerTextView.setText("30 seconds");

            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (captureSensorData && sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerData[0][numSamples] = sensorEvent.values[0]; // x-axis
            accelerometerData[1][numSamples] = sensorEvent.values[1]; // y-axis
            accelerometerData[2][numSamples] = sensorEvent.values[2]; // z-axis
            numSamples++;

            if (numSamples >= 300) {
                captureSensorData = false;
            }

//            System.out.println("A_X: " + sensorEvent.values[0]);
//            System.out.println("A_Y: " + sensorEvent.values[1]);
//            System.out.println("A_Z: " + sensorEvent.values[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("This was called");
        sensorManager.registerListener(this, accelerometer, 100000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        System.out.println(Arrays.toString(accelerometerData[0]));
        System.out.println(Arrays.toString(accelerometerData[1]));
        System.out.println(Arrays.toString(accelerometerData[2]));
        System.out.println(accelerometerData[0][299]);
        System.out.println(accelerometerData[1][299]);
        System.out.println(accelerometerData[2][299]);
    }
}