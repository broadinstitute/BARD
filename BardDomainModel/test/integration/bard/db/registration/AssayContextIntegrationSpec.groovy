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

import bard.db.BardIntegrationSpec
import bard.db.audit.BardContextUtils
import bard.db.dictionary.Element
import bard.db.enums.ExpectedValueType
import bard.db.people.Role
import grails.plugin.spock.IntegrationSpec
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextIntegrationSpec extends BardIntegrationSpec {

    AssayContext assayContext
    Element textAttribute

    Session session

    @Before
    void doSetup() {
        session = sessionFactory.currentSession
        textAttribute = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        Role role = Role.findByAuthority("authority")
        if (!role) {
            role = Role.build(authority: "authority")
        }

        Assay assay = Assay.build(ownerRole: role).save(flush: true)
        assayContext =AssayContext.buildWithoutSave(assay: assay)
        assert assayContext.assay.save()
    }

    void "test list order of assayContextItems persisted"() {

        given: 'an ordered list of assayContextItems'
        List<AssayContextItem> assayContextItems = [AssayContextItem.build(assayContext: assayContext, attributeElement: textAttribute, valueDisplay: 'a'),
                AssayContextItem.build(assayContext: assayContext, attributeElement: textAttribute, valueDisplay: 'b')]

        assert assayContext.save()
        def id = assayContext.getId()

        when:
        assayContext = flushClearReload(id)


        then: 'order preserved'
        assayContext.assayContextItems.size() == 2
        assayContext.assayContextItems*.valueDisplay == ['a', 'b']

    }

    private AssayContext flushClearReload(long id) {
        session.flush()
        session.clear()
        assayContext = null
        assayContext = AssayContext.get(id)
        assayContext
    }

}
