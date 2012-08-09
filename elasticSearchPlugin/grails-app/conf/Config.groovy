import org.apache.log4j.DailyRollingFileAppender
import grails.util.Environment

// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
        appender new DailyRollingFileAppender(
                name: "NCGCResponseTimeAppender",
                file: "logs/" + Environment.current.name + "/NCGC_ResponseTime.log",
                layout: pattern(conversionPattern: '%m%n'),
                immediateFlush: true,
                threshold: org.apache.log4j.Level.INFO,
                datePattern: "'.'yyyy-MM-dd"
        )
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

    warn 'org.mortbay.log'
}

elasticSearchService.restNode.baseUrl = "http://bard-dev-vm:9200"
elasticSearchService.restNode.elasticAssayIndex = "/assays"
elasticSearchService.restNode.elasticCompoundIndex = "/compound"
elasticSearchService.restNode.elasticSearchRequester = "/_search"