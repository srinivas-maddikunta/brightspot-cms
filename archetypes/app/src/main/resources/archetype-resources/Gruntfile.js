'use strict';

module.exports = function (grunt) {
    require('bsp-grunt')(grunt, {
        bsp: {
            
            styles: {
                dir: 'assets',
                less: [ '*.less' ],
                autoprefixer: true,
                options: {
                    autoprefixer: true
                }
            },

            scripts: {
                dir: 'assets'
            },

            bower: {
                'normalize-css': [
                    {
                        src: 'normalize.css',
                        dest: 'bower/normalize-css/normalize.css'
                    }
                ]
            }
        }
    });
};
