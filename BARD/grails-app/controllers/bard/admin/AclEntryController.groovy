package bard.admin

import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclEntry
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclSid
import org.springframework.dao.DataIntegrityViolationException

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class AclEntryController extends grails.plugins.springsecurity.ui.AclEntryController {

    protected String lookupClassName() { AclEntry.name }

    protected Class<?> lookupClass() { AclEntry }

    protected Class<?> lookupAclSidClass() { AclSid }
    def create = {
        throw new RuntimeException("Save not supported. Create the entity directly")
    }

    def save = {
        throw new RuntimeException("Save not supported. Edit the owning role of the entity directly")
    }

    def edit = {
        throw new RuntimeException("Edit not supported. Edit the owning role of the entity directly")
    }

    def update = {

        throw new RuntimeException("Update not supported. Edit the owning role of the entity directly")
    }

}
