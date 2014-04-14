import bard.db.audit.BardContextUtils
import bard.db.dictionary.Element
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory

/**
 * Created by dlahr on 4/13/2014.
 */


final String inputFilename = "c:/Local/i_drive/projects/bard/rdm/ontology_paper/BARD Vocabulary - last.tsv"

SpringSecurityUtils.reauthenticate("dlahr", null)
SessionFactory sf = ctx.sessionFactory
BardContextUtils.setBardContextUsername(sf.currentSession, "dlahr")

println("reading elements from database:")
AllElements allElements = new AllElements()

BufferedReader reader = new BufferedReader(new FileReader(inputFilename))
reader.readLine()//skip first header
reader.readLine()//skip second header
int lineNum = 2;

int modCount = 0;
Element.withTransaction {status ->

    String line
    while ((line = reader.readLine()) != null) {
        String[] split = line.split("\t")

        if (split.length >= 8) {
            String newDescription = split[7].trim()

            if (newDescription) {
                Element e = null

                String eIdStr = split[1].trim()
                String eLabel = split[6].trim()
                if (eIdStr) {
                    Long eId = Long.valueOf(eIdStr)
                    e = allElements.eIdMap.get(eId)
                } else if (eLabel) {
                    e = allElements.eLabelMap.get(eLabel)
                }

                if (e) {
                    e.description = newDescription
                    e.save()
                    modCount++
                } else {
                    println("Warning could not find element using id or label:  lineNum:$lineNum eIdStr:$eIdStr eLabel:$eLabel")
                }
            }
        }
        lineNum++
    }

//    status.setRollbackOnly()
}

println("modCount:$modCount")

reader.close()


return


class AllElements {
    List<Element> eList
    Map<Long, Element> eIdMap
    Map<String, Element> eLabelMap

    AllElements() {
        eList = Element.findAll()

        eIdMap = new HashMap<>()
        eLabelMap = new HashMap<>()

        for (Element e : eList) {
            eIdMap.put(e.id, e)
            eLabelMap.put(e.label, e)
        }
    }
}