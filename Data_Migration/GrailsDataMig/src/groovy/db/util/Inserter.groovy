package db.util

import edu.scripps.fl.pubchem.PubChemFactory
import edu.scripps.fl.pubchem.db.PCAssay
import edu.scripps.fl.pubchem.PubChemXMLParserFactory
import db.registration.Assay

/**
 * Created by IntelliJ IDEA.
 * User: balexand
 * Date: 5/23/12
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
class Inserter {
    Inserter(){}
    void runInsert(long assayId ){
        InputStream is = PubChemFactory.getInstance().getXmlDescr(assayId)
        List<PCAssay> assays = PubChemXMLParserFactory.getInstance().populateAssayFromXML(is, false)
        PCAssay pcAssay = assays.get(0)
        println assays.get(0).getExtRegId()

        Assay bardAssay = new Assay()
        bardAssay.setAssayName(pcAssay.getName().substring(0,127))
        bardAssay.setAssayVersion(pcAssay.version as String )
        bardAssay.setDateCreated(pcAssay.getDepositDate() )
        if ( !bardAssay.save( flush: true ) ) {
            bardAssay.errors.each {
                println "Problem saving record = ${it}"
            }


        }
        println "finished saving record."
    }

}
