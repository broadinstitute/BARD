grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
reportOnTestFailureOnly = true
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.server.port.http=9091
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

	def gebVersion = "0.9.0"
	def seleniumVersion = "2.35.0"
	def spockVersion = "0.7"
	
    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
	
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

//        runtime 'mysql:mysql-connector-java:5.1.20'
		build 'com.oracle:ojdbc6:11.2.0.2.0'
		test("org.seleniumhq.selenium:selenium-htmlunit-driver:$seleniumVersion") {
			excludes "xml-apis", "commons-io"
		}
		test("org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion"){
			exclude "xml-apis"
		}
		test("org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion"){
			excludes "xml-apis"//, "commons-io"
		}
		test("org.seleniumhq.selenium:selenium-ie-driver:$seleniumVersion"){
			exclude "xml-apis"
		}
		test ("org.seleniumhq.selenium:selenium-support:$seleniumVersion"){
			exclude "xml-apis"
		}
//		test("org.seleniumhq.selenium:selenium-safari-driver:$seleniumVersion")
//		test("org.seleniumhq.selenium:selenium-opera-driver:$seleniumVersion")
		// You usually only need one of these, but this project uses both
		test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
		test "org.gebish:geb-spock:$gebVersion"
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.10.2"
        runtime ":resources:1.2"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:$grailsVersion"
        runtime ":database-migration:1.3.5"
        compile ':cache:1.1.1'
		
		test ":geb:$gebVersion"
		test (":spock:$spockVersion"){
			exclude "spock-grails-support"
		}
//		test ":remote-control:1.4"
    }
}
