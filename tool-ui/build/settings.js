var path = require('path');
var argv = require('yargs').argv;

var root = path.join(__dirname, '../');
var webappRoot = root + 'src/main/webapp/';

var theme = 'v3';
var target = 'target/' + argv['bsp-maven-build-finalName'] + '/';

module.exports = {

  theme: theme,
  root: root,

  lessSrc: webappRoot + 'style/',
  cssDest: target + 'style/',

  nodeModulesRoot: root + 'node_modules/',

  jsSrc: webappRoot + 'script/',
  jsDest: target + 'script/',
  minJsDest: target + 'script.min/'
};