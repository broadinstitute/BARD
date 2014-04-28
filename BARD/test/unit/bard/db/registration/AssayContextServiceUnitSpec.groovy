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
import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.experiment.ExperimentMeasure
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 9/4/12
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, AssayContext, AssayContextItem, AssayContextExperimentMeasure, ExperimentMeasure])
@Mock([AssayContext, AssayContextItem, AssayContextExperimentMeasure, ExperimentMeasure])
@TestFor(AssayContextService)
@Unroll
class AssayContextServiceUnitSpec extends Specification {

    @Shared String ORIGINAL_CONTEXT_NAME = 'original title'





    void "test addItemToEndOfList #desc"() {
        given: 'an a'

        AssayContext targetAssayContext = AssayContext.build(assayContextItems: createAssayContextItem(numberOfExistingContextItems))
        AssayContext sourceAssayContext = AssayContext.build(contextName: ORIGINAL_CONTEXT_NAME)
        sourceAssayContext.addToAssayContextItems(AssayContextItem.build())
        AssayContextItem draggedAssayContextItem = sourceAssayContext.assayContextItems.first()
        assert sourceAssayContext.assayContextItems.size() == 1
        assert sourceAssayContext == draggedAssayContextItem.assayContext

        when:
        service.addItem(draggedAssayContextItem, targetAssayContext, targetAssayContext.assay.id)

        then:
        assertItemAdded(targetAssayContext, draggedAssayContextItem, sizeAfterAdd, indexOfAddedItem)

        where:
        desc                            | numberOfExistingContextItems | indexOfAddedItem | sizeAfterAdd
        'add item to empty list'        | 0                            | 0                | 1
        'add item to list with 1 item'  | 1                            | 1                | 2
        'add item to list with 2 items' | 2                            | 2                | 3

    }

    void "test addItemToEndOfList when item is already in list"() {
        given: 'an item already in an AssayContext'
        AssayContext assayContext = AssayContext.build()
        AssayContextItem item = new AssayContextItem()
        assayContext.addToAssayContextItems(item)


        when: 'it is added to the assayContext again'
        service.addItem(assayContext.assayContextItems.first(), assayContext, null)

        then: 'do nothing, particulary throw an IOOBE' // if you didn't guess the earlier code was resulting in a IOOBE
        notThrown(IndexOutOfBoundsException)
        assayContext.assayContextItems.size() == 1
        item == assayContext.assayContextItems.first()
    }

    void "test addItemAtIndex #desc"() {
        given:
        AssayContext targetAssayContext = AssayContext.build(assayContextItems: createAssayContextItem(numberOfExistingContextItems))
        AssayContext sourceAssayContext = AssayContext.build(contextName: ORIGINAL_CONTEXT_NAME)
        sourceAssayContext.addToAssayContextItems(AssayContextItem.build())
        AssayContextItem draggedAssayContextItem = sourceAssayContext.assayContextItems.first()
        assert sourceAssayContext.assayContextItems.size() == 1
        assert sourceAssayContext == draggedAssayContextItem.assayContext

        when:
        service.addItem(indexOfAddedItem, draggedAssayContextItem, targetAssayContext)

        then:
        assertItemAdded(targetAssayContext, draggedAssayContextItem, sizeAfterAdd, indexOfAddedItem)


        where:
        desc                    | numberOfExistingContextItems | indexOfAddedItem | sizeAfterAdd
        'addItem to empty list' | 0                            | 0                | 1
        'addItem at index 0'    | 1                            | 0                | 2
        'addItem at index 1'    | 2                            | 1                | 3

    }

    private void assertItemAdded(AssayContext targetAssayContext, AssayContextItem draggedAssayContextItem, int sizeAfterAdd, int indexOfAddedItem) {
        assert draggedAssayContextItem.assayContext == targetAssayContext
        assert draggedAssayContextItem in targetAssayContext.assayContextItems
        assert sizeAfterAdd == targetAssayContext.assayContextItems.size()
        assert indexOfAddedItem == targetAssayContext.assayContextItems.indexOf(draggedAssayContextItem)
    }

    private List<AssayContextItem> createAssayContextItem(int i) {
        List<AssayContextItem> items = []
        i.times {
            items << new AssayContextItem(attributeType: AttributeType.Fixed, attributeElement: new Element())
        }
        items
    }


}
