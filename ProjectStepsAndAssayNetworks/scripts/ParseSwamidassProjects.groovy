import groovy.io.FileType
import bard.db.registration.Assay
import registration.AssayService
import swamidass.clustering.Cluster
import swamidass.clustering.Bundle
import swamidass.clustering.Log
import org.apache.log4j.Level
import swamidass.clustering.ParseSwamidassProjectsService

Log.logger.setLevel(Level.INFO)
ParseSwamidassProjectsService parseSwamidassProjectsService = ctx.getBean("parseSwamidassProjectsService")
assert parseSwamidassProjectsService

String path = 'C:/BARD_DATA_MIGRATION/projects'
def rootFolder = new File(path)
Integer count=1

rootFolder.eachFileRecurse(FileType.DIRECTORIES) {File projectDirectory ->
    Log.logger.info("processing project-folder: ${projectDirectory} (${count++})")
    String clusterName = projectDirectory.name
    Cluster cluster = new Cluster(name: clusterName)

    //Parse nad build all the bundles
    parseSwamidassProjectsService.buildBundles(projectDirectory, cluster)

    //Parse and build all the bundleRelarions
    parseSwamidassProjectsService.buildBundleRelations(projectDirectory, cluster)

//    cluster.validate()
    cluster.save(failOnError: true, flush: true)
}