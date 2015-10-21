var gulp = require('gulp');
var path = require("path");
var settings = require('../settings');
var Builder = require('systemjs-builder');
var exec = require('child_process').exec;

//TODO: minify and add system js to bundle

gulp.task('script', function() {

  exec(settings.jspmScript, function (err, stdout, stderr) {
    console.log(stdout);
    console.log(stderr);
  });
});