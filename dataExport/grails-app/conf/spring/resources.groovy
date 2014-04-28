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

import dataexport.registration.MediaTypesDTO

// Place your Spring DSL code here
beans = {
    mediaTypesDTO(MediaTypesDTO){
        elementMediaType = '${bard.data.export.dictionary.element.xml}'
        stageMediaType = '${bard.data.export.dictionary.stage.xml}'
        resultTypeMediaType = '${bard.data.export.dictionary.resultType.xml}'
        assayMediaType = '${bard.data.export.assay.xml}'
        assayDocMediaType = '${bard.data.export.assay.doc.xml}'
        projectDocMediaType = '${bard.data.export.project.doc.xml}'
        dictionaryMediaType = '${bard.data.export.dictionary.xml}'
        assaysMediaType = '${bard.data.export.assays.xml}'
        projectMediaType = '${bard.data.export.project.xml}'
        projectsMediaType = '${bard.data.export.projects.xml}'
        experimentsMediaType = '${bard.data.export.experiments.xml}'
        experimentMediaType = '${bard.data.export.experiment.xml}'
        resultsMediaType = '${bard.data.export.results.json}'
        resultMediaType = '${bard.data.export.results.json}'
        externalReferenceMediaType = '${bard.data.export.externalreference.xml}'
        externalReferencesMediaType = '${bard.data.export.externalreferences.xml}'
        externalSystemMediaType = '${bard.data.export.externalsystem.xml}'
        externalSystemsMediaType = '${bard.data.export.externalsystems.xml}'
    }
    experimentExportService(dataexport.experiment.ExperimentExportService) {
        numberRecordsPerPage = '${bard.experiments.max.per.page}'
        mediaTypeDTO = ref('mediaTypesDTO')
        grailsLinkGenerator = ref('grailsLinkGenerator')
        utilityService = ref('utilityService')
    }
}
