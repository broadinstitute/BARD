import grails.util.Environment
import org.apache.log4j.DailyRollingFileAppender

//TODO: Override in dev, qa and prod to point to the current stable release
ncgc.server.root.url = "http://bard.nih.gov/api/v9"
promiscuity.badapple.url="${ncgc.server.root.url}/plugins/badapple/prom/cid/"
//override in config file for environment
server.port = System.properties.getProperty('server.port') ?: 8080
grails.serverURL = "http://localhost:${server.port}/bardwebclient"
//URL to the ROOT of the cap server
bard.cap.home = "http://localhost:8081/BARD/"
bard.cap.assay = "${bard.cap.home}assayDefinition/show/"


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false

grails.mime.types = [
        html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text/plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        pdf: 'application/pdf',
        rtf: 'application/rtf',
        excel: 'application/vnd.ms-excel',
        ods: 'application/vnd.oasis.opendocument.spreadsheet',
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
    info NCGCResponseTimeAppender: ['grails.app.services.bardqueryapi.QueryService', 'grails.app.services.elasticsearchplugin.QueryExecutorService']

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
CbipCrowd {
    application.url = 'https://crowd.somewhere.com/crowd/'
    application.username = 'bard'
    application.password = 'ChangeMe'
    applicationSpecificRoles = ['ROLE_USER', 'ROLE_NO_ROLE']
}
grails {
    plugins {
        springsecurity {
            roleHierarchy = '''ROLE_USER > ROLE_NO_ROLE'''
            providerNames = ['inMemMapAuthenticationProviderService', 'crowdAuthenticationProvider']
            useBasicAuth = true
            basic.realmName = 'WEBQUERY'
            filterChain.chainMap = [
                    '/bardWebInterface/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
            ]

        }
    }
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