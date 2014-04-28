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


import bard.validation.ext.ExternalItem
import bard.db.registration.AssayContextItem
import bard.db.dictionary.Element
import bard.db.model.AbstractContextItem
import bard.db.project.ProjectContextItem
import bard.db.experiment.ExperimentContextItem
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import bard.db.audit.BardContextUtils
import bard.db.dictionary.OntologyDataAccessService

FileWriter logWriter = new FileWriter("populateExValueDisplay.out")

logWriter.write("ErrorType\tContextType\tContextItemId\tAttributeElementId\tURL\tExtValue\tModifiedBy\tErrorMessage\n")
logWriter.flush()
log = {msg ->
    //println(msg)
    logWriter.write(msg+"\n")
    logWriter.flush()
}
SpringSecurityUtils.reauthenticate('fixValueDisplay', null)

String modifiedBy = "fixValueDisplay"

OntologyDataAccessService externalOntologyAccessService = ctx.ontologyDataAccessService
List<AssayContextItem> acItems = AssayContextItem.executeQuery("""select aci from AssayContextItem as aci
join aci.assayContext as ac
join ac.assay as a
where aci.extValueId is not null and aci.modifiedBy <> '${modifiedBy}' and aci.modifiedBy <> 'BAO_1000'
""")

int updatedAssayContextItem = 0
for (AssayContextItem item : acItems) {
    if (processItem(item, externalOntologyAccessService, "Assay", modifiedBy)){
        BardContextUtils.setBardContextUsername(ctx.sessionFactory.currentSession, modifiedBy)
        item.save()
        updatedAssayContextItem++
    }
}
println("Finish process assay context item, total ${acItems.size()}, updated ${updatedAssayContextItem}")

List<ExperimentContextItem> ecItems = ExperimentContextItem.executeQuery("""select eci from ExperimentContextItem as eci
join eci.experimentContext as ec
join ec.experiment as e
where eci.extValueId is not null and eci.modifiedBy <> '${modifiedBy}' and eci.modifiedBy <> 'BAO_1000'
""")
int updatedExperimentContextItem = 0
for (ExperimentContextItem item : ecItems) {
    if (processItem(item, externalOntologyAccessService, "Experiment", modifiedBy)){
        BardContextUtils.setBardContextUsername(ctx.sessionFactory.currentSession, modifiedBy)
//       println("username: " + BardContextUtils.getCurrentUsername(ctx.sessionFactory.currentSession))
        item.save()
        updatedExperimentContextItem++
    }
}
println("Finish process experiment context item, total ${ecItems.size()}, updated ${updatedExperimentContextItem}")

List<ProjectContextItem> pcItems =  ProjectContextItem.executeQuery("""select pci from ProjectContextItem as pci
join pci.context as pc
where pci.extValueId is not null and pci.modifiedBy <> '${modifiedBy}' and pci.modifiedBy <> 'BAO_1000'
""")
int updatedProjectContextItem = 0
for (ProjectContextItem item : pcItems) {
    if (processItem(item, externalOntologyAccessService, "Project", modifiedBy)){
        BardContextUtils.setBardContextUsername(ctx.sessionFactory.currentSession, modifiedBy)
 //       println("username: " + BardContextUtils.getCurrentUsername(ctx.sessionFactory.currentSession))
        item.save()
        updatedProjectContextItem++
    }
}
println("Finish process project context item, total ${pcItems.size()}, updated ${updatedProjectContextItem}")


boolean processItem(AbstractContextItem item, OntologyDataAccessService externalOntologyAccessService, String itemType, String modifiedBy) {
    Element element = item.attributeElement
    if (!element) {
        log("Missing attributeElement\t${itemType}ContextItem\t${item.id}\t\t\t${item.extValueId}\t${item.modifiedBy}\t\n")
        return false
    }
    if (!element.externalURL) {
        log("Missing externalURL\t${itemType}ContextItem\t${item.id}\t${element.id}\t\t${item.extValueId}\t${item.modifiedBy}\t\n")
        return false
    }

    try{
        ExternalItem externalItem = externalOntologyAccessService.findExternalItemById(element.externalURL, StringUtils.trim(item.extValueId))
        if (!externalItem) {
            log("Not found\t${itemType}ContextItem\t${item.id}\t${element.id}\t${element.externalURL}\t${item.extValueId}\t${item.modifiedBy}\t\n")
            return false
        }

        item.extValueId = externalItem.id
        item.valueDisplay = externalItem.display
        item.modifiedBy = modifiedBy
        return true
    }catch(Exception e) {
        log("Exception\t${itemType}ContextItem\t${item.id}\t${element.id}\t${element.externalURL}\t${item.extValueId}\t${item.modifiedBy}\t${e.message}\n")
        return false
    }
}


