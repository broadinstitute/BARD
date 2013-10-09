import grails.util.Environment

// configuration for plugin testing - will not be included in the plugin zip

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

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%c{2} %m%n')
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

    warn 'org.mortbay.log'

    // logs hibernate sql and params, slow but can be very helpful
//    debug  'org.hibernate.SQL'
//    trace 'org.hibernate.type.descriptor.sql.BasicBinder'


}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"

grails {
    plugins {
        springsecurity {
            providerNames = ['inMemMapAuthenticationProviderService', 'crowdAuthenticationProvider']
            ipRestrictions = [
                    '/console/**': '127.0.0.1'
            ]
        }
    }
}

CbipCrowd {
    application.url = 'https://crowd.somewhere.com/crowd/'
    register.url = 'https://crowd.somewhere.com/crowd/'
    application.username = 'bard'
    application.password = 'ChangeMe'
    applicationSpecificRoles = ['ROLE_Bard', 'ROLE_MOBILE', 'ROLE_USER', 'ROLE_CONSOLE_USER', 'ROLE_NO_ROLE', 'ROLE_CURATOR', "ROLE_BARD_ADMINISTRATOR", "ROLE_TEAM_BROAD"]
    mockUsers {
        integrationTestUser {
            roles = ['ROLE_USER', 'ROLE_CURATOR', 'ROLE_BARD_ADMINISTRATOR']
            username = 'integrationTestUser'
            password = 'integrationTestUser'
            email = 'integrationTestUser@nowhere.com'
        }
        curator {
            roles = ['ROLE_CURATOR']
            owningRole = 'ROLE_CURATOR'
            username = 'curator'
            password = 'curator'
            email = 'curator@nowhere.com'
        }
        teamA_1 {
            roles = ['ROLE_TEAM_A']
            owningRole = 'ROLE_TEAM_A'
            username = 'teamA_1'
            password = 'teamA_1'
            email = 'team1@nowhere.com'
        }
        teamA_2 {
            roles = ['ROLE_TEAM_A']
            owningRole = 'ROLE_TEAM_A'
            username = 'teamA_2'
            password = 'teamA_2'
            email = 'teamA2@nowhere.com'
        }
        teamB_1 {
            roles = ['ROLE_TEAM_B']
            owningRole = 'ROLE_TEAM_B'
            username = 'teamB_1'
            password = 'teamB_2'
            email = 'team2@nowhere.com'
        }
    }
}