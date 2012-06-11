package bard.db.util

import bard.db.experiment.Experiment
import bard.db.registration.Assay
import bard.db.registration.ExternalAssay
import bard.db.registration.ExternalSystem
import edu.scripps.fl.pubchem.PubChemFactory
import edu.scripps.fl.pubchem.PubChemXMLParserFactory
import edu.scripps.fl.pubchem.db.PCAssay

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
        bardAssay.setAssayName(pcAssay.getName().substring(0,127))
        bardAssay.setAssayVersion(pcAssay.version as String)
        bardAssay.setVersion(1)
        bardAssay.setDateCreated(pcAssay.getDepositDate() )
        bardAssay.setDescription(pcAssay.getDescription().substring(0,999))
        bardAssay.setAssayStatus("Active")
        bardAssay.setReadyForExtraction('Ready')

 //       ExternalSystem system =  ExternalSystem.findBySystemNameLike("PubChem")

        try {
            if ( !bardAssay.save( /* flush: true */ ) ) {
                bardAssay.errors.each {
                    println "Problem saving record = ${it}"
                }

            }
            print "Assay Record successfully inserted"
        }
        catch(Exception ex) {
            ex.printStackTrace()
        }



        ExternalSystem  externalSystem  =  ExternalSystem.findById(1l)
        ExternalAssay extAssay = new ExternalAssay()
        extAssay.setExternalSystem(externalSystem)
        extAssay.setAssay(bardAssay)
        extAssay.setDateCreated(pcAssay.getDepositDate())
        extAssay.setExtAssayId("aid=" + pcAssay.getAID())
        extAssay.setVersion(pcAssay.getVersion())
        print "ExternalAssay Record successfully inserted"



        if ( !extAssay.save( flush: true ) ) {
            extAssay.errors.each {
                println "Problem saving record = ${it}"
            }
        }


        println "finished saving AID" + assayId
    }

}
