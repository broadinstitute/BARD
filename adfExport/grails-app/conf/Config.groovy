import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler
import grails.util.Environment

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
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
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

grails {
    plugins {
        springsecurity {
            userLookup.userDomainClassName = 'bard.db.people.Person'
            userLookup.usernamePropertyName = 'userName'
            userLookup.enabledPropertyName = 'enabled'
            userLookup.passwordPropertyName = 'password'
            userLookup.authoritiesPropertyName = 'authorities'
            userLookup.accountExpiredPropertyName = 'accountExpired'
            userLookup.accountLockedPropertyName = 'accountLocked'
            userLookup.passwordExpiredPropertyName = 'passwordExpired'
            userLookup.authorityJoinClassName = 'bard.db.people.PersonRole'
            authority.className = 'bard.db.people.Role'
            authority.nameField = 'authority'

            controllerAnnotations.staticRules = [
                    '/console/**': ['ROLE_CONSOLE_USER'],
                    '/jesqueOverview/**': ['ROLE_BARD_ADMINISTRATOR'],
                    '/jesqueQueues/**': ['ROLE_BARD_ADMINISTRATOR'],
                    '/jesqueFailed/**': ['ROLE_BARD_ADMINISTRATOR'],
                    '/jesqueStats/**': ['ROLE_BARD_ADMINISTRATOR'],
                    '/jesqueWorking/**': ['ROLE_BARD_ADMINISTRATOR'],
                    '/jesqueWorkers/**': ['ROLE_BARD_ADMINISTRATOR'],
                    '/jesqueScheduled/**': ['ROLE_BARD_ADMINISTRATOR'],
            ]
            ipRestrictions = [
                    '/console/**': '127.0.0.1'
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

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
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


if (appName) {
    grails.config.locations = []

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
}
