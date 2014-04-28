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

import au.com.bytecode.opencsv.CSVReader
import bard.db.experiment.BulkSubstanceService
import bard.db.experiment.Experiment
import bard.db.experiment.ResultsService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

String experimentFile = System.getProperty("expFile")
if (experimentFile == null) {
    throw new RuntimeException("Need to run with -DexpFile=FILENAME")
}

SpringSecurityUtils.reauthenticate('integrationTestUser', null)

BulkSubstanceService bulkSubstanceService = ctx.bulkSubstanceService
assert bulkSubstanceService != null

CSVReader reader = new CSVReader(new FileReader(experimentFile))
boolean foundStart = false;
List<Long> sids = []
while(true) {
    String[] line = reader.readNext()
    if (line == null) break
    if (foundStart) {
        sids.add(Long.parseLong(line[0]))
    }
    else if (line[1] == "Substance")
        foundStart = true;
}
assert foundStart

try {
    def missing = bulkSubstanceService.findMissingSubstances(sids)
    bulkSubstanceService.insertSubstances(missing, "bulk-substance-load")
} catch (Exception ex) {
    ex.printStackTrace()
}



