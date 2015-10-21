var gulp = require('gulp');
var path = require("path");
var settings = require('../settings');
var Builder = require('systemjs-builder');
var exec = require('child_process').exec;

//TODO: minify/non-minify bundles

gulp.task('script', function() {

  exec('jspm bundle-sfx src/main/webapp/script/' + settings.theme + '.js ' + settings.minJsDest + settings.theme + '.js --inject',
    function (err, stdout, stderr) {
      console.log(stdout);
      console.log(stderr);
    });
});