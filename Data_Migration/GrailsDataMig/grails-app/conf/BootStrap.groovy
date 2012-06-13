import bard.db.util.Inserter
import bard.db.util.InsertAssayTest

class BootStrap {

    def init = { servletContext ->
        try {
            new InsertAssayTest().runInsertTest()
        }
        catch(Exception ex) {
            ex.printStackTrace()
        }
        println "FINISHED"
//        Inserter inserter  = new Inserter()
//        inserter.runInsert(602305 as long)
    }
    def destroy = {
    }
}
