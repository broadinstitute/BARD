
import org.apache.log4j.BasicConfigurator;
import edu.scripps.fl.pubchem.*
import edu.scripps.fl.pubchem.db.*

import bard.db.registration.Assay

class Main {
	static void main(def args) {
		InputStream is = PubChemFactory.getInstance().getXmlDescr(602305)
		List<PCAssay> assays = PubChemXMLParserFactory.getInstance().populateAssayFromXML(is, false)
		PCAssay pcAssay = assays.get(0)
		println assays.get(0).getExtRegId()
		
		Assay bardAssay = new Assay()
		bardAssay.setAssayName(pcAssay.getName())
		bardAssay.save()
	}
}

