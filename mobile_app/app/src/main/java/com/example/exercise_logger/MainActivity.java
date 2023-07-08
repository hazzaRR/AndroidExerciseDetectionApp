package com.example.exercise_logger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private TextView timerTextView;
    private Button startButton;
    private Button cancelButton;
    private CountDownTimer countDownTimer;

    private TextView exerciseTextView;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private boolean captureAccelData = false;
    private boolean captureGyroData = false;

    private float[][] sensorData = new float[6][];
    private int accelNumSamples;
    private int gyroNumSamples;

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        cancelButton = findViewById(R.id.cancel_Button);

        exerciseTextView = findViewById(R.id.exercise_prediction);



        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (sensorManager != null) {

            //create arrays for each axis of sensors to record 30 seconds of data
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorData[0] = new float[150]; // Accel x-axis
            sensorData[1] = new float[150]; // Accel y-axis
            sensorData[2] = new float[150]; // Accel z-axis
            sensorData[3] = new float[150]; // Gyro x-axis
            sensorData[4] = new float[150]; // Gyro y-axis
            sensorData[5] = new float[150]; // Gyro z-axis

        }
        else {
            Toast.makeText(this, "Sensor service not detected", Toast.LENGTH_SHORT).show();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(400);

                captureAccelData = true;
                captureGyroData = true;

                accelNumSamples = 0;
                gyroNumSamples = 0;

                onResume();

                startButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);

                //create a 30 seconds timer that is displayed to the phone screen
                countDownTimer = new CountDownTimer(15900, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timerTextView.setText("" + millisUntilFinished / 1000 + " seconds");
                    }

                    @Override
                    public void onFinish() {
                        //once the timer has finished, send a post request to send the data to the api
                        timerTextView.setText("Done!");
                        cancelButton.setText("Reset");

                        captureAccelData = false;
                        captureGyroData = false;
                        onPause();

                        vibrator.vibrate(800);

                        sendPostRequest("http://10.113.228.4:5000/predict", sensorData);


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
                exerciseTextView.setVisibility(View.GONE);

                timerTextView.setText("15 seconds");

            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (captureAccelData && sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorData[0][accelNumSamples] = sensorEvent.values[0]; // Accel x-axis
            sensorData[1][accelNumSamples] = sensorEvent.values[1]; // Accel y-axis
            sensorData[2][accelNumSamples] = sensorEvent.values[2]; // Accel z-axis
            accelNumSamples++;

            if (accelNumSamples >= 150) {
                captureAccelData = false;
            }

        }
        if (captureGyroData && sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            sensorData[3][gyroNumSamples] = sensorEvent.values[0]; // Gyro x-axis
            sensorData[4][gyroNumSamples] = sensorEvent.values[1]; // Gyro y-axis
            sensorData[5][gyroNumSamples] = sensorEvent.values[2]; // Gyro z-axis
            gyroNumSamples++;

            if (gyroNumSamples >= 150) {
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

        sensorManager.registerListener(this, accelerometer, 100000);
        sensorManager.registerListener(this, gyroscope, 100000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        System.out.println(Arrays.toString(sensorData[0]));
        System.out.println(Arrays.toString(sensorData[1]));
        System.out.println(Arrays.toString(sensorData[2]));
        System.out.println(Arrays.toString(sensorData[3]));
        System.out.println(Arrays.toString(sensorData[4]));
        System.out.println(Arrays.toString(sensorData[5]));

    }


    private void sendPostRequest(String postUrl, float[][] data) {

        requestQueue = Volley.newRequestQueue(this);

        //create a 2d jsonarray of the sensor data captured to send to the api

        JSONArray jsonDataArray;

        try {

            jsonDataArray = new JSONArray();

            jsonDataArray.put(new JSONArray(data[0]));
            jsonDataArray.put(new JSONArray(data[1]));
            jsonDataArray.put(new JSONArray(data[2]));
            jsonDataArray.put(new JSONArray(data[3]));
            jsonDataArray.put(new JSONArray(data[4]));
            jsonDataArray.put(new JSONArray(data[5]));



        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        // Send a Post request to the api
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, postUrl, jsonDataArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println(response);
                        try {

                            String result = (String) response.get(0);
                            exerciseTextView.setText(result);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        exerciseTextView.setVisibility(View.VISIBLE);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        System.out.println(error);

                    }
        });

        requestQueue.add(jsonArrayRequest);
    }
}