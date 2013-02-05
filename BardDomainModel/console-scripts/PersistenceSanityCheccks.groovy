import bard.db.registration.*

def assay = new Assay(assayName:'foo',assayVersion:1, assayStatus:'Pending', readyForExtraction:'Ready')
assay.save(flush:true,failOnError:true)
