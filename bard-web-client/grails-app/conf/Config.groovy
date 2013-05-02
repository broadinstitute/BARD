import grails.util.Environment
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.net.SMTPAppender
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler

ncgc.thickclient.compounds.url="http://bard.nih.gov/bard/compounds/"
ncgc.thickclient.etags.url="http://bard.nih.gov/bard/etag/"

ncgc.server.root.url = "http://bard.nih.gov/api/v16"
promiscuity.badapple.url = "${ncgc.server.root.url}/plugins/badapple/prom/cid/"
//override in config file for environment
server.port = System.properties.getProperty('server.port') ?: 8080
grails.serverURL = "http://localhost:${server.port}/bardwebclient"
//URL to the ROOT of the cap server
bard.cap.home = "http://localhost:8081/BARD/"
bard.cap.assay = "${bard.cap.home}assayDefinition/show/"
bard.cap.project="${bard.cap.home}project/show/"

//Override in config file
dataexport.apikey= "test"
dataexport.dictionary.accept.type= "application/vnd.bard.cap+xml;type=dictionary"
dataexport.dictionary.url = "https://bard-qa.broadinstitute.org/dataExport/api/dictionary"


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.cache.clearAtStartup=true
grails.cache.config = {
    cache {
        name 'dictionaryElements'
        eternal false
        overflowToDisk true
        maxElementsInMemory 10000
        maxElementsOnDisk 10000000
    }
}
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
CbipCrowd {
    application.url = 'http://localhost:8095/crowd/'
    application.username = 'webquery'
    application.password = '2345'
    applicationSpecificRoles = ['ROLE_USER','ROLE_Bard', 'ROLE_NO_ROLE', 'ROLE_MOBILE']
}
webquery

rememberme.key = 'bard_web_client_crowd_remember_me'
rememberme.cookieName = 'bard_web_client_crowd_remember_me_cookie'

grails.plugins.springsecurity.providerNames = ['crowdAuthenticationProvider', 'inMemMapAuthenticationProviderService', 'anonymousAuthenticationProvider', 'rememberMeAuthenticationProvider']
grails.plugins.springsecurity.rememberMe.cookieName = rememberme.cookieName
grails.plugins.springsecurity.rememberMe.key = rememberme.key
grails {
    plugins {
        springsecurity {
            roleHierarchy = '''ROLE_USER > ROLE_Bard > ROLE_NO_ROLE'''
//            providerNames = ['crowdAuthenticationProvider', 'inMemMapAuthenticationProviderService', 'anonymousAuthenticationProvider', 'rememberMeAuthenticationProvider']
            useBasicAuth = true
            basic.realmName = 'WEBQUERY'
            filterChain.chainMap = [
                    '/bardWebInterface/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/chemAxon/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/dictionaryTerms/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/doseResponseCurve/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
            ]

            /** authenticationEntryPoint */
            auth.loginFormUrl = '/bardLogin/auth'
            auth.forceHttps = 'false'
            auth.ajaxLoginFormUrl = '/bardLogin/authAjax'
            auth.useForward = false

            /** logoutFilter */
            logout.afterLogoutUrl = '/bardLogout/afterLogout' // '/'
            logout.filterProcessesUrl = '/j_spring_security_logout'
            logout.handlerNames = [] // 'rememberMeServices', 'securityContextLogoutHandler'

            // failureHandler
            failureHandler.defaultFailureUrl = '/bardLogin/authfail?login_error=1'
            failureHandler.ajaxAuthFailUrl = '/bardLogin/authfail?ajax=true'
            failureHandler.exceptionMappings = [:]
            failureHandler.useForward = false

            // successHandler
            successHandler.defaultTargetUrl = '/'
            successHandler.alwaysUseDefault = false
            successHandler.targetUrlParameter = AbstractAuthenticationTargetUrlRequestHandler.DEFAULT_TARGET_PARAMETER // 'spring-security-redirect'
            successHandler.useReferer = false
            successHandler.ajaxSuccessUrl = '/bardLogin/ajaxSuccess'

            /**
             * accessDeniedHandler
             * set errorPage to null to send Error 403 instead of showing error page
             */
            adh.errorPage = '/bardLogin/denied'
            adh.ajaxErrorPage = '/bardLogin/ajaxDenied'

        }
    }
}

//TODO: Override In Production. Also add greenmail.disabled=true so Green mail is disabled
grails.mail.port = com.icegreen.greenmail.util.ServerSetupTest.SMTP.port
grails.mail.default.from = "noreply@broadinstitute.org"
grails.mail.default.to = "noreply@broadinstitute.org"
grails.mail.host = "localhost"
grails.mail.default.subject = "Error From BARD Web Query"

//TODO Replace with the analytics ID
google.analytics.webPropertyID = "UA-xxxxxx-x"

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

log4j = {
    appenders {
        // This should work on both windows and unix
        appender new DailyRollingFileAppender(
                name: "NCGCErrorAppender",
                file: "logs/" + Environment.current.name + "/NCGC_Errors.log",
                layout: pattern(conversionPattern: '%m%n'),
                immediateFlush: true,
                threshold: org.apache.log4j.Level.ERROR,
                datePattern: "'.'yyyy-MM-dd"
        )
        appender new DailyRollingFileAppender(
                name: "JavaScriptErrorsAppender",
                file: "logs/" + Environment.current.name + "/Client_JavaScript_Errors.log",
                layout: pattern(conversionPattern: '%m%n'),
                immediateFlush: true,
                threshold: org.apache.log4j.Level.ERROR,
                datePattern: "'.'yyyy-MM-dd"
        )
        appenders {
            // This should work on both windows and unix
            appender new DailyRollingFileAppender(
                    name: "NCGCRestApiTimingAppender",
                    file: "logs/" + Environment.current.name + "/NCGC_StopWatch.log",
                    layout: pattern(conversionPattern: '%m%n'),
                    immediateFlush: true,
                    threshold: org.apache.log4j.Level.INFO,
                    datePattern: "'.'yyyy-MM-dd"
            )
        }

        def patternLayout = new org.apache.log4j.PatternLayout()
        patternLayout.setConversionPattern("%d [%t] %-5p %c - %m%n")
        appender new SMTPAppender(
                name: "mail",
                smtpPort: config.grails.mail.port,
                from: config.grails.mail.default.from,
                to: config.grails.mail.default.to,
                subject: "[${InetAddress.getLocalHost().getHostName()}] " + config.grails.mail.default.subject,
                smtpHost: config.grails.mail.host,
                layout: patternLayout,
                threshold: org.apache.log4j.Level.ERROR
        )
    }

    root {
        debug 'stdout'
        error 'mail', 'fileAppender'
        additivity true
    }


    error additivity: false,
    //Capture errors from the NCGC API (via JDO) but DO NOT send emails about them.
    NCGCErrorAppender: ['grails.app.services.bard.core.rest.spring.AbstractRestService']
    error additivity: true,
    //Capture JavaScript errors from the client (via the ErrorHandling controller)
    JavaScriptErrorsAppender: ['grails.app.controllers.bardqueryapi.ErrorHandlingController']

    //Capture NCGC REST API roundtrip timing.
    info NCGCRestApiTimingAppender: ['grails.app.services.bard.core.helper.LoggerService']
}


grails.plugins.twitterbootstrap.fixtaglib = true
grails.spring.disable.aspectj.autoweaving = true