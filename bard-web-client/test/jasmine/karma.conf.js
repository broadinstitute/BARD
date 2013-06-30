// Karma configuration
// Generated on Mon Jun 10 2013 15:30:43 GMT-0400 (EDT)


// base path, that will be used to resolve files and exclude
basePath = '';

frameworks: ['jasmine'],
// list of files / patterns to load in the browser
files = [
	 JASMINE,
	 JASMINE_ADAPTER,
	 'lib/jquery/jquery-1.7.1.js',
	 'lib/jquery-ui/jquery-ui-1.8.15.custom.js',
	 'lib/jasmine-jquery.js',
	 '../../web-app/js/search.js',
	 '../../web-app/js/cart.js',
	 'spec/*.js'
	 ];


// list of files to exclude
exclude = [
  
];
preprocessors = {
    '../../web-app/js/search.js':'coverage'
};

// test results reporter to use
// possible values: 'dots', 'progress', 'junit'
reporters = ['junit','coverage'];


// web server port
port = 9876;


// cli runner port
runnerPort = 9100;


// enable / disable colors in the output (reporters and logs)
colors = true;


// level of logging
// possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
logLevel = LOG_INFO;


// enable / disable watching file and executing tests whenever any file changes
autoWatch = true;


// Start these browsers, currently available:
// - Chrome
// - ChromeCanary
// - Firefox
// - Opera
// - Safari (only Mac)
// - PhantomJS
// - IE (only Windows)
browsers = ['Safari','Firefox'];


// If browser does not capture in given timeout [ms], kill it
captureTimeout = 60000;


// Continuous Integration mode
// if true, it capture browsers, run tests and exit
singleRun = false;
