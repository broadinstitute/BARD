# Building and Installing BARD

## Prerequisites
Before starting to build and installing BARD from source, you will need the following installed:

1. Grails 2.2.1 http://grails.org/
2. Maven http://maven.apache.org/ (Used to install 3rd party jars into a local repo)
3. An oracle database and an oracle user created with permissions to create views, triggers, stored procedures, function, and tables.
4. Redis 2.8.4 http://redis.io/
5. Tomcat 7.0.50 http://tomcat.apache.org/

## Installation

### Download and install the Oracle JDBC driver
The oracle java jdbc driver downloaded from http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html

The driver should be free, however, the license does not allow us to redistribute the driver, so you must download from oracle's site after agreeing to their license.

Install the oracle driver in your local maven repo with the following command:

```
mvn install:install-file -Dfile=ojdbc6-11.2.0.2.0.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.2.0 -Dpackaging=jar
```

### Download instant JChem from ChemAxon and install jars into local maven repo
This will require you register with ChemAxon.   Download instant JChem version 5.10.4, and specifically download the `instantjchem-5_10_4.zip` file.   Extract the zip file and execute the following to import the needed jars into your repo.

```
mvn install:install-file -Dfile=instantjchem/instantjchem/modules/ext/MarvinBeans-concurrent.jar -DgroupId=ChemAxon -DartifactId=MarvinBeans-concurrent -Dversion=5.10.4 -Dpackaging=jar
mvn install:install-file -Dfile=instantjchem/instantjchem/modules/ext/MarvinBeans-diverse-modules.jar -DgroupId=ChemAxon -DartifactId=MarvinBeans-diverse-modules -Dversion=5.10.4 -Dpackaging=jar
mvn install:install-file -Dfile=instantjchem/instantjchem/modules/ext/MarvinBeans-formats-peptide.jar -DgroupId=ChemAxon -DartifactId=MarvinBeans-formats-peptide -Dversion=5.10.4 -Dpackaging=jar
mvn install:install-file -Dfile=instantjchem/instantjchem/modules/ext/MarvinBeans-formats-smiles.jar -DgroupId=ChemAxon -DartifactId=MarvinBeans-formats-smiles -Dversion=5.10.4 -Dpackaging=jar
mvn install:install-file -Dfile=instantjchem/instantjchem/modules/ext/MarvinBeans-formats.jar -DgroupId=ChemAxon -DartifactId=MarvinBeans-formats -Dversion=5.10.4 -Dpackaging=jar
mvn install:install-file -Dfile=instantjchem/instantjchem/modules/ext/MarvinBeans-formats.cml.jar -DgroupId=ChemAxon -DartifactId=MarvinBeans-formats.cml -Dversion=5.10.4 -Dpackaging=jar
mvn install:install-file -Dfile=instantjchem/instantjchem/modules/ext/MarvinBeans-formats.image.jar -DgroupId=ChemAxon -DartifactId=MarvinBeans-formats.image -Dversion=5.10.4 -Dpackaging=jar
mvn install:install-file -Dfile=instantjchem/instantjchem/modules/ext/MarvinBeans-license.jar -DgroupId=ChemAxon -DartifactId=MarvinBeans-license -Dversion=5.10.4 -Dpackaging=jar
mvn install:install-file -Dfile=instantjchem/instantjchem/modules/ext/MarvinBeans-plugin.jar -DgroupId=ChemAxon -DartifactId=MarvinBeans-plugin -Dversion=5.10.4 -Dpackaging=jar
mvn install:install-file -Dfile=instantjchem/instantjchem/modules/ext/MarvinBeans.jar -DgroupId=ChemAxon -DartifactId=MarvinBeans -Dversion=5.10.4 -Dpackaging=jar
``` 

### Check code out of github

Use git to clone the BARD repo locally.

```
git clone https://github.com/broadinstitute/BARD.git
```

In the following, I'll refer to the checked out directory (BARD) as $BARD_CHECKOUT


### Build external lookup api for querying external ontologies and install jar in local maven repo
Change to the external-valuation directory

```
cd $BARD_CHECKOUT/external-validation
./gradlew api:install
Create local configuration files
mkdir ~/.grails
mkdir ~/.grails/BARD
mkdir ~/.grails/dataExport
cp $BARD_CHECKOUT/sample_configs/BARD/BARD-production-config.groovy ~/.grails/BARD
cp $BARD_CHECKOUT/sample_configs/dataExport/dataExport-production-config.groovy ~/.grails/dataExport
```

Edit the files in ~/.grails/BARD and ~/.grails/dataExport replacing the following text with values for:
* DATABASE_HOST - The host the oracle database is running on
* DATABASE_SID - The oracle SID on that host to connect to
* USERNAME - The oracle user within the database which will hold the data for BARD
* PASSWORD - The oracle user password
* RESULTS_PATH - The path to a directory where results can be saved for transfering to the warehouse.  The amount of space needed will depend on the volume of results loaded.
* WEB_SERVER_HOSTNAME - The name of the server which the webserver is running on.
* WAREHOUSE_API - The URL of the local warehouse API.  (for example "http://bard.nih.gov/api" is the value used by public BARD)
 
### Build and deploy BARD application
```
cd $BARD_CHECKOUT/BARD
grails compile
```


### Set up database

The following will create all the necessary objects in the configured schema and populate reference data.

```
grails -Dgrails.env=production dbm-update --contexts=standard,insert-reference
Create BARD war and deploy
grails war BARD.war
```

Copy the BARD.war to your tomcat's webapp directory to deploy it.


### Create dataExport API war and deploy
```
cd $BARD_CHECKOUT/dataExport
grails war dataExport.war
```
Copy the dataExport.war to your tomcat's webapp directory to deploy it


### Set up environment variable for Tomcat
Add the following to the environment by editing your .bashrc file or simply executing the following before starting tomcat to increase the amount of memory that BARD is allowed to use.

```
export CATALINA_OPTS="-server -Djava.awt.headless=true -Xmx1024m -XX:MaxPermSize=512m"
```

With the WAR files in place, you can now start tomcat, and BARD should be accessible as "/BARD" once tomcat has finished starting.

 
