package bard.db.experiment

import bard.db.registration.ExternalReference
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.commons.lang3.time.StopWatch
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.transaction.TransactionStatus
import bard.db.audit.BardContextUtils
import org.hibernate.Session


def outFile = new File('reload_experiment_result_out.txt')
outFile.withWriter { writer ->
    List<Long> eids = []
    List<String> failedEids = []
    new File('EIDs.txt').eachLine { String line ->
        String[] eidStrings = line.split(/\s/)
        assert eidStrings.every { String eidString -> eidString.isLong() }, "All EIDs must be a LONG number ${eidStrings}"
        eids.addAll(eidStrings as List<Long>)
    }
    writer.writeLine("EIDs: ${eids.join(', ')}")
    println("EIDs: ${eids.join(', ')}")

    SpringSecurityUtils.reauthenticate("user", "user")
    StopWatch sw = new StopWatch()
    sw.start()
    println("started reloading experiment results")

    def experimentController = ctx.getBean("bard.db.project.ExperimentController")
    assert experimentController, "Could not find ExperimentController"

    Integer index = 1
    for (eid in eids) {
        def experiment = Experiment.get(eid)
        assert experiment, "Experiment ${experiment.id} does not exist"
        ExternalReference xref = experiment.externalReferences.find { it.extAssayRef.startsWith("aid=") }
        def aid = Integer.parseInt(xref.extAssayRef.replace("aid=", ""))

        String message = "Reloading for EID=${eid} [AID=${aid}; ADID=${experiment.assay.id}] (${index++}/${eids.size()})"
        println(message)
        writer.writeLine(message)
        try {
            Experiment.withTransaction { TransactionStatus transactionStatus ->
                Experiment.withSession { Session session ->
                    BardContextUtils.setBardContextUsername(session, 'user')
                    experimentController.reloadResults(new Long(eid))
                    //      comment below when ready to commit
                    //transactionStatus.setRollbackOnly()

                    println("\t...Finished")
                    writer.writeLine("\t...Finished")
                }
            }
        } catch (Exception exp) {
            failedEids << "\nEID=${eid} [AID=${aid}; ADID=${experiment.assay.id}]\n\tCause:\n\t${exp.cause}"
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
