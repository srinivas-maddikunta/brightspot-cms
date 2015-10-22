var gulp = require('gulp');
var path = require("path");
var settings = require('../settings');
var Builder = require('systemjs-builder');
var exec = require('child_process').exec;

//TODO: minify/non-minify bundles

gulp.task('script', function() {

  var jspmBundleScript = 'jspm bundle ' + settings.jsSrc + settings.theme + '.js ' + settings.minJsDest + settings.theme + '.js';

  console.log('JSPM BUNDLE SCRIPT: ' + jspmBundleScript);

  exec(jspmBundleScript,
    function (err, stdout, stderr) {
      console.log(stdout);
      console.log(stderr);
    });
});