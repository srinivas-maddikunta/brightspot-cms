var path = require('path');
var argv = require('yargs').argv;

var root = path.join(__dirname, '../');
var webappRoot = root + 'src/main/webapp/';

var theme = 'v3';

var target = 'target/' + argv['bsp-maven-build-finalName'] + '/';
var nodeModulesRoot = root + 'node_modules/';

var scriptSrc = webappRoot + 'script/';
var scriptDest = target + 'script/';
var scriptMinDest = target + 'script.min/';

var styleSrc = webappRoot + 'style/';
var styleDest = target + 'style/';

module.exports = {

  theme: theme,

  lessSrc: styleSrc,
  cssDest: styleDest,

  jsSrc: scriptSrc,
  jsDest: scriptMinDest,

  lessJsSrc: nodeModulesRoot + 'gulp-less/node_modules/less/dist/less.min.js',
  lessJsDest: scriptDest,

  jspmScript: 'jspm bundle src/main/webapp/script/' + theme + '.js ' + scriptMinDest + theme + '.js'
};