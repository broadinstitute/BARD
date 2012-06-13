package bard.db.util

import bard.db.registration.Assay
import bard.db.registration.ExternalAssay
import bard.db.registration.ExternalSystem
import edu.scripps.fl.pubchem.PubChemFactory
import edu.scripps.fl.pubchem.PubChemXMLParserFactory
import edu.scripps.fl.pubchem.db.PCAssay

class InsertAssayTest {
    void runInsertTest() {
        Assay bardAssay = new Assay()
        bardAssay.setAssayName("blah")
        bardAssay.setAssayVersion("1")
        bardAssay.setVersion(1)
        bardAssay.setDateCreated(new Date())
        bardAssay.setDescription("description")
        bardAssay.setAssayStatus("Active")
        bardAssay.setReadyForExtraction('Ready')
        bardAssay.save(flush:  true)
    }
}