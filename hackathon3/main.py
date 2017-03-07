from flask import Flask, send_file
from sklearn.linear_model import Ridge
from sklearn.preprocessing import PolynomialFeatures
from sklearn.pipeline import make_pipeline
app = Flask(__name__)

input = [ [10, 0, 1, 8], [10, 0, 2, 9], [10, 1, 1, 13], [5, 0, 1, 15], [5, 1, 5, 16], [5, 1, 6, 18] ]
output = [8, 7, 5, 6, 9, 9]

@app.route("/input")
def input():
    return send_file('index.html')

@app.route("/predict")
def predict():
    model = make_pipeline(PolynomialFeatures(2), Ridge())
    model.fit(input, output)
    prediction = model.predict([ [10, 0, 1, 9] ])
    return str(prediction[0])

if __name__ == "__main__":
    #predict()
    app.run()