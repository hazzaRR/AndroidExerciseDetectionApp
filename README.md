## Prototype Android App 
To use the prototype Android app, please follow the instructions below:

## Prerequisites
- Ensure that you have activated the Conda environment by running the following command:

```
conda activate gym_classification
```
- Make sure you have set up the Flask server by running the following command in the root of the server directory:

```
flask run --host=0.0.0.0
```

## Configuring the Android App

1. In the root directory of the project, navigate to the server directory.

2. Start the Flask server by running the following command:

```
flask run --host=0.0.0.0
```

This will start the Flask server on your local machine.

3. Once the server is running, copy the IP address displayed at the bottom of the terminal/console.

4. Open the MainActivity.java file located in the front-end code of the Android app.

5. Look for line 122 in the MainActivity.java file, which should contain the URL for the Flask server.

6. Replace the existing IP address in the URL with the IP address you copied in step 3.

line 122 should look like this once completed : sendPostRequest("`your_url`/predict", sensorData);


## Building and Running the Android App

1. Enable developer mode and USB debugging on your Android phone. Refer to the official documentation for your specific Android device on how to enable these options.

2. Connect your Android phone to your computer via USB.

3. Open the project in Android Studio.

4. Build the app onto your Android phone using Android Studio.

5. Once the app is successfully installed on your phone, you can launch and use it for gym exercise classification.
