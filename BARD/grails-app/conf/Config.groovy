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

//Default server url
grails.serverURL = "http://localhost:8081/"
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
grails.views.javascript.library = "jquery"
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
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

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

// DB Reverse Engineering configuration options
// uncomment these to run db-reverse-engineer plugin. Run as: grails db-reverse-engineer
grails.plugin.reveng.packageName = 'bard.db.model'
grails.plugin.reveng.jdbcDriverJarDep = 'mysql:mysql-connector-java:5.1.16'
//grails.plugin.reveng.includeTables = ['Test_Measure_Context_Item']

bard.home.page = 'http://localhost:8080/bardwebclient'
bard.services.elasticSearchService.restNode.baseUrl = 'http://bard-dev-vm:9200'
// this should get overwritten by
bard.services.resultService.archivePath = System.getProperty("java.io.tmpdir")

//grails.plugins.springsecurity.successHandler.defaultTargetUrl = '/home'
rememberme.key = 'bard_cap_crowd_remember_me'
rememberme.cookieName = 'bard_cap_crowd_remember_me_cookie'

//Use inMemMap provider only in non-production settings
switch (Environment.current) {
    case Environment.PRODUCTION:
        grails.plugins.springsecurity.providerNames = ['bardAuthorizationProviderService', 'anonymousAuthenticationProvider', 'rememberMeAuthenticationProvider']

        break
    default:
        grails.plugins.springsecurity.providerNames = ['bardAuthorizationProviderService', 'inMemMapAuthenticationProviderService', 'anonymousAuthenticationProvider', 'rememberMeAuthenticationProvider']

}

grails.plugins.springsecurity.rememberMe.cookieName = rememberme.cookieName
grails.plugins.springsecurity.rememberMe.key = rememberme.key
grails {
    plugins {
        springsecurity {
            controllerAnnotations.staticRules = [
                    '/console/**': ['ROLE_CONSOLE_USER']
            ]
            ipRestrictions = [
                    '/console/**': '127.0.0.1'
            ]
            useBasicAuth = true
            basic.realmName = 'CAP'
            filterChain.chainMap = [
//                    '/dictionaryTerms/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/element/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/document/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/assayDefinition/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/experiment/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
            ]
        }
    }
}
//prevent session fixation attacks
grails.plugins.springsecurity.useSessionFixationPrevention = true

//grails.plugins.springsecurity.rejectIfNoRule = true

CbipCrowd {
    application.url = 'https://crowd.somewhere.com/crowd/'
    register.url = 'https://crowd.somewhere.com/crowd/'
    application.username = 'bard'
    application.password = 'ChangeMe'
    applicationSpecificRoles = ['ROLE_TEAM_A', 'ROLE_TEAM_B', 'ROLE_USER', 'ROLE_CONSOLE_USER', 'ROLE_NO_ROLE', 'ROLE_CURATOR', 'CURATOR', "ROLE_BARD_ADMINISTRATOR"]
    mockUsers {
        integrationTestUser {
            roles = ['ROLE_USER', 'ROLE_CURATOR', 'ROLE_BARD_ADMINISTRATOR']
            username = 'integrationTestUser'
            password = 'integrationTestUser'
            email = 'integrationTestUser@nowhere.com'
        }
        curator {
            roles = ['ROLE_CURATOR']
            username = 'curator'
            password = 'curator'
            email = 'curator@nowhere.com'
        }
        teamA_1 {
            roles = ['ROLE_TEAM_A']
            username = 'teamA_1'
            password = 'teamA_1'
            email = 'team1@nowhere.com'
        }
        teamA_2 {
            roles = ['ROLE_TEAM_A']
            username = 'teamA_2'
            password = 'teamA_2'
            email = 'teamA2@nowhere.com'
        }
        teamB_1 {
            roles = ['ROLE_TEAM_B']
            username = 'teamB_1'
            password = 'teamB_2'
            email = 'team2@nowhere.com'
        }
    }
}

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

// Added by the JQuery Validation UI plugin:
jqueryValidationUi {
    errorClass = 'error'
    validClass = 'valid'
    onsubmit = true
    renderErrorsOnTop = false

    qTip {
        packed = true
        classes = 'ui-tooltip-red ui-tooltip-shadow ui-tooltip-rounded'
    }

    /*
       Grails constraints to JQuery Validation rules mapping for client side validation.
       Constraint not found in the ConstraintsMap will trigger remote AJAX validation.
     */
    StringConstraintsMap = [
            blank: 'required', // inverse: blank=false, required=true
            creditCard: 'creditcard',
            email: 'email',
            inList: 'inList',
            minSize: 'minlength',
            maxSize: 'maxlength',
            size: 'rangelength',
            matches: 'matches',
            notEqual: 'notEqual',
            url: 'url',
            nullable: 'required',
            unique: 'unique',
            validator: 'validator'
    ]

    // Long, Integer, Short, Float, Double, BigInteger, BigDecimal
    NumberConstraintsMap = [
            min: 'min',
            max: 'max',
            range: 'range',
            notEqual: 'notEqual',
            nullable: 'required',
            inList: 'inList',
            unique: 'unique',
            validator: 'validator'
    ]

    CollectionConstraintsMap = [
            minSize: 'minlength',
            maxSize: 'maxlength',
            size: 'rangelength',
            nullable: 'required',
            validator: 'validator'
    ]

    DateConstraintsMap = [
            min: 'minDate',
            max: 'maxDate',
            range: 'rangeDate',
            notEqual: 'notEqual',
            nullable: 'required',
            inList: 'inList',
            unique: 'unique',
            validator: 'validator'
    ]

    ObjectConstraintsMap = [
            nullable: 'required',
            validator: 'validator'
    ]

    CustomConstraintsMap = [
            phone: 'true', // International phone number validation
            phoneUS: 'true',
            alphanumeric: 'true',
            letterswithbasicpunc: 'true',
            lettersonly: 'true'
    ]
}

