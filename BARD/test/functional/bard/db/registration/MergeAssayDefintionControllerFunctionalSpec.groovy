package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.experiment.Experiment
import groovy.sql.Sql
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Unroll
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

import javax.servlet.http.HttpServletResponse

/**
 *
 * Merging of Assays should only be done by Administrators
 *
 *
 *
 */
@Unroll
class MergeAssayDefintionControllerFunctionalSpec extends BardControllerFunctionalSpec {
    static final String controllerUrl = getBaseUrl() + "mergeAssayDefinition/"

    @Shared
    List<Long> assayIdList = []  //we keep ids of all assays here so we can delete after all the tests have finished
    @Shared
    Map experimentData = [:]

    def setupSpec() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        Map assayData = (Map) remote.exec({
            //Create two assays
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            String childLabel = "child"
            String parentLabel = "parent"
            Element childElement = Element.findByLabel(childLabel)
            if (!childElement) {
                childElement = Element.build(label: childLabel).save(flush: true)
            }
            Measure childMeasure = Measure.findByResultType(childElement)
            if (!childMeasure) {
                childMeasure = Measure.build(resultType: childElement).save(flush: true)
            }

            Element parentElement = Element.findByLabel(parentLabel)
            if (!parentElement) {
                parentElement = Element.build(label: parentLabel).save(flush: true)
            }
            Measure parentMeasure = Measure.findByResultType(parentElement)
            if (!parentMeasure) {
                parentMeasure = Measure.build(resultType: parentElement, childMeasures: [childMeasure] as Set).save(flush: true)
            }
            childMeasure.parentMeasure = parentMeasure
            childMeasure.parentChildRelationship = HierarchyType.SUPPORTED_BY
            childMeasure.save(flush: true)

            Assay assay1 = Assay.build(assayName: "Assay Name10", measures: [childMeasure, parentMeasure] as Set).save(flush: true)
            String experimentsAlias = "experiment1"
            final Experiment experiment1 = Experiment.build(experimentName: experimentsAlias, assay: assay1, capPermissionService: null).save(flush: true)
            ExternalReference.build(extAssayRef: "aid=2", experiment: experiment1).save(flush: true)

            AssayContext.build(assay: assay1, contextName: "alpha1").save(flush: true)

            Assay assay2 = Assay.build(assayName: "Assay Name10", measures: [childMeasure, parentMeasure] as Set).save(flush: true)

            experimentsAlias = "experiment2"
            final Experiment experiment2 = Experiment.build(experimentName: experimentsAlias, assay: assay2, capPermissionService: null).save(flush: true)
            ExternalReference.build(extAssayRef: "aid=1", experiment: experiment2).save(flush: true)
            AssayContext.build(assay: assay2, contextName: "alpha2").save(flush: true)
            //create assay context
            return [assayId1: assay1.id, assayId2: assay2.id, experimentId1: experiment1.id, experimentId2: experiment2.id]
        })
        experimentData.put("experimentId1", assayData.experimentId1)
        experimentData.put("experimentId2", assayData.experimentId2)
        assayIdList.add(assayData.assayId1)
        assayIdList.add(assayData.assayId2)


    }     // run before the first feature method
    def cleanupSpec() {

        Sql sql = Sql.newInstance(dburl, dbusername,
                dbpassword, driverClassName)
        sql.call("{call bard_context.set_username(?)}", [TEAM_A_1_USERNAME])

        sql.execute("DELETE FROM EXTERNAL_REFERENCE WHERE EXPERIMENT_ID=${experimentData.experimentId1}")
        sql.execute("DELETE FROM EXTERNAL_REFERENCE WHERE EXPERIMENT_ID=${experimentData.experimentId2}")
        sql.execute("DELETE FROM EXPRMT_MEASURE WHERE EXPERIMENT_ID=${experimentData.experimentId1}")
        sql.execute("DELETE FROM EXPRMT_MEASURE WHERE EXPERIMENT_ID=${experimentData.experimentId2}")
        sql.execute("DELETE FROM EXPRMT_CONTEXT WHERE EXPERIMENT_ID=${experimentData.experimentId1}")
        sql.execute("DELETE FROM EXPRMT_CONTEXT WHERE EXPERIMENT_ID=${experimentData.experimentId2}")
        for (Long assayId : assayIdList) {
            sql.execute("DELETE FROM EXPERIMENT WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM ASSAY_CONTEXT WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM MEASURE WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM ASSAY WHERE ASSAY_ID=${assayId}")
        }
    }

    def 'test index #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "index", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_FOUND

    }

    def 'test index - forbidden  #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "index", team, teamPassword)

        when:
        client.get()
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN


    }

    def 'test show #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "show", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_OK

    }

    def 'test show - forbidden  #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "show", team, teamPassword)

        when:
        client.get()
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN


    }

}
