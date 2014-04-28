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

package bard.db.experiment

import bard.db.experiment.results.ImportSummary
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import bard.db.registration.Panel
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.security.access.prepost.PreAuthorize

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 7/29/13
 * Time: 9:20 AM
 * To change this template use File | Settings | File Templates.
 */
class PubchemImportService {
    GrailsApplication grailsApplication
    def pubchemReformatService
    ResultsService resultsService

    @PreAuthorize("hasRole('ROLE_BARD_ADMINISTRATOR')")
    ImportSummary recreateMeasuresAndLoad(boolean forceConvertPubchem, Long eid, Closure statusCallback) {
        statusCallback("Starting...")

        String pubchemPrefix
        String pubchemFileDir
        String convertedFileDir






        pubchemPrefix = grailsApplication.config.bard.pubchemPrefix



        pubchemFileDir = "${pubchemPrefix}/pubchem-files"
        convertedFileDir = "${pubchemPrefix}/converted-files"

        Experiment experiment = Experiment.get(eid)
        if(experiment == null) {
            throw new RuntimeException("skipping experiment ${eid} because it could not be found")
        }
        ExternalReference xref = experiment.externalReferences.find { it.extAssayRef.startsWith("aid=") }
        if(xref == null) {
            throw new RuntimeException("Could not find aid for experiment ${eid}");
        }
        int aid = Integer.parseInt(xref.extAssayRef.replace("aid=",""))

        PubchemReformatService.ResultMap map
        try {
            map = pubchemReformatService.loadMap(aid)
        } catch (Exception ex) {
            throw new RuntimeException("Exception while loading result map", ex)
        }

        List<ExternalReference> refs = ExternalReference.findAllByExtAssayRef("aid=${aid}")
        if (refs.size() == 0) {
            throw new RuntimeException("skipping ${aid} because it was not in the database at all")
        }

        Panel panel = null;
        if(experiment.panel != null)
        {
            panel = experiment.panel.panel
        }

        statusCallback("Recreating measures...")

        if (map.allRecords.size() == 0) {
            throw new RuntimeException("Skipping ${aid} -> ${refs*.experiment*.id.join(', ')} because we're missing resultmapping")
        }

        Collection<Long> eids;
        if(panel != null) {
             eids = experiment.panel.experiments.collect { it.id }
        } else {
            eids = [eid]
        }
        return reloadEids(forceConvertPubchem, eids, map, aid, pubchemFileDir, convertedFileDir, statusCallback);
    }

    protected ImportSummary reloadEids(boolean forceConvertPubchem, Collection<Long> eids, PubchemReformatService.ResultMap map, int aid,
                                       String pubchemFileDir, String convertedFileDir,
                                       Closure statusCallback) {
        for (eid in eids) {
            Experiment experiment = Experiment.get(eid)
            try {
                pubchemReformatService.recreateMeasures(experiment, map)
            } catch (Exception ex) {
                throw new RuntimeException("Exception while creating measures", ex)
            }
        }

        ImportSummary firstResults = null
        for (eid in eids) {
            def pubchemFile = "${pubchemFileDir}/${aid}.csv"
            def capFile = "${convertedFileDir}/exp-${aid}-${eid}.csv"

            if (forceConvertPubchem || !(new File(capFile).exists())) {
                statusCallback("Converting pubchem file...")
                log.warn("Converting pubchem file ${pubchemFile} -> ${capFile}")
                try {
                    pubchemReformatService.convert(eid, pubchemFile, capFile)
                } catch (Exception ex) {
                    throw new RuntimeException("Exception while converting", ex)
                }
            }

            statusCallback("Starting import...")
            // disable DB operations to speed this up for the migration process
            ResultsService.ImportOptions options = new ResultsService.ImportOptions()
            options.validateSubstances = false
            options.writeResultsToDb = false
            options.skipExperimentContexts = true
            options.statusCallback = statusCallback
            ImportSummary results = resultsService.importResults(eid, new FileInputStream(capFile), options)
            if (firstResults == null)
                firstResults = results

            log.info("errors from loading ${aid}: ${results.errors.size()}")
            for (e in results.errors) {
                log.info("\t${e}")
            }

            if (results.errors.size() > 0) {
                log.error("failed to load: ${aid}")
            } else {
                log.info("successfully loaded: ${aid}")
                log.info("imported: ${results.resultsCreated} results")
            }
        }

        return firstResults;
    }

    Panel derivePanel(Collection<Long> eids) {
        // find the unique set of assays used by these experiments.
        Set<Assay> assays = new HashSet(eids.collect { Experiment.get(it).assay })

        // the unique set of panels that those assays are in
        Set<Panel> panels = assays.collectMany { it.panelAssays.panel }

        // now, find a panel which has exactly this set of assays in it
        Set<Panel> matching = panels.findAll { panel ->
            return new HashSet(panel.panelAssays.collect { it.assay }).equals(assays)
        }

        if (matching.size() == 0) {
            throw new RuntimeException("Could not find panel which contain assays ${assays}");
        }

        if (matching.size() > 1) {
            throw new RuntimeException("Found multiple panels ${matching} which contain assays ${assays}");
        }

        return matching.first()
    }
}
