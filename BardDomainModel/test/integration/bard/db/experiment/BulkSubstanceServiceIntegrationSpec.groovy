package bard.db.experiment

import bard.db.BardIntegrationSpec
import grails.plugin.spock.IntegrationSpec

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/12/13
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
class BulkSubstanceServiceIntegrationSpec extends BardIntegrationSpec {
    BulkSubstanceService bulkSubstanceService

    long nonexistantId = 999999

    def testFindMissingSubstances() {
        given:
        Substance substance = Substance.build()
        Substance.withSession { session -> session.flush() }

        when:
        assert Substance.get(nonexistantId) == null
        def missing = bulkSubstanceService.findMissingSubstances([substance.id,nonexistantId])

        then:
        missing.size() == 1
        missing[0] == nonexistantId
    }

    def testInsertSubstances() {
        when:
        assert Substance.get(nonexistantId) == null

        bulkSubstanceService.insertSubstances([nonexistantId], "testuser")

        then:
        Substance substance = Substance.get(nonexistantId)
        substance != null
    }
}
