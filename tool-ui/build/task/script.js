var gulp = require('gulp');
var path = require('path');
var settings = require('../settings');
var exec = require('child_process').exec;
var Builder = require('systemjs-builder');

gulp.task('script', function() {

  var appJsFileName = settings.theme + '.js';
  var appJsSrc = settings.jsSrc + '**/*.js';

  var appJsDest = settings.jsDest + appJsFileName;
  var appJsMinDest = settings.minJsDest + appJsFileName;

  var builder = new Builder(settings.root, settings.root + 'config.js');

  builder.buildStatic(appJsSrc, appJsDest, { minify: false, sourceMaps: true });
  builder.buildStatic(appJsSrc, appJsMinDest, { minify: true, sourceMaps: true });

  //builder.bundle(settings.jsSrc + '/**/* - [' + settings.jsSrc + "]", dep.js);

  //Dependency bundle
  ////builder.bundle('app/**/* - [app/**/*]', 'dependencies.js', {minify: true, sourceMaps: true});
  //builder.bundle(settings.jsSrc + '**/*', settings.minJsDest + 'all.js');
  //builder.buildStatic(settings.jsSrc + 'v3.js', settings.minJsDest + 'v3.js');
});