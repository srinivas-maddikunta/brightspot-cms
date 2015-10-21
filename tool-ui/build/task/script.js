var gulp = require('gulp');
var path = require("path");
var settings = require('../settings');
var Builder = require('systemjs-builder');

gulp.task('script', function() {
  var builder = new Builder();

  //builder.loadConfig(path.join(__dirname, '../../config.js'))
  //  .then(function() {
  //    return builder.buildStatic(
  //      path.join(settings.jsSrc, settings.theme + '.js'),
  //      path.join(settings.jsDest, settings.theme + 'min.js'),
  //      {
  //        minify: false,
  //        sourceMaps: true
  //      }
  //    )
  //  })
  //  .catch(function(e) {
  //    console.log(e);
  //  })
});