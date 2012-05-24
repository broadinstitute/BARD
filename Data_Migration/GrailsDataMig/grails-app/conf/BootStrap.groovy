import db.util.Inserter

class BootStrap {

    def init = { servletContext ->
        Inserter inserter  = new Inserter()
        inserter.runInsert(602305 as long)
    }
    def destroy = {
    }
}
