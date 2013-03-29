package bard.validation.ext

import edu.scripps.fl.entrez.EUtilsWeb
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll
import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException


/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/1/13
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ExternalOntologyIntegrationSpec extends Specification {

    String NCBI_EMAIL = BardExternalOntologyFactory.NCBI_EMAIL
    String NCBI_TOOL = BardExternalOntologyFactory.NCBI_TOOL

    void "test valid urls externalOntologyFactory().getExternalOntologyAPI() for #externalUrl"() {
        when:
        println("url: $externalUrl")
        Properties props = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'test@test.com'])
        ExternalOntologyAPI externalOntologyAPI = new BardExternalOntologyFactory().getExternalOntologyAPI(externalUrl, props)

        then:
        (externalOntologyAPI != null) == notNull


        where:
        externalUrl                                                             | notNull
        "http://amigo.geneontology.org/cgi-bin/amigo/gp-details.cgi?gp=FB:FBgn" | true
        "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term="        | true

//        "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid="                                     | false // these should work but on 3/29/2013 started returning Unknow NCBI database
//        "http://omim.org/entry/"                                                                       | false // these should work but on 3/29/2013 started returning Unknow NCBI database
//        "http://www.ncbi.nlm.nih.gov/biosystems/"                                                      | false // these should work but on 3/29/2013 started returning Unknow NCBI database
//        "http://www.ncbi.nlm.nih.gov/gene/"                                                            | false // these should work but on 3/29/2013 started returning Unknow NCBI database
//        "http://www.ncbi.nlm.nih.gov/mesh/"                                                            | false // these should work but on 3/29/2013 started returning Unknow NCBI database
//        "http://www.ncbi.nlm.nih.gov/nuccore/"                                                         | false // these should work but on 3/29/2013 started returning Unknow NCBI database
//        "http://www.ncbi.nlm.nih.gov/omim/"                                                            | false // these should work but on 3/29/2013 started returning Unknow NCBI database
//        "http://www.ncbi.nlm.nih.gov/protein/"                                                         | false // these should work but on 3/29/2013 started returning Unknow NCBI database
//        "http://www.ncbi.nlm.nih.gov/pubmed/"                                                          | false // these should work but on 3/29/2013 started returning Unknow NCBI database
//        "http://www.ncbi.nlm.nih.gov/structure/?term="                                                 | false // these should work but on 3/29/2013 started returning Unknow NCBI database
//        "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id="                                  | false // these should work but on 3/29/2013 started returning Unknow NCBI database

        "http://www.uniprot.org/uniprot/" | true

        "http://cas.org/" | false
        "http://www.atcc.org/ATCCAdvancedCatalogSearch/ProductDetails/tabid/452/Default.aspx?ATCCNum=" | false
        "https://mli.nih.gov/mli/?dl_id=" | false
        "http://regid.org/find" | false

        "http://someUnknown.com" | false
    }

    void "test invalid urls new ConfigurableExternalOntologyFactory().getExternalOntologyAPI for #externalUrl"() {
        when:
        println("url: $externalUrl")
        Properties props = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'test@test.com'])
        new BardExternalOntologyFactory().getExternalOntologyAPI(externalUrl, props)

        then:
        ExternalOntologyException e = thrown()
        e.message == message

        where:
        externalUrl                                                   | message
        "http://www.ncbi.nlm.nih.gov/gquery/?term="                   | 'Unknown NCBI database gquery'
        "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid="    | 'Unknown NCBI database pcsubstance'       // should hopefully get fixed soon
        "http://omim.org/entry/"                                      | 'Unknown NCBI database omim'              // should hopefully get fixed soon
        "http://www.ncbi.nlm.nih.gov/biosystems/"                     | 'Unknown NCBI database biosystems'        // should hopefully get fixed soon
        "http://www.ncbi.nlm.nih.gov/gene/"                           | 'Unknown NCBI database gene'              // should hopefully get fixed soon
        "http://www.ncbi.nlm.nih.gov/mesh/"                           | 'Unknown NCBI database mesh'              // should hopefully get fixed soon
        "http://www.ncbi.nlm.nih.gov/nuccore/"                        | 'Unknown NCBI database nuccore'           // should hopefully get fixed soon
        "http://www.ncbi.nlm.nih.gov/omim/"                           | 'Unknown NCBI database omim'              // should hopefully get fixed soon
        "http://www.ncbi.nlm.nih.gov/protein/"                        | 'Unknown NCBI database protein'           // should hopefully get fixed soon
        "http://www.ncbi.nlm.nih.gov/pubmed/"                         | 'Unknown NCBI database pubmed'            // should hopefully get fixed soon
        "http://www.ncbi.nlm.nih.gov/structure/?term="                | 'Unknown NCBI database structure'         // should hopefully get fixed soon
        "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=" | 'Unknown NCBI database taxonomy'          // should hopefully get fixed soon

    }

    void "test successful externalOntologyAPI.findById for url: #externalUrl externalValueId: #externalValueId"() {
        when:
        println("url: $externalUrl externalValueId: $externalValueId")
        Properties props = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'test@test.com'])
        ExternalOntologyAPI extOntology = new BardExternalOntologyFactory().getExternalOntologyAPI(externalUrl, props)
        ExternalItem externalItem = extOntology.findById(externalValueId)

        then:
        println(externalItem)
        println(externalItem.display)
        println(externalItem.dump())
        externalItem.id == externalValueId
        externalItem.display

        where:
        externalUrl                                                      | externalValueId
//        "http://www.ncbi.nlm.nih.gov/gene/"                              | "9986"                // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/protein/"                           | "9966877"             // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/mesh/"                              | "68020170"            // should work but currently broken
//        "http://omim.org/entry/"                                         | "602706"              // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/omim/"                              | "602706"              // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id="    | "9986"                // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/nuccore/"                           | "91199539"            // should work but currently broken
        "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" | "GO:1901112"
        "http://www.uniprot.org/uniprot/"                                | "Q9Y6Q9"
    }

    void "test exception externalOntologyAPI.findById for url: #externalUrl externalValueId: '#externalValueId'"() {
        when:
        println("url: $externalUrl externalValueId: $externalValueId")
        Properties props = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'test@test.com'])
        ExternalOntologyAPI extOntology = new BardExternalOntologyFactory().getExternalOntologyAPI(externalUrl, props)
        ExternalItem externalItem = extOntology.findById(externalValueId)

        then:
        def e = thrown(expectedException)
        println(e.message)

        where:
        externalUrl                                                      | externalValueId | expectedException
        "http://www.ncbi.nlm.nih.gov/gene/"                              | null            | ExternalOntologyException
//        "http://www.ncbi.nlm.nih.gov/gene/"                              | " "             | IndexOutOfBoundsException // currently throwing ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/gene/"                              | ""              | ExternalOntologyException
        "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" | null            | ExternalOntologyException
        "http://www.uniprot.org/uniprot/"                                | null            | ExternalOntologyException
        "http://www.uniprot.org/uniprot/"                                | " "             | RemoteDataAccessException
        "http://www.uniprot.org/uniprot/"                                | ""              | RemoteDataAccessException

        "http://www.ncbi.nlm.nih.gov/gene/"                              | "9986"          | ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/protein/"                           | "9966877"       | ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/mesh/"                              | "68020170"      | ExternalOntologyException
        "http://omim.org/entry/"                                         | "602706"        | ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/omim/"                              | "602706"        | ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id="    | "9986"          | ExternalOntologyException
    }



    void "test successful externalOntologyAPI.findMatching for url: #externalUrl term: #term"() {
        when:
        println("url: $externalUrl term: $term")
        Properties props = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'test@test.com'])
        ExternalOntologyAPI extOntology = new BardExternalOntologyFactory().getExternalOntologyAPI(externalUrl, props)
        List<ExternalItem> externalItems = extOntology.findMatching(term)

        then:
        println("url: $externalUrl term: $term")
        println("count: ${externalItems.size()}")
        for (ExternalItem externalItem in externalItems) {
            println(externalItem.id)
            println(externalItem.display)
            println(externalItem.dump())
        }
        println("*******************************")
        externalItems

        where:
        externalUrl                                                      | term
//        "http://omim.org/entry/"                                         | "PROTEASOME 26S SUBUNIT, ATPase, 1;"            // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/biosystems/"                        | "9986"                                          // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/gene/"                              | "9986"                                          // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/mesh/"                              | "68020170"                                      // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/protein/"                           | "9966877"                                       // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/omim/"                              | "PROTEASOME 26S SUBUNIT, ATPase, 1;"            // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/pubmed/"                            | "Distinct 3-O-Sulfated Heparan Sulfate"         // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id="    | "Cellana capensis"                              // should work but currently broken
//        "http://www.ncbi.nlm.nih.gov/nuccore/"                           | "91199539"                                      // should work but currently broken
//        "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid="       | "9562061"                                       // should work but currently broken
        "http://www.uniprot.org/uniprot/"                                | "Q9Y6Q9"
        "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" | "apoptotic process"

    }

    void "test exception externalOntologyAPI.findMatching for url: #externalUrl term: '#term'"() {
        when:
        println("url: $externalUrl term: $term")
        Properties props = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'test@test.com'])
        ExternalOntologyAPI extOntology = new BardExternalOntologyFactory().getExternalOntologyAPI(externalUrl, props)
        List<ExternalItem> externalItems = extOntology.findMatching(term)

        then:
        def e = thrown(expectedException)
        println(e.message)

        where:
        externalUrl                                                      | term | expectedException
//        "http://www.ncbi.nlm.nih.gov/gene/"                              | null | NullPointerException
        "http://www.ncbi.nlm.nih.gov/gene/"                              | " "  | ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/gene/"                              | ""   | ExternalOntologyException
        "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" | null | ExternalOntologyException
        "http://www.uniprot.org/uniprot/"                                | null | RemoteDataAccessException
        "http://www.uniprot.org/uniprot/"                                | " "  | RemoteDataAccessException
        "http://www.uniprot.org/uniprot/"                                | ""   | RemoteDataAccessException

        "http://omim.org/entry/"                                         | "PROTEASOME 26S SUBUNIT, ATPase, 1;"      |   ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/biosystems/"                        | "9986"                                    |   ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/gene/"                              | "9986"                                    |   ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/mesh/"                              | "68020170"                                |   ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/protein/"                           | "9966877"                                 |   ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/omim/"                              | "PROTEASOME 26S SUBUNIT, ATPase, 1;"      |   ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/pubmed/"                            | "Distinct 3-O-Sulfated Heparan Sulfate"   |   ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id="    | "Cellana capensis"                        |   ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/nuccore/"                           | "91199539"                                |   ExternalOntologyException
        "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid="       | "9562061"                                 |   ExternalOntologyException
    }

    @Ignore
    void "test show NCBI Databases"() {
        when:
        EUtilsWeb web = new EUtilsWeb("BARD-CAP", "anonymous@bard.nih.gov");
        Set<String> databases = web.getDatabases()
        databases.each { println(it) }

        then:
        databases
    }
}