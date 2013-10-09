package bard.admin

import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclSid
@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class AclSidController extends grails.plugins.springsecurity.ui.AclSidController {

    protected String lookupClassName() { AclSid.name }

    protected Class<?> lookupClass() { AclSid }
}
