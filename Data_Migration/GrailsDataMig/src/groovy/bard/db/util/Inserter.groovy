package bard.db.util

import edu.scripps.fl.pubchem.PubChemFactory
import edu.scripps.fl.pubchem.db.PCAssay
import edu.scripps.fl.pubchem.PubChemXMLParserFactory
import bard.db.registration.Assay
import bard.db.experiment.Experiment
import bard.db.registration.ExternalAssay
import bard.db.registration.ExternalSystem

class Inserter {
    Inserter(){}

    void runMLPInsert() {
        Set<Long> mlpAids = PubChemFactory.getInstance().getMolecularLibrariesOrBeforeAIDs()
        for(Long aid: mlpAids)
            runInsert(aid)
    }

/*    void runInsertTest(long assayId) {
        Assay bardAssay = new Assay()
        bardAssay.setAssayName("test")
        bardAssay.setAssayVersion("1")
        bardAssay.setVersion((pcAssay.version as String)
        bardAssay.setDateCreated(new Date())
        bardAssay.setDescription("description")
        bardAssay.setAssayStatus("Active")
        bardAssay.setReadyForExtraction('Ready')
        bardAssay.save()
    } */

    void runInsert(long assayId ){
        InputStream is = PubChemFactory.getInstance().getXmlDescr(assayId)
        List<PCAssay> assays = PubChemXMLParserFactory.getInstance().populateAssayFromXML(is, false)
        PCAssay pcAssay = assays.get(0)
//        String.format("%s.%s", pcAssay.getVersion(), pcAssay.getRevision())
        Experiment expt = new Experiment()
        expt.setLastUpdated(pcAssay.getModifyDate())

        // id, status, name, assay_version, version, date_created, ready_for_extraction
        Assay bardAssay = new Assay()
        bardAssay.setAssayName(pcAssay.getName())
        bardAssay.setAssayVersion(pcAssay.version as String)
        bardAssay.setVersion(1)
        bardAssay.setDateCreated(pcAssay.getDepositDate() )
        bardAssay.setDescription(pcAssay.getDescription())
        bardAssay.setAssayStatus("Active")
        bardAssay.setReadyForExtraction('Ready')

 //       ExternalSystem system =  ExternalSystem.findBySystemNameLike("PubChem")

        try {
            if ( !bardAssay.save( /* flush: true */ ) ) {
                bardAssay.errors.each {
                    println "Problem saving record = ${it}"
                }
                print 'hello'
            }
        }
        catch(Exception ex) {
            ex.printStackTrace()
        }



        ExternalAssay extAssay = new ExternalAssay()
        extAssay.setExternalSystem(system)
        extAssay.setAssay(bardAssay)
        extAssay.setDateCreated(pcAssay.getDepositDate())
        extAssay.setExtAssayId("aid=" + pcAssay.getAID())
        extAssay.setVersion(pcAssay.getVersion())



        if ( !extAssay.save( flush: true ) ) {
            extAssay.errors.each {
                println "Problem saving record = ${it}"
            }
        }


        println "finished saving AID" + assayId
    }

}
