var request = require('request');
var AWS = require('aws-sdk');

AWS.config.update({
    region: "us-west-1"
});

'use strict';

var docClient = new AWS.DynamoDB.DocumentClient();
var allTimes = "all_times"; //table that holds all of the times of every bus location
var recentTimes = "recent_times"; //table that holds the most recent bus locations
var api = "https://rqato4w151.execute-api.us-west-1.amazonaws.com/dev/info";

module.exports.updateDB = (event, context, callback) => {
    request(api, function(error, response, body) {
        if (!error && response.statusCode == 200) {
            var text = JSON.parse(body);
            for (var i = 0; i < text.length; i++) {
                putItem(allTimes, text[i]);
                putItem(recentTimes, text[i]);
            }
            const resp = {
                statusCode: 200,
                headers: {
                    "Access-Control-Allow-Origin": "*"
                },
                body: body
            };
            callback(null, resp);
        }
        else {
            console.log("Error with request..");
        }
    })
};

module.exports.getTimes = (event, context, callback) => {
    var params = {
        TableName: recentTimes,
    };
    console.log("Scanning table...");
    docClient.scan(params, function(err, data) {
        if (err) {
            console.log("Error scanning...");
            const response = {
                statusCode: 200,
                headers: {
                    "Access-Control-Allow-Origin": "*"
                },
                body: "Error: Couldn't get shuttle times..."
            };
            callback(null, response);
        }
        else {
            console.log("Scanning done...");
            const resp = {
                statusCode: 200,
                headers: {
                    "Access-Control-Allow-Origin": "*"
                },
                body: JSON.stringify(data.Items)
            };
            callback(null, resp);
        }
    });
};

function putItem(tableName, bus) {
    var params;
    if (tableName == allTimes) {
        params = {
            TableName: tableName,
            Item: {
                "id": bus.id,
                "timestamp": Date.now(),
                "logo": bus.logo,
                "lat": bus.lat,
                "lng": bus.lng,
                "route": bus.route
            }
        };
    }
    else if (tableName == recentTimes) {
        params = {
            TableName: tableName,
            Item: {
                "id": bus.id,
                "logo": bus.logo,
                "lat": bus.lat,
                "lng": bus.lng,
                "route": bus.route
            }
        };
    }

    console.log("Adding a new item...");
    docClient.put(params, function(err, data) {
        if (err) {
            console.error("Unable to add item. Error: ", JSON.stringify(err, null, 2));
        } else {
            console.log("Added item: ", JSON.stringify(data, null, 2));
        }
    });
}