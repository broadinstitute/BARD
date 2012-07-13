
class ElasticSearchGrailsPlugin {

    String groupId = 'org.grails.plugins'

    // the plugin version
    def version = "0.2-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]

    // since we want to override the migrationResourceAccessor configured by the database-migraiton plugin
    // asked to be loaded after that plugin
    def loadAfter = []
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "web-app",
            "grails-app/conf/BardCodeNarcRuleSet.groovy"
    ]

    // TODO Fill in these fields
    def title = "Elastic Search Plugin" // Headline display name of the plugin
    def author = "Broad Chemical Biology Platform"
    def authorEmail = "bard-tech@broadinstitute.org"
    def description = '''Provides an ElasticSearch integration and querying services.'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/broadinstitute/BARD/wiki/elasticSearchPlugin"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [name: "Broad Institute", url: "http://www.broadinstitute.org/"]

    // Any additional developers beyond the author specified above.
    def developers = [[name: "bard-tech"]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [url: "https://github.com/broadinstitute/BARD/tree/master/elasticSearchPlugin"]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        /**
         *  overriding the configuration of the migrationResourceAccessor so it can file the
         *  changelog in this plugin instead of the parent application.
         *
         *  based on the initialization that happens in the DatabaseMigrationGrailsPlugin.groovy
         */
        elasticSearchService(elasticsearchplugin.ElasticSearchService) {
            elasticSearchBaseUrl = "http://bard-dev-vm:9200"
            assayIndexName = 'assays'
            assayIndexTypeName = 'assay'
            compoundIndexName = 'compounds'
            compoundIndexTypeName = 'compound'
            queryExecutorService = ref('queryExecutorService')
        }
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
