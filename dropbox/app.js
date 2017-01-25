/**
 * Created by David on 1/23/2017.
 */
var express = require('express')
var AWS = require('aws-sdk')
var fs = require('fs')
var s3 = new AWS.S3();
var app = express()

var myBucket = 'silvan9145bucket'; //the S3 bucket
var dir = '/home/ec2-user/dropbox/'; //the designated directory

//watch file changes
fs.watch(dir, function (event, filename) {
    //if a file is created or deleted
    if (event == 'rename') {
        fs.exists(dir + filename, function (exists) {
            //a file has been created, upload it to the S3 bucket
            if (exists) {
                console.log(filename + ' added');
                uploadFileToS3(filename);
            }
            //a file has been deleted, delete it from the S3 bucket
            else {
                console.log(filename + ' deleted');
                deleteFileFromS3(filename);
            }
        });
    }
    //if a file's contents are updated, upload the updated file to the S3 bucket
    else {
        console.log(filename + ' has been updated');
        uploadFileToS3(filename);
    }
});

//Enable CORS
app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});

app.get('/', function (req, res) {
    res.sendfile('test.html')
});

//list all of the objects (and their data) from the S3 bucket in formatted JSON
app.get('/list', function(req, res){
    var params = {Bucket: myBucket};
    s3.listObjects(params, 	function(err, data){
        for (var i = 0; i < data.Contents.length; i++) {
            data.Contents[i].Url = 'https://s3-us-west-1.amazonaws.com/' + data.Name + '/' + data.Contents[i].Key;
        }
        res.setHeader('Content-Type', 'application/json');
        res.send(JSON.stringify(data.Contents, null, 3));
    })
});

//uploads a file as an object to the S3 bucket
function uploadFileToS3(filename) {
    fs.readFile(dir + filename, function (err, data) {
        var params = {Bucket: myBucket, Key: filename, Body: data, ACL: "public-read"};
        s3.putObject(params, function(err, data) {
            if (err) {
                console.log(err)
            } else {
                console.log("Successfully uploaded data to " + myBucket, data);
            }
        });
    });
}

//deletes an object (file) from the S3 bucket
function deleteFileFromS3(filename) {
    fs.readFile(dir + filename, function (err, data) {
        var params = {Bucket: myBucket, Key: filename};
        s3.deleteObject(params, function(err, data) {
            if (err) {
                console.log(err)
            } else {
                console.log("Successfully deleted data from " + myBucket, data);
            }
        });
    });
}

app.listen(3000, function () {
    console.log('Example app listening on port 3000!')
});