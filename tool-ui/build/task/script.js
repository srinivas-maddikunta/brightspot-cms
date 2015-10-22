var gulp = require('gulp');
var path = require("path");
var settings = require('../settings');
var exec = require('child_process').exec;

var jspm = require('jspm');

gulp.task('script', function() {

  var appJsFileName = settings.theme + '.js';
  var appJsSrc = settings.jsSrc + appJsFileName;

  var appJsDest = settings.jsDest + appJsFileName;
  var appJsMinDest = settings.minJsDest + appJsFileName;

  jspm.setPackagePath(settings.root);
  var builder = new jspm.Builder(settings.jsSrc, settings.root + 'config.js');

  builder.bundle(appJsSrc, appJsDest, { minify: false, sourceMaps: true });
  builder.bundle(appJsSrc, appJsMinDest, { minify: true, sourceMaps: true });

  //builder.bundle(settings.jsSrc + '/**/* - [' + settings.jsSrc + "]", dep.js);

  //Dependency bundle
  ////builder.bundle('app/**/* - [app/**/*]', 'dependencies.js', {minify: true, sourceMaps: true});
  //builder.bundle(settings.jsSrc + '**/*', settings.minJsDest + 'all.js');
  //builder.buildStatic(settings.jsSrc + 'v3.js', settings.minJsDest + 'v3.js');
});