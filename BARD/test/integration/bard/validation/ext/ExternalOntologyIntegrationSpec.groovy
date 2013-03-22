package bard.validation.ext

import edu.scripps.fl.entrez.EUtilsWeb
import spock.lang.Specification
import spock.lang.Unroll
import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException

import static bard.validation.ext.BardExternalOntologyFactory.NCBI_EMAIL
import static bard.validation.ext.BardExternalOntologyFactory.NCBI_TOOL

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/1/13
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ExternalOntologyIntegrationSpec extends Specification {

    void "test valid urls new ConfigurableExternalOntologyFactory().getExternalOntologyAPI for #externalUrl"() {
        when:
        println("url: $externalUrl")
        Properties props = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'test@test.com'])
        ExternalOntologyAPI externalOntologyAPI = new BardExternalOntologyFactory().getExternalOntologyAPI(externalUrl, props)

        then:
        (externalOntologyAPI != null) == notNull


        where:
        externalUrl                                                                                    | notNull
        "http://amigo.geneontology.org/cgi-bin/amigo/gp-details.cgi?gp=FB:FBgn"                        | true
        "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term="                               | true

        "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid="                                     | true
        "http://omim.org/entry/"                                                                       | true
        "http://www.ncbi.nlm.nih.gov/biosystems/"                                                      | true
        "http://www.ncbi.nlm.nih.gov/gene/"                                                            | true
        "http://www.ncbi.nlm.nih.gov/mesh/"                                                            | true
        "http://www.ncbi.nlm.nih.gov/nuccore/"                                                         | true
        "http://www.ncbi.nlm.nih.gov/omim/"                                                            | true
        "http://www.ncbi.nlm.nih.gov/protein/"                                                         | true
        "http://www.ncbi.nlm.nih.gov/pubmed/"                                                          | true
        "http://www.ncbi.nlm.nih.gov/structure/?term="                                                 | true
        "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id="                                  | true

        "http://www.uniprot.org/uniprot/"                                                              | true

        "http://cas.org/"                                                                              | false
        "http://www.atcc.org/ATCCAdvancedCatalogSearch/ProductDetails/tabid/452/Default.aspx?ATCCNum=" | false
        "https://mli.nih.gov/mli/?dl_id="                                                              | false
        "http://regid.org/find"                                                                        | false

        "http://someUnknown.com"                                                                       | false
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
        externalUrl                                 | message
        "http://www.ncbi.nlm.nih.gov/gquery/?term=" | 'Unknown NCBI database gquery'
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
        "http://www.ncbi.nlm.nih.gov/gene/"                              | "9986"
        "http://www.ncbi.nlm.nih.gov/protein/"                           | "9966877"
        "http://www.ncbi.nlm.nih.gov/mesh/"                              | "68020170"
        "http://omim.org/entry/"                                         | "602706"
        "http://www.ncbi.nlm.nih.gov/omim/"                              | "602706"
        "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id="    | "9986"
        "http://www.uniprot.org/uniprot/"                                | "Q9Y6Q9"
        "http://www.ncbi.nlm.nih.gov/nuccore/"                           | "91199539"
        "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" | "GO:1901112"
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
        "http://www.ncbi.nlm.nih.gov/gene/"                              | " "             | IndexOutOfBoundsException
        "http://www.ncbi.nlm.nih.gov/gene/"                              | ""              | ExternalOntologyException
        "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" | null            | ExternalOntologyException
        "http://www.uniprot.org/uniprot/"                                | null            | ExternalOntologyException
        "http://www.uniprot.org/uniprot/"                                | " "             | RemoteDataAccessException
        "http://www.uniprot.org/uniprot/"                                | ""              | RemoteDataAccessException
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
        "http://omim.org/entry/"                                         | "PROTEASOME 26S SUBUNIT, ATPase, 1;"
        "http://www.ncbi.nlm.nih.gov/biosystems/"                        | "9986"
        "http://www.ncbi.nlm.nih.gov/gene/"                              | "9986"
        "http://www.ncbi.nlm.nih.gov/mesh/"                              | "68020170"
        "http://www.ncbi.nlm.nih.gov/protein/"                           | "9966877"
        "http://www.ncbi.nlm.nih.gov/omim/"                              | "PROTEASOME 26S SUBUNIT, ATPase, 1;"
        "http://www.ncbi.nlm.nih.gov/pubmed/"                            | "Distinct 3-O-Sulfated Heparan Sulfate"
        "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id="    | "Cellana capensis"
        "http://www.uniprot.org/uniprot/"                                | "Q9Y6Q9"
        "http://www.ncbi.nlm.nih.gov/nuccore/"                           | "91199539"
        "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid="       | "9562061"
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
        "http://www.ncbi.nlm.nih.gov/gene/"                              | null | NullPointerException
        "http://www.ncbi.nlm.nih.gov/gene/"                              | " "  | ExternalOntologyException
        "http://www.ncbi.nlm.nih.gov/gene/"                              | ""   | ExternalOntologyException
        "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=" | null | ExternalOntologyException
        "http://www.uniprot.org/uniprot/"                                | null | RemoteDataAccessException
        "http://www.uniprot.org/uniprot/"                                | " "  | RemoteDataAccessException
        "http://www.uniprot.org/uniprot/"                                | ""   | RemoteDataAccessException
    }

    void "test show NCBI Databases"() {
        when:
        EUtilsWeb web = new EUtilsWeb("BARD-CAP", "anonymous@bard.nih.gov");
        Set<String> databases = web.getDatabases()
        databases.each { println(it) }

        then:
        databases
    }
}