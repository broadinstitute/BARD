import bard.db.dictionary.Element

/**
 * Created by dlahr on 3/24/2014.
 */

File outputFile = new File("c:/Local/i_drive/projects/bard/RDM/ontology_paper/bard_element_ids.csv")
File elementFile = new File("c:/Local/i_drive/projects/bard/RDM/ontology_paper/BARD Vocabulary.csv")
assert elementFile.exists()

List<ElementFileEntry> elementFileEntries = readElementFile(elementFile)
println("elementFileEntries.size(): ${elementFileEntries.size()}")

ElementMapper elementMapper = new ElementMapper()
elementMapper.lookupElements()

//testElementMapper()

BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))

for (ElementFileEntry efe : elementFileEntries) {
    elementMapper.attemptMatch(efe)

    StringBuilder line = new StringBuilder()
    line.append("${efe.bardUri}\t${efe.label}\t")
    if (efe.matchingElement) {
        if (efe.matchingElement.bardURI) {
            String shortUri = efe.matchingElement.bardURI.substring(38)
            line.append(shortUri)
        }
        line.append("\t")

        line.append("${efe.matchingElement.id}\t${efe.matchingElement.elementStatus}")
    }

    writer.write(line.toString())
    writer.newLine()
}

writer.close()

return

void testElementMapper() {
    ElementMapper em = new ElementMapper()
    em.lookupElements()

    println("${em.uriElementMap.size()} ${em.labelElementMap.size()}")

    println(em.uriElementMap.keySet().iterator().next())
}

class ElementMapper {
    Map<String, Element> uriElementMap
    Map<String, Element> labelElementMap

    ElementMapper() {
        uriElementMap = new HashMap<>()
        labelElementMap = new HashMap<>()
    }

    void lookupElements() {
        List<Element> elements = Element.findAll()

        for (Element e : elements) {
            if (e.bardURI) {
                String shortUri = e.bardURI.substring(38).toLowerCase()
                if (uriElementMap.containsKey(shortUri)) {
                    println(shortUri)
                    throw new RuntimeException()
                }
                uriElementMap.put(shortUri, e)
            }

            labelElementMap.put(e.label, e)
        }
    }

    void attemptMatch(ElementFileEntry efe) {
        String uriLower = efe.bardUri.toLowerCase()
        if (uriElementMap.containsKey(uriLower)) {
            efe.matchingElement = uriElementMap.get(uriLower)
        } else if (labelElementMap.containsKey(efe.label)) {
            efe.matchingElement = labelElementMap.get(efe.label)
        }
    }
}

class ElementFileEntry {
    final String bardUri
    final String label

    Element matchingElement

    ElementFileEntry(String bardUri, String label) {
        this.bardUri = bardUri
        this.label = label
    }
}


List<ElementFileEntry> readElementFile(File elementFile) {
    List<ElementFileEntry> result = new LinkedList<>()

    BufferedReader reader = new BufferedReader(new FileReader(elementFile))
    reader.readLine()

    String line
    while ((line = reader.readLine()) != null) {
        String[] split = line.split(",")

        result.add(new ElementFileEntry(split[0], split[1]))
    }

    reader.close()

    return result
}

