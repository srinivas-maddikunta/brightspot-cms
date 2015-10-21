var gulp = require('gulp');
var fs = require('fs');
var rename = require('gulp-rename');
var runSequence = require('run-sequence');
var settings = require('../settings');

gulp.task('misc', function(callback) {
  return runSequence(
    'less-js',
    'system-js',
    callback
  );
});

gulp.task('less-js', function() {
  return gulp.src(settings.nodeModulesRoot + 'gulp-less/node_modules/less/dist/less.min.js')
    .pipe(rename('less.js'))
    .pipe(gulp.dest(settings.jsDest));
});

gulp.task('system-js', function() {
  return gulp.src(settings.jspmModulesRoot + 'system.js')
    .pipe(gulp.dest(settings.jsDest));
});

//TODO: copy additional js files to target directory