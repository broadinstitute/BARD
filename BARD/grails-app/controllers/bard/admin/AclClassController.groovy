package bard.admin

import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclClass


@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class AclClassController extends grails.plugins.springsecurity.ui.AclClassController {

    protected String lookupClassName() { AclClass.name }

    protected Class<?> lookupClass() { AclClass }
}
