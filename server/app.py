from flask import Flask, request, jsonify
import json
import os
import numpy as np
import pickle

app = Flask(__name__)

ROOT_DIR = os.getcwd()

rocket_classifier = pickle.load(open("rocket_model.pkl", "rb"))

def preprocess_data(data):

    '''
    normalise the sensor data recieved from the request and split it into 3 seperate data instances to predict on
    '''

    data = np.array(data)
    normalised_data = []


    for axis in data:
        axis = axis[50:150]
        axis = (axis-axis.min())/(axis.max()-axis.min())
        normalised_data.append(axis)


    normalised_data = np.array([normalised_data])
    print(normalised_data)

    return normalised_data




@app.route('/predict', methods=['POST'])

def predict():

    data = request.get_json()

    sensorData = preprocess_data(data)

    print(sensorData)

    prediction = rocket_classifier.predict(sensorData)
    

    print(prediction)


    return list(prediction)



@app.route('/test_predict', methods=['POST'])

def test_predict():

    data = request.get_json()

    data = np.array(data)

    prediction = rocket_classifier.predict(data)
    prediction_probabilties = rocket_classifier.predict_proba(data)


    print(prediction)
    print(prediction_probabilties)

    response = [prediction[0], prediction_probabilties[0]]

    return jsonify(list(response))



if __name__ == '__main__':
    app.run()

