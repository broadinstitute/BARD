Please put all external javascript libraries in the lib folder.  jsLint will be configured to ignore these.

The convention is to put the js files in a subfolder that has the version number of the library in it (e.g., FixedHeader-2.0.6).

This convention makes it easier to figure out which version of a library you're working with.

Finally, add a reference to the library in the bardqueryapiResources.groovy file so that the Resources plug-in can pick it up.
