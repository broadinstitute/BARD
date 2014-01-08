import grails.util.Environment
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.net.SMTPAppender


// If the developer specifies a directory for the external config files at the command line, use it.
// This will look like 'grails -DprimaryConfigDir=[directory name] [target]'
// Otherwise, look for these files in the user's home .grails/projectdb directory
// If there are no external config files in either location, don't override anything in this Config.groovy
String primaryOverrideDirName = System.properties.get('primaryConfigDir')
String secondaryOverrideDirName = "${userHome}/.grails/${appName}"

List<String> fileNames = ["${appName}-commons-config.groovy", "${appName}-${Environment.current.name}-config.groovy"]
fileNames.each { fileName ->
    String primaryFullName = "${primaryOverrideDirName}/${fileName}"
    String secondaryFullName = "${secondaryOverrideDirName}/${fileName}"

    if (new File(primaryFullName).exists()) {
        println "Overriding Config.groovy with $primaryFullName"
        grails.config.locations << "file:$primaryFullName"
    } else if (new File(secondaryFullName).exists()) {
        println "Overriding Config.groovy with $secondaryFullName"
        grails.config.locations << "file:$secondaryFullName"
    } else {
        println "Skipping Config.groovy overrides: $primaryFullName and $secondaryFullName not found"
    }
}


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
        all: '*/*',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        form: 'application/x-www-form-urlencoded',
        html: ['text/html', 'application/xhtml+xml'],
        js: 'text/javascript',
        json: ['application/json', 'text/json'],
        multipartForm: 'multipart/form-data',
        rss: 'application/rss+xml',
        text: 'text/plain',
        xml: ['text/xml', 'application/xml']
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

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

//TODO: Override In Production. Also add greenmail.disabled=true so Green mail is disabled
grails.mail.port = com.icegreen.greenmail.util.ServerSetupTest.SMTP.port
grails.mail.default.from = "noreply@broadinstitute.org"
grails.mail.default.to = "noreply@broadinstitute.org"
grails.mail.host = "localhost"
grails.mail.default.subject = "Error From ${Environment.current.name}"

// log4j configuration
log4j = {
    appenders {
        try {
            String baselogDir = grails.util.Environment.warDeployed ? System.getProperty('catalina.home') : 'target'
            String logDir = "$baselogDir/logs"
            String defaultPattern = '%d [%t] %-5p %c{1} - %m%n' // see http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
            console(name: "stdout", layout: pattern(defaultPattern));
            appender(new DailyRollingFileAppender(
                    name: "mySQLAppender",
                    file: "$logDir/MySQLAppender_Errors.log",
                    layout: pattern(defaultPattern),
                    immediateFlush: true,
                    datePattern: "'.'yyyy-MM-dd"))
            appender(new SMTPAppender(
                    name: "mail",
                    SMTPPort: config.grails.mail.port,
                    from: config.grails.mail.default.from,
                    to: config.grails.mail.default.to,
                    subject: config.grails.mail.default.subject,
                    SMTPHost: config.grails.mail.host,
                    layout: pattern("%d [%t] %X{request}\n%-5p %c{1} - %m%n"),
                    threshold: org.apache.log4j.Level.ERROR))
        }
        catch (Exception ex) {
            // have to write to System.out because System.err appears to _also_ get dropped on the floor
            System.out.println("!!!!!!!!! Got exception trying to set up log4j.  This causes the whole logging config to be messed up, so printing the exception before it gets lost: ${ex}");
            System.out.println("SMTPAppender ${SMTPAppender.methods.each { it.name }}")
            throw ex;
        }

        root {
            error('stdout', 'mail', 'mySQLAppender')
        }

        error 'org.codehaus.groovy.grails.web.servlet',        // controllers
                'org.codehaus.groovy.grails.web.pages',          // GSP
                'org.codehaus.groovy.grails.web.sitemesh',       // layouts
                'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
                'org.codehaus.groovy.grails.web.mapping',        // URL mapping
                'org.codehaus.groovy.grails.commons',            // core / classloading
                'org.codehaus.groovy.grails.plugins',            // plugins
                'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
                'org.springframework',
                'org.hibernate',
                'net.sf.ehcache.hibernate'
    }
}