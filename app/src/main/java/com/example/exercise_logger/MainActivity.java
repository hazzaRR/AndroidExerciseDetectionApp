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
    private Sensor gyroscope;
    private boolean captureAccelData = false;
    private boolean captureGyroData = false;

    private float[][] accelerometerData = new float[3][];
    private float[][] gyroscopeData = new float[3][];
    private int accelNumSamples = 0;
    private int gyrolNumSamples = 0;


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
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            accelerometerData[0] = new float[300]; // x-axis
            accelerometerData[1] = new float[300]; // y-axis
            accelerometerData[2] = new float[300]; // z-axis
            gyroscopeData[0] = new float[300]; // x-axis-
            gyroscopeData[1] = new float[300]; // y-axis
            gyroscopeData[2] = new float[300]; // z-axis

        }
        else {
            Toast.makeText(this, "Sensor service not detected", Toast.LENGTH_SHORT).show();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                captureAccelData = true;
                captureGyroData = true;

                onResume();

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

                        captureAccelData = false;
                        captureGyroData = false;
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
                startButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.GONE);

                timerTextView.setText("30 seconds");

            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (captureAccelData && sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerData[0][accelNumSamples] = sensorEvent.values[0]; // x-axis
            accelerometerData[1][accelNumSamples] = sensorEvent.values[1]; // y-axis
            accelerometerData[2][accelNumSamples] = sensorEvent.values[2]; // z-axis
            accelNumSamples++;

            if (accelNumSamples >= 300) {
                captureAccelData = false;
            }

        }
        if (captureGyroData && sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeData[0][gyrolNumSamples] = sensorEvent.values[0]; // x-axis
            gyroscopeData[1][gyrolNumSamples] = sensorEvent.values[1]; // y-axis
            gyroscopeData[2][gyrolNumSamples] = sensorEvent.values[2]; // z-axis
            gyrolNumSamples++;

            if (gyrolNumSamples >= 300) {
                captureGyroData = false;
            }

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
        sensorManager.registerListener(this, gyroscope, 100000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        System.out.println(Arrays.toString(accelerometerData[0]));
        System.out.println(Arrays.toString(accelerometerData[1]));
        System.out.println(Arrays.toString(accelerometerData[2]));
        System.out.println(Arrays.toString(gyroscopeData[0]));
        System.out.println(Arrays.toString(gyroscopeData[1]));
        System.out.println(Arrays.toString(gyroscopeData[2]));

    }
}