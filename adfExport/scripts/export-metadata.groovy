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

import org.apache.commons.lang3.StringUtils

/**
 * use assay 8111, experiment 5631, project 3 to test
 */
String entityType =  System.getProperty("entity")
String filename = System.getProperty("outputfile")
String entityid = System.getProperty("entityid")
println "Usage: grails -Dentity=assay|experiment|project -Dentityid=entityid [-Doutputfile=filename] run-script scripts/export-metadata.groovy "
if (!entityType) {
    println("Please provide entity type: -Dentity=assay|experiment|project")
    return
}
if (!entityid && !StringUtils.isNumeric(entityid)) {
    println("Please provide a valid entity id: -Dentityid=somenumber")
    return
}

if (!filename) {
    filename = entityType + "_" + entityid + ".n3"
}

m = ctx.bardMetadataToRdfService.createModel()
if (entityType.equals('assay')) {
    ctx.bardMetadataToRdfService.addAssay(Integer.parseInt(entityid), m);
}
else if (entityType.equals('experiment')) {
    ctx.bardMetadataToRdfService.addExperiment(Integer.parseInt(entityid), m);
}
else if (entityType.equals('project')) {
    ctx.bardMetadataToRdfService.addProject(Integer.parseInt(entityid), m);
}
ctx.bardMetadataToRdfService.writeToFileInN3Format(m, filename)
