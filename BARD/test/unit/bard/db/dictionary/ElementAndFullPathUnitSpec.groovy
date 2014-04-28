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

package bard.db.dictionary

import spock.lang.Specification
import grails.buildtestdata.mixin.Build

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 4/24/13
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Element, ElementHierarchy])
class ElementAndFullPathUnitSpec extends Specification {
    private static final String delimeter = "/"

    void "simple test splitPathIntoTokens"() {
        setup:
        String pathString = """
      /a / b/c /d /    """

        when:
        List<String> tokenList = ElementAndFullPath.splitPathIntoTokens(pathString, delimeter)

        then:
        tokenList.size() == 4
        tokenList.get(0) == "a"
        tokenList.get(1) == "b"
        tokenList.get(2) == "c"
        tokenList.get(3) == "d"
    }

    void "empty test splitPathIntoTokens"() {
        setup:
        String pathString = "/"

        when:
        List<String> tokenList = ElementAndFullPath.splitPathIntoTokens(pathString, delimeter)

        then:
        tokenList.size() == 0
    }

    void "invalid path test entries before first delimeter"() {
        setup:
        String pathString = "abcd/efghij/"

        when:
        ElementAndFullPath.splitPathIntoTokens(pathString, delimeter)

        then:
        thrown(InvalidElementPathStringException)
    }

    void "invalid path test entries after last delimeter"() {
        setup:
        String pathString = "/abcd/efghij"

        when:
        ElementAndFullPath.splitPathIntoTokens(pathString, delimeter)

        then:
        thrown(InvalidElementPathStringException)
    }

    void "no delimeters just text"() {
        setup:
        String pathString = "abcd"

        when:
        ElementAndFullPath.splitPathIntoTokens(pathString, delimeter)

        then:
        thrown(InvalidElementPathStringException)
    }

    void "test pathContains"() {
        setup:
        ElementHierarchy eh0 = ElementHierarchy.build()
        ElementHierarchy eh1 = ElementHierarchy.build()
        Element element = Element.build()

        ElementAndFullPath elementAndFullPath = new ElementAndFullPath(eh1.childElement)
        elementAndFullPath.path.add(eh0)
        elementAndFullPath.path.add(eh1)

        when:
        boolean expectTrue = elementAndFullPath.pathContainsElement(eh0.childElement)
        boolean expectFalse = elementAndFullPath.pathContainsElement(element)

        then:
        assert expectTrue
        assert ! expectFalse
    }
}
