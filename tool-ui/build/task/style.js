var gulp = require('gulp');
var gutil = require('gulp-util');
var less = require('gulp-less');
var settings = require('../settings');
var sourcemaps = require('gulp-sourcemaps');
var path = require('path');
var fs = require('fs');
var rename = require('gulp-rename');

//TODO: minify, autoprefix, sourcemaps

gulp.task('style', function () {
  return gulp.src(settings.lessSrc + '*.less')
    //.pipe(sourcemaps.init())
    .pipe(less())
    .pipe(rename('v3.min.css'))
    //.pipe(sourcemaps.write())
    .pipe(gulp.dest(settings.cssDest));
});