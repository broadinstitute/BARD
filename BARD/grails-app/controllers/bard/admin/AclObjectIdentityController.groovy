package bard.admin

import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclClass
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclObjectIdentity
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclSid
import org.springframework.dao.DataIntegrityViolationException

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class AclObjectIdentityController extends grails.plugins.springsecurity.ui.AclObjectIdentityController {

    protected String lookupClassName() { AclObjectIdentity.name }

    protected Class<?> lookupClass() { AclObjectIdentity }

    protected Class<?> lookupAclSidClass() { AclSid }

    protected Class<?> lookupAclClassClass() { AclClass }
    def create = {
        [aclObjectIdentity: lookupClass().newInstance(params),
                classes: lookupAclClassClass().list(), sids: lookupAclSidClass().list()]
    }

    def save = {
        throw new RuntimeException("Save not supported. Create the entity directly")
    }

    def edit = {
        throw new RuntimeException("Edit not supported. Edit the entity directly")
    }

    def update = {
        throw new RuntimeException("Update not supported. Update the entity directly")
    }

    def delete = {
        def aclObjectIdentity = findById()
        if (!aclObjectIdentity) return

        try {
            springSecurityUiService.deleteAclObjectIdentity aclObjectIdentity
            flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'aclObjectIdentity.label', default: 'AclObjectIdentity'), params.id])}"
            redirect action: search
        }
        catch (DataIntegrityViolationException e) {
            flash.error = "${message(code: 'default.not.deleted.message', args: [message(code: 'aclObjectIdentity.label', default: 'AclObjectIdentity'), params.id])}"
            redirect action: edit, id: params.id
        }
    }

    def aclObjectIdentitySearch = {

        boolean useOffset = params.containsKey('offset')
        setIfMissing 'max', 10, 100
        setIfMissing 'offset', 0

        def hql = new StringBuilder('FROM ').append(lookupClassName()).append(' oid WHERE 1=1 ')
        def queryParams = [:]

        if (params.objectId) {
            hql.append " AND oid.objectId=:objectId"
            queryParams.objectId = params.long('objectId')
        }

        for (name in ['ownerSid', 'parent', 'aclClass']) {
            if (params[name] && params[name] != 'null') {
                long id = params.long(name)
                if (name == 'ownerSid') name = 'owner'
                hql.append " AND oid.${name}.id=:$name"
                queryParams[name] = id
            }
        }

        for (name in ['entriesInheriting']) {
            Integer value = params.int(name)
            if (value) {
                hql.append " AND oid.$name=:$name"
                queryParams[name] = value == 1
            }
        }

        int totalCount = lookupClass().executeQuery("SELECT COUNT(DISTINCT oid) $hql", queryParams)[0]

        Integer max = params.int('max')
        Integer offset = params.int('offset')

        String orderBy = ''
        if (params.sort) {
            orderBy = " ORDER BY oid.$params.sort ${params.order ?: 'ASC'}"
        }

        final String query = "SELECT oid $hql $orderBy"
        def results = lookupClass().executeQuery(
                query,
                queryParams, [max: max, offset: offset])
        def model = [results: results, totalCount: totalCount, searched: true,
                classes: lookupAclClassClass().list(), sids: lookupAclSidClass().list()]

        // add query params to model for paging
        for (name in ['aclClass', 'objectId', 'entriesInheriting', 'ownerSid', 'parent']) {
            model[name] = params[name]
        }

        render view: 'search', model: model
    }
}
