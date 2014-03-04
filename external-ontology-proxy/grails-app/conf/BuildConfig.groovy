grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherit(false) // don't repositories from plugins
        grailsPlugins()
        grailsHome()
        mavenRepo "http://bard-repo.broadinstitute.org:8081/artifactory/bard-virtual-repo"
        grailsRepo("http://bard-repo.broadinstitute.org:8081/artifactory/bard-virtual-repo", "grailsCentral")
    }

    dependencies {
        def externalValidationVersion = 20140227
        compile "bard:external-validation-api:${externalValidationVersion}"
        // excluding icu4j transitive dependency
        // was seeing SEVERE: Unable to process Jar entry [com/ibm/icu/impl/data/LocaleElements_zh__PINYIN.class] from Jar [jar:file:/local/bard/server/external-ontology-proxy/apache-tomcat-7.0.47/webapps/external-ontology-proxy/WEB-INF/lib/icu4j-2.6.1.jar!/] for annotations
        // org.apache.tomcat.util.bcel.classfile.ClassFormatException: Invalid byte tag in constant pool: 60
        // http://stackoverflow.com/questions/6751920/tomcat-7-servlet-3-0-invalid-byte-tag-in-constant-pool
        compile ("bard:external-validation-impl:${externalValidationVersion}"){
            excludes "icu4j"
        }
        compile "com.ibm.icu:icu4j:3.4.4"

        compile "org.apache.commons:commons-lang3:3.2"
        compile "xalan:xalan:2.7.0" // http://jira.grails.org/browse/GRAILS-9740 ( junitreporter fails when Xalan 2.6.0 dependency is declared in Java 1.7.0 env )
        compile 'log4j:apache-log4j-extras:1.2.17'

        compile "com.cenqua.clover:clover:3.1.12" // Clover core JAR
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
    }

    plugins {
        build ":tomcat:$grailsVersion"

//        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.8.3"
        runtime ":resources:1.1.6"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.5"
        runtime ":database-migration:1.3.2"

        compile ':cache:1.0.1'
        compile ":clover:3.1.12"
        compile ":greenmail:1.3.3"

        // test scope
        test(":spock:0.7") {
            exclude "spock-grails-support"
        }
    }
}

clover {
    // if you want to use clover put a valid license in the .grails dir in the home dir for this environment
    final String separator =  System.getProperty("file.separator")
    license.path = "${System.getProperty("user.home")}${separator}.grails${separator}clover.license"
    directories: ['src/java', 'src/groovy', 'grails-app']
    includes = ['**/*.groovy', '**/*.java']
}
