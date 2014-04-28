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

package bard.db.model

import bard.db.dictionary.Element
import bard.db.enums.ExpectedValueType
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/8/13
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
abstract class AbstractContextUnitSpec<T extends AbstractContext> extends Specification  {

    T domainInstance

    @Before
    abstract void doSetup()

    void "test getContextType #desc "() {


        when:
        if (itemAttibuteLabels) {
            itemAttibuteLabels.each { label ->
                final Element element = Element.findByLabel(label) ?: Element.build(label: label, expectedValueType: ExpectedValueType.FREE_TEXT)
                domainInstance.addContextItem(domainInstance.getItemSubClass().build(attributeElement: element))
            }
        }

        then:
        domainInstance.getDataExportContextType()?.label == expectedContextTypeLabel


        where:
        desc                                            | itemAttibuteLabels          | expectedContextTypeLabel
        'null expected when no defining attributes'     | null                        | null
        'only biology'                                  | ['biology']                 | 'biology'
        'only probe report'                             | ['probe report']            | 'probe report'
        'only biology if both biology and probe report' | ['biology', 'probe report'] | 'biology'
        'biology if more than one biology'              | ['biology', 'biology']      | 'biology'
    }


}
