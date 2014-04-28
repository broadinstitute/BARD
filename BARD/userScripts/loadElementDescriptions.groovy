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
