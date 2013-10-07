package bard.admin

import bard.db.people.Person
import bard.db.people.Role
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class UserController {


    def create = {
       redirect controller: "person", action: "list"
    }

    def save = {
        redirect controller: "person", action: "list"
    }

    def edit = {
        redirect controller: "person", action: "list"
    }

    def update = {
        redirect controller: "person", action: "list"

    }

    def delete = {
        redirect controller: "person", action: "list"
    }

    def search = {
        redirect controller: "person", action: "list"
    }

    def userSearch = {

        redirect controller: "person", action: "list"
    }

    /**
     * Ajax call used by autocomplete textfield.
     */
    def ajaxUserSearch = {

        redirect controller: "person", action: "list"
    }

    protected void addRoles(user) {
        throw new RuntimeException("handled by Person Controller class")
    }

    protected Map buildUserModel(user) {

        throw new RuntimeException("handled by Person Controller class")
    }

    protected findById() {
        throw new RuntimeException("handled by Person Controller class")
    }

    protected List sortedRoles() {
        throw new RuntimeException("handled by Person Controller class")
    }

}