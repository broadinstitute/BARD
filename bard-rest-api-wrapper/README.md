bard-client
===========

This is an in-place plugin for the bardwebclient

Testing:
--------

In your .grails directory, create a folder bard-rest-api-wrapper, then create a
file bard-rest-api-wrapper-commons-config.groovy.
In this file add the following:
```
ncgc.server.root.url = THE_URL_TO_THE_NCGC_SERVER //for example "http://bard.nih.gov/api/v12"
promiscuity.badapple.url = "${ncgc.server.root.url}/plugins/badapple/prom/cid/" //URL to promiscuity plugin

dataexport.apikey=YOUR_DATA_EXPORT_API_KEY
dataexport.element.accept.type="application/vnd.bard.cap+xml;type=element"
dataexport.element.url=THE_URL_TO_THE_ELEMENT_RESOURCE 
```
For example on QA it is //"https://bard-qa.broadinstitute.org/dataExport/api/dictionary/element/"
Replace the variables with the appropriate values

Open a grails command window into this directory and run:
```
grails test-app unit: integration:
```
If you are using an IDE like Intellij you can use its grails command to run the tests

Deploying:
----------

To build the application for deployment on Tomcat use the command:
```
grails -Dgrails.env=xxx war
```
[Q: what is our preferred environment for production deployment?]

Dependencies:
-------------
```
Spock
Codenarc
Clover
```
[Q: what other dependencies?]


