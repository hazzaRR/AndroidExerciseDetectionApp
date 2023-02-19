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


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorData[0] = new float[300]; // Accel x-axis
            sensorData[1] = new float[300]; // Accel y-axis
            sensorData[2] = new float[300]; // Accel z-axis
            sensorData[3] = new float[300]; // Gyro x-axis
            sensorData[4] = new float[300]; // Gyro y-axis
            sensorData[5] = new float[300]; // Gyro z-axis

        }
        else {
            Toast.makeText(this, "Sensor service not detected", Toast.LENGTH_SHORT).show();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                captureAccelData = true;
                captureGyroData = true;

                accelNumSamples = 0;
                gyroNumSamples = 0;

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

                        sendPostRequest("http://192.168.0.33:5000/predict", sensorData);

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
            sensorData[0][accelNumSamples] = sensorEvent.values[0]; // Accel x-axis
            sensorData[1][accelNumSamples] = sensorEvent.values[1]; // Accel y-axis
            sensorData[2][accelNumSamples] = sensorEvent.values[2]; // Accel z-axis
            accelNumSamples++;

            if (accelNumSamples >= 300) {
                captureAccelData = false;
            }

        }
        if (captureGyroData && sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            sensorData[3][gyroNumSamples] = sensorEvent.values[0]; // Gyro x-axis
            sensorData[4][gyroNumSamples] = sensorEvent.values[1]; // Gyro y-axis
            sensorData[5][gyroNumSamples] = sensorEvent.values[2]; // Gyro z-axis
            gyroNumSamples++;

            if (gyroNumSamples >= 300) {
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


//        // Create the JSON object to send in the request body
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("data", data[0]);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        // Request a JSON object response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, postUrl, jsonDataArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println(response);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        System.out.println(error);

                    }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(jsonArrayRequest);
    }
}