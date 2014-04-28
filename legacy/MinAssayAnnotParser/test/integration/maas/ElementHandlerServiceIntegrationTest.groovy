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

package maas

import bard.db.dictionary.Element
import org.junit.Ignore

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/2/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
class ElementHandlerServiceIntegrationTest extends GroovyTestCase {

    def elementHandlerService
    public void testAddElements() {
        def elements = ["asb":"abcded", "abcd":"ttt"]
        def elementParent = ["asb":555, "abcd":555]
        elementHandlerService.addMissingElement("xiaorong", elements, elementParent)
        Element element = Element.findByLabel("asb")
        assert element.description == "abcded"
        assert element.modifiedBy == "xiaorong"
    }

    public void testLoadElements() {
        Map elementAndDescription = [
                'science officer' : ''
        ]

// element parent
        Map elementParent = [
                'science officer' : 555  // id = 555   'project information'
        ]
        elementHandlerService.addMissingElement("xiaorong", elementAndDescription, elementParent)
    }

    @Ignore
    public void testAddMissingName() {
        String fileWithUniqueName = 'test/exampleData/maas/missingNameUniq.txt'
        elementHandlerService.addMissingName(fileWithUniqueName)
    }

    public void testBuild() {
       def elementDescription = [:]
        def elementParent = [:]
       ElementHandlerService.build(null, elementDescription, elementParent)
        println(elementDescription)
        println(elementParent)
    }
}
