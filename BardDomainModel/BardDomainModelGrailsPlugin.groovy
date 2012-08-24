import grails.plugin.databasemigration.MigrationUtils
import liquibase.resource.FileSystemResourceAccessor
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils

class BardDomainModelGrailsPlugin {

    String groupId = 'org.grails.plugins'

    // the plugin version
    def version = "0.1.9-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]

    // since we want to override the migrationResourceAccessor configured by the database-migraiton plugin
    // asked to be loaded after that plugin
    def loadAfter = ['datbase-migration']
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp",
        "src/groovy/test/"
    ]

    // TODO Fill in these fields
    def title = "Bard Domain Model Plugin" // Headline display name of the plugin
    def author = "Broad Chemical Biology Platform"
    def authorEmail = "cbntsoftware@broadinstitute.org"
    def description = '''\
Provide domain objects for any objects wishing to directly access the Bard database.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/broadinstitute/BARD/wiki/Bard-domain-objects-for-grails"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Broad Institute", url: "http://www.broadinstitute.org/" ]

    // Any additional developers beyond the author specified above.
    def developers = [ [ name: "ba" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/broadinstitute/BARD/tree/master/BardDomainModel" ]

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
        if ( !application.warDeployed ) {
            File bardDomainModelPluginDir = GrailsPluginUtils.getPluginDirForName('bard-domain-model').getFile()
            //println(bardDomainModelPluginDir.path)
            String changelogLocationPath = new File(bardDomainModelPluginDir, MigrationUtils.changelogLocation).path
            //println(changelogLocationPath)
            migrationResourceAccessor(FileSystemResourceAccessor, changelogLocationPath)
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
