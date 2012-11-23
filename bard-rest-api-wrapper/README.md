bard-client
===========

This repository contains Java client code forked from [BARD](https://github.com/ncatsdpiprobedev/bard) REST api. 
To compile, simply type
```
./gradlew compile
```

To run test cases for the REST implementation, type
```
./gradlew tests
```
or run it from intellij or eclipse

To generate idea files do:

```
./gradlew idea
```

To generate eclipse files do:
```
./gradlew eclipse
```

jar files
=========

To create a jar file for distribution 
first edit gradle.properties to add the version.

Note that the version should correspond to the REST-API version.

So if the REST-API is at version 7 simply replace the version value in the properties file with v7.
then type :
```
./gradlew jar
```

This creates a jar file in the build/lib directory
bard-client-version.jar

Where version is the version you specified in the property file


dependencies
============

For the REST implementation, the following jar files in the ```lib```
directory are required:

```
httpcore-4.1.jar
httpclient-4.1.1.jar
commons-logging-1.1.1.jar
jackson-core-asl-1.9.2.jar
jackson-mapper-asl-1.9.2.jar
jchem.jar
````

Note that ```jchem.jar``` is required only if the
```CompoundService``` is used.

Also note that we use our own jchem.jar file named ChemAxonJChemBase-5.10.jar which is located in the lib directory

