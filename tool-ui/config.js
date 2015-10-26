System.config({
  baseURL: '/',
  defaultJSExtensions: true,
  transpiler: 'babel',

  //TODO: shims

  paths: {
    'atmosphere': 'bower_components/atmosphere/modules/javascript/src/main/webapp/javascript/atmosphere',
    'bsp-utils': 'bower_components/bsp-utils/bsp-utils',
    'codemirror/*': 'bower_components/codemirror/*',
    'evaporate': 'bower_components/evaporate/evaporate',
    'input/*': 'src/main/webapp/script/input/*',
    'jquery': 'bower_components/jquery/jquery',
    'jquery.extra': 'src/main/webapp/script/jquery.extra',
    'jquery.handsontable.full': 'bower_components/handsontable/dist/jquery.handsontable.full',
    'js.cookie': 'bower_components/js-cookie/src/js.cookie',
    'jsdiff': 'bower_components/jsdiff/diff',
    'leaflet': 'bower_components/leaflet/dist/leaflet.js',
    'leaflet.common': 'src/main/webapp/script/leaflet.common',
    'leaflet.draw': 'bower_components/leaflet.draw/dist/leaflet.draw.js',
    'l.control.geosearch': 'bower_components/L.GeoSearch/src/js/l.control.geosearch',
    'l.geosearch.provider.openstreetmap.js': 'bower_components/L.GeoSearch/src/js/l.geosearch.provider.openstreetmap',
    'L.Control.Locate.js': 'bower_components/leaflet.locatecontrol/dist/L.Control.Locate.min.js',
    'pixastic/*': 'src/main/webapp/script/pixastic/*',
    'spectrum': 'bower_components/spectrum/spectrum',
    'v3/*': 'src/main/webapp/script/v3/*',
    'wysihtml5-0.3.0': 'src/main/webapp/script/wysihtml5-0.3.0'
  }
});
