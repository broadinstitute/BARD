import org.springframework.transaction.support.DefaultTransactionStatus
import org.apache.commons.lang.time.StopWatch
import depositor.neighbor.Relation

/**
 * Parses and persists the parent/child AID relationships that the PubChem depositors have been uploaded.
 * Doesn't guarantee uniqueness.
 */
Integer i = 0
StopWatch globalTime = new StopWatch()
globalTime.start()
Date startTime= new Date()
Date endTime

Relation.withTransaction {DefaultTransactionStatus status ->
    File infile = new File("C:/BARD_DATA_MIGRATION/SwamidassProjectFlow/pcassay_pcassay_depositorneighbor.txt")
    infile.eachLine { String line ->

        String[] values = line.trim().split()
        String parent = values[0]
        Long parentId = new Long(parent - 'AID')
        String child = values[1]
        Long childId = new Long(child - 'AID')

        Relation relation = new Relation(parent: parent, parentId: parentId, child: child, childId: childId)
//        println("${i}: ${relation.dump()}")
        relation.save(flush: false)

        if (i++ % 10000 == 0) {
            endTime = new Date()
            println("${i} of rows persisted so far (${(endTime.time - startTime.time)/1000} sec; total time so far: ${globalTime.time/1000} sec)")
            startTime = endTime
            status.flush()
        }
    }
}