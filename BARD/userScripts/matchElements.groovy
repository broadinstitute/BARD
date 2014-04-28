/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

