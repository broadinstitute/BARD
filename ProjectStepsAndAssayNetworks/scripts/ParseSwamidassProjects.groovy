import groovy.io.FileType
import bard.db.registration.Assay
import registration.AssayService
import swamidass.clustering.Cluster
import swamidass.clustering.Bundle
import Log
import org.apache.log4j.Level

Log.logger.setLevel(Level.INFO)
AssayService assayService = new AssayService()

String path = 'C:/BARD_DATA_MIGRATION/SwamidassProjectFlow/projects'
def rootFolder = new File(path)

rootFolder.eachFileRecurse(FileType.DIRECTORIES) {File projectDirectory ->
    String clusterName = projectDirectory.name
    Cluster cluster = new Cluster(name: clusterName)
    File bundlesFile = new File(projectDirectory, 'bundles')
    assert bundlesFile.exists(), "${bundlesFile.canonicalPath} doesn't exist"
    File workflowFlie = new File(projectDirectory, 'workflow.csv')
    assert workflowFlie.exists(), "${workflowFlie.canonicalPath} doesn't exist"

    //Parse and build all the bundle-nodes (BIDs)
    bundlesFile.withReader {Reader reader ->
        reader.readLine() //the header line
        reader.eachLine {String line ->
            String[] values = line.trim().split('\t')
            assert values.size() == 2, "we must have a value in each one of the BID/BUNDLE columns of the bundle file"
            Integer BID = new Integer(values[0].trim())
            List<Long> AIDs = values[1].split(',').collect { new Long(it.trim() - 'AID')} //e.g., AID1,AID5,AID7
            assert AIDs.size() >= 1, "We expect at least one AID in each row of the BUNDLE column: ${values[1]}"

            //Get all assays by aids
            List<Assay> assays = AIDs.collect {Long aid ->
                List<Assay> foundAssays = assayService.findByPubChemAid(aid)
                assert foundAssays.size() <= 1, "We expect one assay the most when searching by aid: ${foundAssays.dump()}"
                if (!foundAssays) {
                    Log.logger.error("We couldn't find a matching assay for AID=${aid}")
                }
                return foundAssays.first() //and only one
            }
            assert assays, "We expect to have at least one valid assay in each bundle"
            Bundle bundle = new Bundle(bid: BID, assays: assays, cluster: cluster)
            cluster.addToBundles(bundle)
        }
    }

    //Parse and build all the bundleRelarions
}