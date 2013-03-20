package bard.validation.ext

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/19/13
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class BardExternalOntologyFactoryUnitSpec extends Specification {

    BardExternalOntologyFactory externalOntologyFactory = new BardExternalOntologyFactory()



    void "test foo"() {
        given:
        externalOntologyFactory.customExternalOntologyApiMap = customMap
        when:
        ExternalOntologyAPI externalOntologyApi = externalOntologyFactory.getExternalOntologyAPI(externalUrl)
        then:
        externalOntologyApi.getClass() == expectedApiImplClass

        where:
        desc                               | customMap                                                                  | externalUrl                               | expectedApiImplClass
        'empty map defaults to defaultIpl' | [:]                                                                        | 'http://www.ncbi.nlm.nih.gov/biosystems/' | ExternalOntologyNCBI.class
        'map used over default'            | ['http://www.ncbi.nlm.nih.gov/biosystems/': new ExternalOntologyUniprot()] | 'http://www.ncbi.nlm.nih.gov/biosystems/' | ExternalOntologyUniprot.class

    }
}
