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

        item.valueElement == (xValueElement != null ? xValueElement.call() : null)
        item.qualifier == xQualifier
        item.extValueId == xExtId
        item.valueType == type

        item.getValue() == xValue.call()

        where:
        type                        | mutate                                                  | xValueNum | xValueMin | xValueMax | xValueDisplay | xValueElement | xQualifier | xExtId  | xValue
        ValueType.ELEMENT           | {AssayContextItem i -> i.setDictionaryValue(element)}   | null      | null      | null      | "elementLabel"| {element}     | null       | null    | {element}
        ValueType.NONE              | {AssayContextItem i -> i.setNoneValue()}                | null      | null      | null      | null          | null          | null       | null    | {null}
        ValueType.NUMERIC           | {AssayContextItem i -> i.setNumericValue("= ", 5)}      | 5.0       | null      | null      | "= 5.0"       | null          | "= "       | null    | {new NumericValue(number: 5.0, qualifier: "= ")}
        ValueType.FREE_TEXT         | {AssayContextItem i -> i.setFreeTextValue("x")}         | null      | null      | null      | "x"           | null          | null       | null    | {"x"}
        ValueType.EXTERNAL_ONTOLOGY | {AssayContextItem i -> i.setExternalOntologyValue("xv", "disp")} | null| null   | null      | "disp"        | null          | null       | "xv"    | {new ExternalOntologyValue(extValueId: "xv", valueDisplay: "disp")}
        ValueType.RANGE             | {AssayContextItem i -> i.setRange(2,3)}                 | null      | 2.0       | 3.0       | "2.0 - 3.0"   | null          | null       | null    | {new RangeValue(valueMin: 2.0, valueMax: 3.0)}
    }
}
