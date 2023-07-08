import os
from sktime.datasets import load_from_tsfile
from sktime.classification.kernel_based import RocketClassifier
import numpy as np
import pickle


def main():

    ROOT_DIR = os.getcwd()

    print(ROOT_DIR)

    DATASET_PATH = os.path.join(ROOT_DIR, 'datasets','Harry_gym_movements_TRAIN.ts',)
    DATASET_PATH_TEST = os.path.join(ROOT_DIR, 'datasets', 'Harry_gym_movements_TEST.ts')

    rocket_classifier = RocketClassifier(num_kernels=1000)


    X_train, y_train = load_from_tsfile(DATASET_PATH, return_data_type="numpy3D")
    X_test, y_test = load_from_tsfile(DATASET_PATH_TEST, return_data_type="numpy3D")


    # print(np.shape(X_test[0:1]))

    rocket_classifier.fit(X_train, y_train)


    pickle.dump(rocket_classifier, open("./rocket_model.pkl", "wb"))



    









if __name__ == "__main__":
    main()
