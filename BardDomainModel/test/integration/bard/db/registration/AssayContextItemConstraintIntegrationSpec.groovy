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

import bard.db.experiment.Experiment
import bard.db.model.AbstractContextItemIntegrationSpec
import bard.db.project.ProjectExperimentContextItem
import org.junit.Before
import spock.lang.Ignore
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class AssayContextItemConstraintIntegrationSpec extends AbstractContextItemIntegrationSpec<AssayContextItem> {

    @Before
    @Override
    void doSetup() {
        this.domainInstance = constructInstance([:])
    }

    AssayContextItem constructInstance(Map props) {
        def instance = AssayContextItem.buildWithoutSave(props)
        instance.attributeElement.save(failOnError:true, flush: true)

        return instance
    }

    void "test canDelete has No experiments #desc"() {
        given:
        domainInstance.attributeType = attributeType
        when:
        boolean canDelete = AssayContextItem.canDeleteContextItem(domainInstance)
        then:
        assert canDelete == valid

        where:
        desc                   | attributeType       | valid
        "Fixed Attribute Type" | AttributeType.Fixed | true
        "Free Attribute Type"  | AttributeType.Free  | true
        "List Attribute Type"  | AttributeType.List  | true
        "Range Attribute Type" | AttributeType.Range | true
    }

    void "test canDelete has experiments #desc"() {
        given:
        domainInstance.attributeType = attributeType
        Assay assay = domainInstance.assayContext.assay
        assay.experiments = [new Experiment()]
        when:
        boolean canDelete = AssayContextItem.canDeleteContextItem(domainInstance)
        then:
        assert canDelete == valid

        where:
        desc                   | attributeType       | valid
        "Fixed Attribute Type" | AttributeType.Fixed | true
        "Free Attribute Type"  | AttributeType.Free  | false
        "List Attribute Type"  | AttributeType.List  | false
        "Range Attribute Type" | AttributeType.Range | false

    }


    void "test safeToDeleteContextItem #desc"() {
        given:
        domainInstance.attributeType = attributeType
        when:
        boolean safeToDelete = AssayContextItem.safeToDeleteContextItem(domainInstance)
        then:
        assert safeToDelete == valid

        where:
        desc                   | attributeType       | valid
        "Fixed Attribute Type" | AttributeType.Fixed | true
        "Free Attribute Type"  | AttributeType.Free  | false
        "List Attribute Type"  | AttributeType.List  | false
        "Range Attribute Type" | AttributeType.Range | false

    }
    @Ignore
    void "test attributeType constraints #desc attributeType: '#valueUnderTest'"() {

        final String field = 'attributeType'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        RuntimeException e = thrown()
        e.message == 'Unknown attributeType: null'

        where:
        desc   | valueUnderTest
        'null' | null
    }

}
