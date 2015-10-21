var path = require('path');
var argv = require('yargs').argv;

var root = path.join(__dirname, '../');
var webappRoot = root + 'src/main/webapp/';

var theme = 'v3';

var target = 'target/' + argv['bsp-maven-build-finalName'] + '/';
var nodeModulesRoot = root + 'node_modules/';

var scriptSrc = webappRoot + 'script/';
var scriptDest = target + 'script/';

var styleSrc = webappRoot + 'style/';
var styleDest = target + 'style/';

module.exports = {

  lessSrc: styleSrc,
  cssDest: styleDest,

  jsSrc: scriptSrc,
  jsDest: scriptDest,

  lessJsSrc: nodeModulesRoot + 'gulp-less/node_modules/less/dist/less.min.js',
  lessJsDest: scriptDest
};