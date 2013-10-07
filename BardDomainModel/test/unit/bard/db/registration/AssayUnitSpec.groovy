package bard.db.registration

import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/11/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Measure])
@Mock([Assay, Measure])
@Unroll
class AssayUnitSpec extends Specification {
    def 'test allowsNewExperiments when #desc'() {
        when:
        Set measures = new HashSet()
        for (int i = 0; i < measureCount; i++) {
            measures.add(Measure.build())
        }
        Assay assay = Assay.build(assayType: assayType, assayStatus: assayStatus, measures: measures)

        then:
        assay.allowsNewExperiments() == expectedAllowsNewExperiments

        where:
        desc              | assayType          | assayStatus         | measureCount | expectedAllowsNewExperiments
        'retired assay'   | AssayType.REGULAR  | AssayStatus.RETIRED | 1            | false
        'template assay'  | AssayType.TEMPLATE | AssayStatus.DRAFT   | 1            | false
        'everything good' | AssayType.REGULAR  | AssayStatus.DRAFT   | 1            | true
    }
}