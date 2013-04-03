package bard.db.dictionary

import org.apache.commons.lang3.builder.CompareToBuilder

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 4/3/13
 * Time: 10:39 AM
 * Sort case insensitively by abbreviation then label
 */
class ElementAbbreviationLabelComparator implements Comparator<Element> {
    @Override
    int compare(Element lhs, Element rhs) {
        CompareToBuilder compareToBuilder  =new CompareToBuilder()
        compareToBuilder.append(getIdenifier(lhs), getIdenifier(rhs))
        return compareToBuilder.toComparison()
    }

    private String getIdenifier(Element element){
        String identifier = [element?.abbreviation, element?.label].findAll().join(' ')
        identifier.toLowerCase()
    }
}
