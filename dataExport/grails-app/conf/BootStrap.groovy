import bard.db.dictionary.Element
import grails.util.Environment
import groovy.sql.Sql

import javax.sql.DataSource

class BootStrap {
    DataSource dataSource
    def grailsApplication

    def init = { servletContext ->
    }
    def destroy = {
    }

}
