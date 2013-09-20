package bard.admin

import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclEntry
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclSid

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class AclEntryController extends grails.plugins.springsecurity.ui.AclEntryController {

    protected String lookupClassName() { AclEntry.name }

    protected Class<?> lookupClass() { AclEntry }

    protected Class<?> lookupAclSidClass() { AclSid }
}
