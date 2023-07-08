import os
from sktime.datasets import load_from_tsfile
import requests
import json


def main():

    ROOT_DIR = os.getcwd()

    DATASET_PATH_TEST = os.path.join(ROOT_DIR, 'datasets', 'Harry_gym_movements_TEST.ts')

    X_test, y_test = load_from_tsfile(DATASET_PATH_TEST, return_data_type="numpy3D")


    print(X_test)

    headers = {'Content-type': 'application/json'}

    response = requests.post('http://localhost:5000/test_predict', data=json.dumps(X_test[62:63].tolist()), headers=headers)
    # response = requests.post(' https://harry.max-d.co.uk/test_predict', data=json.dumps(X_test[62:63].tolist()), headers=headers)
    print(response.text)


    









if __name__ == "__main__":
    main()