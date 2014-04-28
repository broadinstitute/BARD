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

import bard.db.dictionary.Element;
import bard.db.enums.ValueType
import bard.db.model.ExternalOntologyValue
import bard.db.model.NumericValue
import bard.db.model.RangeValue
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 8/22/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
public class ContextItemValueUnitSpec extends Specification {
    @Shared
    Element element = new Element(label: "elementLabel")

    void "test context item of type #type"() {
        when:
        AssayContextItem item = new AssayContextItem()
        item.setAttributeElement(new Element(label: "attr"))
        mutate.call(item)

        then:
        item.valueMin == xValueMin
        item.valueMax == xValueMax
        item.valueNum == xValueNum
        item.valueDisplay == xValueDisplay

        item.valueElement == xValueElement
        item.qualifier == xQualifier
        item.extValueId == xExtId
        item.valueType == type

        item.getValue() == xValue

        where:
        type                        | mutate                                                             | xValueNum | xValueMin | xValueMax | xValueDisplay  | xValueElement | xQualifier | xExtId | xValue
        ValueType.ELEMENT           | { AssayContextItem i -> i.setDictionaryValue(element) }            | null      | null      | null      | "elementLabel" | element       | null       | null   | element
        ValueType.NONE              | { AssayContextItem i -> i.setNoneValue() }                         | null      | null      | null      | null           | null          | null       | null   | null
        ValueType.NUMERIC           | { AssayContextItem i -> i.setNumericValue("= ", 5) }               | 5.0       | null      | null      | "5.0"          | null          | "= "       | null   | new NumericValue(number: 5.0, qualifier: "= ")
        ValueType.FREE_TEXT         | { AssayContextItem i -> i.setFreeTextValue("x") }                  | null      | null      | null      | "x"            | null          | null       | null   | "x"
        ValueType.EXTERNAL_ONTOLOGY | { AssayContextItem i -> i.setExternalOntologyValue("xv", "disp") } | null      | null      | null      | "disp"         | null          | null       | "xv"   | new ExternalOntologyValue(extValueId: "xv", valueDisplay: "disp")
        ValueType.RANGE             | { AssayContextItem i -> i.setRange(2, 3) }                         | null      | 2.0       | 3.0       | "2.0 - 3.0"    | null          | null       | null   | new RangeValue(valueMin: 2.0, valueMax: 3.0)
    }
}
