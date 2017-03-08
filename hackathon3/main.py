from flask import Flask, send_file, request, jsonify
from sklearn.linear_model import Ridge
from sklearn.preprocessing import PolynomialFeatures
from sklearn.pipeline import make_pipeline
import json
app = Flask(__name__)

inputs = [ [10, 0, 1, 8], [10, 0, 2, 9], [10, 1, 1, 13], [5, 0, 1, 15], [5, 1, 5, 16], [5, 1, 6, 18] ]
outputs = [8, 7, 5, 6, 9, 9]

model = make_pipeline(PolynomialFeatures(2), Ridge())

@app.route("/start")
def start():
    return send_file('index.html')

@app.route("/handleinput")
def handleinput():
    myList = json.loads(request.args.get('list'))
    new_input = [int(myList[0]), int(myList[1]), int(myList[2]), int(myList[3])]
    new_result = int(myList[4])
    inputs.append(new_input)
    outputs.append(new_result)
    return jsonify(result = myList)

@app.route("/handlepredict")
def handlepredict():
    model.fit(inputs, outputs)
    myList = json.loads(request.args.get('predicts'))
    pred = [int(myList[0]), int(myList[1]), int(myList[2]), int(myList[3])]
    prediction = model.predict([ pred ])
    myList = [prediction[0]]
    return jsonify(result = myList)

app.run(host='0.0.0.0')