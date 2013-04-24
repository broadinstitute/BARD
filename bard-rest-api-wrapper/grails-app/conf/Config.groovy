import grails.util.Environment

//Override in config file
//cacheable plugin
grails.cache.config = {
    cache {
        name 'target'
    }
    cache {
        name 'dictionaryElements'
        eternal false
        overflowToDisk true
        maxElementsInMemory 10000
        maxElementsOnDisk 10000000
    }
}
ncgc.server.root.url = "http://bard.nih.gov/api/v16"
promiscuity.badapple.url = "${ncgc.server.root.url}/plugins/badapple/prom/cid/"

log4j = {

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
}
/**
 * Loads external config files from the .grails subfolder in the user's home directory
 * Home directory in Windows is usually: C:\Users\<username>\.grails
 * In Unix, this is usually ~\.grails
 *
 * bardqueryapi-commons-config.groovy is used to hold generic, non environment-specific configurations such as external api credentials, etc.
 */
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
