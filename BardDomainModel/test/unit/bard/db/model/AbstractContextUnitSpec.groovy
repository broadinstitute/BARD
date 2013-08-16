package bard.db.model

import bard.db.dictionary.Element
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
                final Element element = Element.findByLabel(label) ?: Element.build(label: label)
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
