/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.AssayType
import bard.db.enums.ExpectedValueType
import bard.db.enums.Status
import bard.db.guidance.owner.OneItemPerNonFixedAttributeElementRule
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

import static OneItemPerNonFixedAttributeElementRule.getErrorMsg
import static bard.db.guidance.owner.MinimumOfOneBiologyGuidanceRule.ASSAY_ONE_BIOLOGY_ATTRIBUTE_REQUIRED
import static bard.db.registration.AttributeType.Free

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/11/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, AssayContext, AssayContextItem, Element])
@Mock([Assay, AssayContext, AssayContextItem, Element])
@Unroll
class AssayUnitSpec extends Specification {
    def 'test allowsNewExperiments when #desc'() {
        when:
        Assay assay = Assay.build(assayType: assayType, assayStatus: assayStatus)

        then:
        assay.allowsNewExperiments() == expectedAllowsNewExperiments

        where:
        desc              | assayType          | assayStatus         | measureCount | expectedAllowsNewExperiments
        'retired assay'   | AssayType.REGULAR  | Status.RETIRED | 1            | false
        'template assay'  | AssayType.TEMPLATE | Status.DRAFT   | 1            | false
        'everything good' | AssayType.REGULAR  | Status.DRAFT   | 1            | true
    }

    void 'test that OneItemPerNonFixedAttributeElement is wired into assay'() {
        given: 'an assay with some context items'
        final Assay assay = Assay.build()
        expect:
        assay.getGuidanceRules()*.class*.simpleName == ['MinimumOfOneBiologyGuidanceRule','OneItemPerNonFixedAttributeElementRule','ContextItemShouldHaveValueRule']
    }


}
