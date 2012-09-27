package bard.db.dictionary

import org.apache.commons.lang3.builder.CompareToBuilder

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 9/27/12
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
class DescriptorLabelComparator implements Comparator<Descriptor> {


    int compare(Descriptor lhs, Descriptor rhs) {
        CompareToBuilder compareToBuilder = new CompareToBuilder()
        def lhsLowerCaseLabels = collectLabels(lhs)
        def rhsLowerCaseLabels = collectLabels(rhs)
        compareToBuilder.append(lhsLowerCaseLabels.toString(), rhsLowerCaseLabels.toString())
        return compareToBuilder.toComparison()
    }

    public List collectLabels(Descriptor lhs) {
        lhs?.getPath()?.collect {it.label.toLowerCase()}
    }
}

