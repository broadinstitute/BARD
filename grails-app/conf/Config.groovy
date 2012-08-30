import grails.util.Environment
import org.apache.log4j.DailyRollingFileAppender


//TODO: Still using Elastic Search here
bard.services.elasticSearchService.restNode.baseUrl = "http://bard-dev-vm:9200"

//TODO: Override in dev, qa and prod to point to the current stable realse
ncgc.server.root.url = "http://bard.nih.gov/api/v2"
ncgc.server.username = "bogus"
ncgs.server.password = "bogus"
ncgc.server.projects.url = "${ncgc.server.root.url}/projects/"
ncgc.server.assays.root.url = "${ncgc.server.root.url}/assays/"
ncgc.server.compounds.root.url = "${ncgc.server.root.url}/compounds/"
ncgc.server.projects.root.url = "${ncgc.server.root.url}/projects/"
ncgc.server.accession.root.url = "${ncgc.server.root.url}/targets/accession/"
ncgc.server.gene.root.url = "${ncgc.server.root.url}/targets/geneid/"
ncgc.server.structureSearch.root.url = "${ncgc.server.root.url}/compounds"
bard.assay.view.url = 'http://localhost:8081/BARD/ESAssay/show'
bard.cap.home='http://localhost:8081/BARD'
grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text/plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
    }
}

grails.serverURL = System.properties.get('grails.serverUrl') ?: getServerUrl()

String getServerUrl() {
    switch (Environment.current.name) {
        case ('production'):
            "http://bard.broadinstitute.org/bardwebquery"
            break
        case ('oracleqa'):
            "http://bard-qa.broadinstitute.org/bardwebquery"
            break
        case ('oracledev'):
            "http://bard-dev.broadinstitute.org/bardwebquery"
            break
        default:
            "http://localhost:8080/bardwebquery"
            break
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
//        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
        appender new DailyRollingFileAppender(
                name: "NCGCResponseTimeAppender",
                file: "logs/" + Environment.current.name + "/NCGC_ResponseTime.log",
                layout: pattern(conversionPattern: '%m%n'),
                immediateFlush: true,
                threshold: org.apache.log4j.Level.INFO,
                datePattern: "'.'yyyy-MM-dd"
        )
    }

    environments {

    }

    //Capture the response time (round-trip + response parsing time) from NCGC API requests.
    info NCGCResponseTimeAppender: 'grails.app.services.elasticsearchplugin.QueryExecutorService'

    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'
}

/**
 * Loads external config files from the .grails subfolder in the user's home directory
 * Home directory in Windows is usually: C:\Users\<username>\.grails
 * In Unix, this is usually ~\.grails
 *
 * bardqueryapi-commons-config.groovy is used to hold generic, non environment-specific configurations such as external api credentials, etc.
 */
if (appName) {
    grails.config.locations = []

    // If the developer specifies a directory for the external config files at the command line, use it.
    // This will look like 'grails -DprimaryConfigDir=[directory name] [target]'
    // Otherwise, look for these files in the user's home .grails/projectdb directory
    // If there are no external config files in either location, don't override anything in this Config.groovy
    String primaryOverrideDirName = System.properties.get('primaryConfigDir')
    String secondaryOverrideDirName = "${userHome}/.grails/${appName}"

    List<String> fileNames = ["${appName}-commons-config.groovy", "${appName}-${Environment.current.name}-config.groovy"]
    fileNames.each {fileName ->
        String primaryFullName = "${primaryOverrideDirName}/${fileName}"
        String secondaryFullName = "${secondaryOverrideDirName}/${fileName}"

        if (new File(primaryFullName).exists()) {
            println "Overriding Config.groovy with $primaryFullName"
            grails.config.locations << "file:$primaryFullName"
        }
        else if (new File(secondaryFullName).exists()) {
            println "Overriding Config.groovy with $secondaryFullName"
            grails.config.locations << "file:$secondaryFullName"
        }
        else {
            println "Skipping Config.groovy overrides: $primaryFullName and $secondaryFullName not found"
        }
    }
}