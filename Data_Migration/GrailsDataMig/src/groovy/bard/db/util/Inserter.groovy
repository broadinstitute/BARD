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

    PCAssay getPCAssay(long assayId) {
        InputStream is = PubChemFactory.getInstance().getXmlDescr(assayId);
        List<PCAssay> assays = PubChemXMLParserFactory.getInstance().populateAssayFromXML(is, false);
        PCAssay pcAssay = assays.get(0);
        return pcAssay;
    }

    void runInsert(long assayId ){

       testSaveAssay()



//        String.format("%s.%s", pcAssay.getVersion(), pcAssay.getRevision())
        ExternalSystem system =  ExternalSystem.findBySystemNameLike("PubChem")

        ExternalAssay extAssay = ExternalAssay.findByExternalSystemAndExtAssayId(system,"aid=" + assayId)
        if( null != extAssay )
            extAssay.getAssay().delete(flush:  true)
        else {
            PCAssay pcAssay = getPCAssay(assayId)

            Assay bardAssay = new Assay()
            // id, status, name, assay_version, version, date_created, ready_for_extraction
            bardAssay.setAssayName(pcAssay.getName().substring(0,127))
            bardAssay.setAssayVersion(pcAssay.version as String)
            bardAssay.setVersion(1)
            bardAssay.setDateCreated(pcAssay.getDepositDate() )
            bardAssay.setDescription(pcAssay.getDescription().substring(0,999))
            bardAssay.setAssayStatus("Active")
            bardAssay.setReadyForExtraction('Ready')
            bardAssay.save()

            extAssay = new ExternalAssay()
            extAssay.setExternalSystem(system)
            extAssay.setAssay(bardAssay)
            extAssay.setDateCreated(pcAssay.getDepositDate())
            extAssay.setExtAssayId("aid=" + pcAssay.getAID())
            extAssay.setVersion(pcAssay.getVersion())
            extAssay.save()
        }
        println "finished saving AID" + assayId
    }

}
