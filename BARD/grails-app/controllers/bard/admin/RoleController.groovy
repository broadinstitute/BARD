package bard.admin

import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.ui.AbstractS2UiController
@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class RoleController extends AbstractS2UiController {

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

    def roleSearch = {

        redirect controller: "person", action: "list"
    }

    /**
     * Ajax call used by autocomplete textfield.
     */
    def ajaxRoleSearch = {
        redirect controller: "person", action: "list"
    }

    protected search(String name, boolean nameOnly, Integer max, Integer offset) {
    }

    protected findById() {

    }
}