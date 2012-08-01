import grails.util.Environment

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

//Number of experiments per page
bard.experiments.max.per.page = 1000000

//number of results per page
bard.results.max.per.page = 5000
bard.pubchem.sid.url.prefix = 'http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid='
//root
bard.data.export.bardexport.xml = 'application/vnd.bard.cap+xml;type=bardexport'

//experiments
bard.data.export.experiments.xml = 'application/vnd.bard.cap+xml;type=experiments'
bard.data.export.experiment.xml = 'application/vnd.bard.cap+xml;type=experiment'
bard.data.export.results.xml = 'application/vnd.bard.cap+xml;type=results'
bard.data.export.result.xml = 'application/vnd.bard.cap+xml;type=result'
bard.data.export.projects.xml = 'application/vnd.bard.cap+xml;type=projects'
bard.data.export.project.xml = 'application/vnd.bard.cap+xml;type=project'

//assays
bard.data.export.assays.xml = 'application/vnd.bard.cap+xml;type=assays'
bard.data.export.assay.xml = 'application/vnd.bard.cap+xml;type=assay'
bard.data.export.assay.doc.xml = 'application/vnd.bard.cap+xml;type=assayDoc'

//dictionary
bard.data.export.dictionary.resultType.xml = 'application/vnd.bard.cap+xml;type=resultType'
bard.data.export.dictionary.xml = 'application/vnd.bard.cap+xml;type=dictionary'
bard.data.export.dictionary.stage.xml = 'application/vnd.bard.cap+xml;type=stage'
bard.data.export.dictionary.element.xml = 'application/vnd.bard.cap+xml;type=element'

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml',
                "${bard.data.export.bardexport.xml}",
                "${bard.data.export.experiments.xml}",
                "${bard.data.export.experiment.xml}",
                "${bard.data.export.results.xml}",
                "${bard.data.export.result.xml}",
                 "${bard.data.export.projects.xml}",
                "${bard.data.export.project.xml}",
                "${bard.data.export.assays.xml}",
                "${bard.data.export.assay.xml}",
                "${bard.data.export.dictionary.xml}",
                "${bard.data.export.dictionary.resultType.xml}",
                "${bard.data.export.dictionary.stage.xml}",
                "${bard.data.export.dictionary.element.xml}"

        ],
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

            grails.serverURL = "http://www.changeme.com"
            break
        default:
            //TODO parametize port number to make more flexible.
            // Also refactor when we start externalizing config files
            "http://localhost:8080/dataExport"
            break
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '[%d{ABSOLUTE} %p %c]  %m%n')
        //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
        rollingFile name: "restApiFileAppender",
                file: "logs/" + Environment.current.name + "/dataExport_rest_api.log",
                layout: pattern(conversionPattern: '[%d{dd-MMM-yyyy HH:mm:ss,SSS}] %m%n')
    }

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

    info restApiFileAppender: "grails.app.services.dataexport.util.AuthenticationService"
}

dataexport.externalapplication.apiKey.header = 'APIKEY'
dataexport.externalapplication.apiKey.hashed = 'changeMe'

/**
 * Loads external config files from the .grails subfolder in the user's home directory
 * Home directory in Windows is usually: C:\Users\<username>\.grails
 * In Unix, this is usually ~\.grails
 *
 * dataExport-commons-config.groovy is used to holed generic, non envrironment-specific configurations such as external api credentials, etc.
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
