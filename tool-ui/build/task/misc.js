var gulp = require('gulp');
var fs = require('fs');
var rename = require('gulp-rename');
var runSequence = require('run-sequence');
var settings = require('../settings');

gulp.task('misc', function(callback) {
  return runSequence(
    'less-js',
    callback
  );
});

gulp.task('less-js', function() {
  return gulp.src(settings.lessJsSrc)
    .pipe(rename('less.js'))
    .pipe(gulp.dest(settings.lessJsDest));
});