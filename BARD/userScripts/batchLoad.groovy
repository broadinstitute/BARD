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

import bard.db.experiment.Experiment
import bard.db.experiment.ResultsService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import bard.db.experiment.PubchemReformatService
import bard.db.registration.ExternalReference

SpringSecurityUtils.reauthenticate('integrationTestUser', null)
PubchemReformatService pubchemReformatService = ctx.pubchemReformatService
assert pubchemReformatService != null
ResultsService resultsService = ctx.resultsService

def aids = new File("/Users/pmontgom/data/pubchem-conversion/CARS-603-aid.txt").readLines()

aids.remove("488896")

FileWriter logWriter = new FileWriter("/Users/pmontgom/data/pubchem-conversion/CARS-603-log.txt", true)

log = {msg -> 
   println(msg)
   logWriter.write((new Date().toString())+" "+msg+"\n")
   logWriter.flush()
}

try {
for (aid in aids) {
    ExternalReference ref = ExternalReference.findByExtAssayRef("aid=${aid}")

/*
    if(ref.experiment.experimentFiles.size() > 0) {
        log("Skipping ${aid} -> ${ref.experiment.id} because results exist")
        continue
    }
*/   
    log("Creating measures for ${aid} -> ${ref.experiment.id}")
    
    // set up experiment measures
    def resultMapEntries
    ExternalReference.withSession { session -> 
        session.createSQLQuery("""BEGIN
          delete from result_map;
          insert into result_map select * from southern.result_map@barddev;
          result_map_util.transfer_result_map('${aid}');
        END;
        """).executeUpdate()
        
        resultMapEntries = session.createSQLQuery("select count(*) from result_map where aid = '${aid}'").setCacheable(false).uniqueResult()
    }
    
    if(resultMapEntries == 0) {
        log("Skipping ${aid} -> ${ref.experiment.id} because we're missing resultmapping")
    }
    
    assert ref != null
    log("converting ${aid} -> exp-${ref.experiment.id}")
    def pubchemFile = "/Users/pmontgom/data/pubchem-conversion/CARS-603/${aid}.csv"
    def capFile = "/Users/pmontgom/data/pubchem-conversion/CARS-603-converted/exp-${aid}-${ref.experiment.id}.csv"
    pubchemReformatService.convert(ref.experiment.id, pubchemFile, capFile)

    ResultsService.ImportSummary results = resultsService.importResults(ref.experiment.id, new FileInputStream(capFile))
    log("errors: ${results.errors.size()}")
    for(e in results.errors) {
        log("\t${e}")
    }
    log("imported: ${results.resultsCreated}")
}
} catch (Exception ex) {
    ex.printStackTrace()
}
