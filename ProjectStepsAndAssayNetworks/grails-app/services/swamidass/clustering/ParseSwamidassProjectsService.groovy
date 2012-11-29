package swamidass.clustering

import bard.db.registration.Assay

class ParseSwamidassProjectsService {

    public void buildBundles(File projectDirectory, Cluster cluster) {
        File bundlesFile = new File(projectDirectory, 'bundles')
        assert bundlesFile.exists(), "${bundlesFile.canonicalPath} doesn't exist"

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
    }

    public void buildBundleRelations(File projectDirectory, Cluster cluster) {
        File workflowFlie = new File(projectDirectory, 'workflow.csv')
        assert workflowFlie.exists(), "${workflowFlie.canonicalPath} doesn't exist"

        //Parse and build all the bundle-nodes (BIDs)
        workflowFlie.withReader {Reader reader ->
            reader.readLine() //the header line
            reader.eachLine {String line ->
                String[] values = line.trim().split('\t')
                assert values.size() == 4, "we must have a value in each one of the NODE/CHILD/NODE_SIZE/OVERLAP columns of the workflow.csv file"
                Integer parentBID = new Integer(values[0].trim())
                Integer childBID = new Integer(values[1].trim())
                Integer parentTestedCompounds = new Integer(values[2].trim())
                Integer parentChildOverlappingCompounds = null
                if (values[2].trim().isNumber()) {
                    parentChildOverlappingCompounds = new Integer(values[3].trim())
                }
                else {
                    assert values[3].trim() == '?', "parentChildOverlappingCompounds must either be a number of '?' (a leaf node): ${values[3]}"
                }

                //Find bundleParent and bundleChild
                Bundle parentBundle = cluster.bundles.find {Bundle bundle ->
                    bundle.bid = parentBID
                }
                assert parentBundle, "bundleParent must exist"
                Bundle childBundle = cluster.bundles.find {Bundle bundle ->
                    bundle.bid = childBID
                }
                assert childBundle, "bundleChild must exist"
                //Build the BundleRelation
                BundleRelation bundleRelation = new BundleRelation(cluster: cluster,
                        parentBundle: parentBundle,
                        childBundle: childBundle,
                        parentTestedCompounds: parentTestedCompounds,
                        parentChildOverlappingCompounds: parentChildOverlappingCompounds)
                cluster.addToBundleRelations(bundleRelation)
            }
        }
    }

}
