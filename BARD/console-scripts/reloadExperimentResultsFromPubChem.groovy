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
import bard.db.registration.ExternalReference
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.commons.lang3.time.StopWatch
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.transaction.TransactionStatus
import bard.db.audit.BardContextUtils
import org.hibernate.Session


def outFile = new File('reload_experiment_result_out.txt')
outFile.withWriter { writer ->
    List<String> eids = []
    List<String> failedEids = []
    new File('EIDs.txt').eachLine { String line ->
        String[] eidStrings = line.split(/\s/)
        assert eidStrings.every { String eidString -> eidString.isLong() }, "All EIDs must be a LONG number ${eidStrings}"
        eids.addAll(eidStrings)
    }
    writer.writeLine("EIDs: ${eids.join(', ')} (${eids.size()})")
    println("EIDs: ${eids.join(', ')}")

    SpringSecurityUtils.reauthenticate("gwalzer", null)
    StopWatch sw = new StopWatch()
    sw.start()
    println("started reloading experiment results")

    def experimentController = ctx.getBean("bard.db.project.ExperimentController")
    assert experimentController, "Could not find ExperimentController"
    PubchemImportService pubchemImportService = ctx.getBean("pubchemImportService")
    assert pubchemImportService, "Could not find PubchemImportService"

    Integer index = 1
    for (eid in eids) {
        def aid
        def experiment
        try {
            experiment = Experiment.get(eid)
            assert experiment, "Experiment ${experiment.id} does not exist"
            ExternalReference xref = experiment.externalReferences.find { it.extAssayRef.startsWith("aid=") }
            assert xref, "Couldn't find ExternalReference object for eid=${experiment.id}"
            aid = Integer.parseInt(xref.extAssayRef.replace("aid=", ""))
            assert aid, "Couldn't find AID for experiment eid=${experiment.id}"

            String message = "Reloading for EID=${eid} [AID=${aid}; ADID=${experiment.assay.id}] (${index++}/${eids.size()})"
            println(message)
            writer.writeLine(message)

//            SpringSecurityUtils.doWithAuth("gwalzer") {
            Experiment.withTransaction { TransactionStatus transactionStatus ->
                Experiment.withSession { Session session ->
//                        BardContextUtils.setBardContextUsername(session, 'user')

                    //      comment below when ready to commit
                    //transactionStatus.setRollbackOnly()
                    ImportSummary results = pubchemImportService.recreateMeasuresAndLoad(true, eid.toLong(), { msg -> println("\tPubChemService: " + msg); writer.writeLine("\t" + msg) })

                    println("\t...Finished")
                    writer.writeLine("\t...Finished")
                }
            }
//            }
        } catch (Throwable exp) {
            failedEids << "\nEID=${eid} [AID=${aid}; ADID=${experiment?.assay?.id}]\n\tCause:\n\t${exp.cause}"
            String msg = "\tFailed reloading: ${ExceptionUtils.getStackTrace(exp)}"
            println(msg)
            writer.writeLine(msg)
        }
    }

    ctx.getBean('sessionFactory').currentSession.flush()
    sw.stop()
    String message = "finished processing flush() duration: ${sw}\nFailed EIDs:\n\n${failedEids.join('\n')}"
    println(message)
    writer.writeLine(message)
}
