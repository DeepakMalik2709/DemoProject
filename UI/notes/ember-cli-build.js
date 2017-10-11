/*jshint node:true*/
/* global require, module */
var EmberApp = require('ember-cli/lib/broccoli/ember-app');

module.exports = function(defaults) {
  var app = new EmberApp(defaults, {
    // Add options here
  });
  

/*  app.import('vendor/AdminLTE/css/bootstrap.min.css');  
  app.import('vendor/AdminLTE/css/font-awesome.min.css');
  app.import('vendor/AdminLTE/css/ionicons.min.css');
  /*app.import('vendor/AdminLTE/css/morris/morris.css');
  app.import('vendor/AdminLTE/css/jvectormap/jquery-jvectormap-1.2.2.css');*/
  app.import('vendor/underscore.min.js');
  
  app.import('vendor/AdminLTE/css/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css');
 
  
 /* app.import('vendor/AdminLTE/js/jquery.min.js');  
  app.import('vendor/AdminLTE/js/jquery-ui-1.10.3.min.js');
  app.import('vendor/AdminLTE/js/bootstrap.min.js');
  app.import('vendor/AdminLTE/js/raphael-min.js');
  app.import('vendor/AdminLTE/js/plugins/morris/morris.min.js');
  app.import('vendor/AdminLTE/js/plugins/sparkline/jquery.sparkline.min.js');
  app.import('vendor/AdminLTE/js/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js');
  app.import('vendor/AdminLTE/js/plugins/jvectormap/jquery-jvectormap-world-mill-en.js');

  app.import('vendor/AdminLTE/js/plugins/jqueryKnob/jquery.knob.js');
*/
  app.import('vendor/AdminLTE/js/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js');
 /* app.import('vendor/AdminLTE/js/plugins/iCheck/icheck.min.js');
  app.import('vendor/AdminLTE/js/app.js');
  app.import('vendor/AdminLTE/js/dashboard.js');*/
  app.import('vendor/common.js');
  
 /*  app.import('vendor/fonts/fontawesome-webfont.eot', {
	  destDir: 'fonts'
  });
   app.import('vendor/fonts/fontawesome-webfont.svg', {
	  destDir: 'fonts'
  });
    app.import('vendor/fonts/fontawesome-webfont.ttf', {
	  destDir: 'fonts'
  });
    app.import('vendor/fonts/fontawesome-webfont.woff', {
	  destDir: 'fonts'
  });*/
   
/*   app.import('vendor/fonts/ionicons.ttf', {
	  destDir: 'fonts'
  });
    app.import('vendor/fonts/ionicons.woff', {
	  destDir: 'fonts'
  });*/
     


  app.import('vendor/font-awesome/fonts/fontawesome-webfont.eot', {
	  destDir: 'fonts'
  });
  app.import('vendor/font-awesome/fonts/fontawesome-webfont.woff2', {
	  destDir: 'fonts'
  });
  app.import('vendor/font-awesome/fonts/fontawesome-webfont.woff', {
	  destDir: 'fonts'
  });
  app.import('vendor/font-awesome/fonts/fontawesome-webfont.ttf', {
	  destDir: 'fonts'
  });
  app.import('vendor/font-awesome/fonts/fontawesome-webfont.svg', {
	  destDir: 'fonts'
  });
  app.import('vendor/font-awesome/css/font-awesome.min.css');
 
  // Use `app.import` to add additional libraries to the generated
  // output files.
  //
  // If you need to use different assets in different
  // environments, specify an object as the first parameter. That
  // object's keys should be the environment name and the values
  // should be the asset to use in that environment.
  //
  // If the library that you are including contains AMD or ES6
  // modules that you would like to import into your application
  // please specify an object with the list of modules as keys
  // along with the exports of each module as its value.

  return app.toTree();
};
