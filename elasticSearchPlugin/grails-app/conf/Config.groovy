// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
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

    warn   'org.mortbay.log'
}

elasticSearchService.defaultElementsPerPage = 500

// Elastic root
elasticSearchService.restNode.baseUrl = 'http://bard-dev-vm:9200'
// indexes
elasticSearchService.restNode.elasticAssayIndex =  '/assays'
elasticSearchService.restNode.elasticCompoundIndex =  '/compounds'
elasticSearchService.restNode.elasticXCompoundIndex =  '/compound'
// types
elasticSearchService.restNode.elasticCompoundType =  'compound'
elasticSearchService.restNode.elasticXCompoundType =  'xcompound'
elasticSearchService.restNode.elasticAssayType =  'assay'
// commands
elasticSearchService.restNode.elasticSearchRequester  =  '/_search'

