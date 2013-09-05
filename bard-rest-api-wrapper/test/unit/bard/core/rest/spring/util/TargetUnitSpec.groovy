package bard.core.rest.spring.util

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TargetUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    public static final String TARGET_NODE = '''
   {
       "acc": "Q9Y253",
       "name": "DNA polymerase eta",
       "description": "describe",
       "status": "Reviewed",
       "geneId": 5429,
       "taxId": 9606,
       "url":"url",
       "resourcePath": "/targets/accession/Q9Y253"
    }
   '''
    String TARGET_WITH_CLASSIFICATIONS_JSON=
    '''
    {
        "acc": "O00482",
        "name": "Nuclear receptor subfamily 5 group A member 2",
        "description": null,
        "status": "Reviewed",
        "url": "http://www.uniprot.org/uniprot/O00482",
        "geneId": 2494,
        "taxId": 9606,
        "classes":
        [
            {
                "id": "PC00171",
                "name": "nucleic acid binding",
                "description": "A molecule that binds a nucleic acid.  It can be an enzyme or a binding protein.",
                "levelIdentifier": "1.09.00.00.00",
                "source": "panther"
            },
            {
                "id": "PC00197",
                "name": "receptor",
                "description": "A molecular structure within a cell or on the cell surface characterized by selective binding of a specific substance and a specific physiologic effect that accompanies the binding.",
                "levelIdentifier": "1.01.00.00.00",
                "source": "panther"
            },
            {
                "id": "PC00218",
                "name": "transcription factor",
                "description": "A protein required for the regulation of RNA polymerase by specific regulatory sequences in or near a gene.",
                "levelIdentifier": "1.08.00.00.00",
                "source": "panther"
            },
            {
                "id": "PC00169",
                "name": "nuclear hormone receptor",
                "description": "A receptor of steroid hormones that traverses the nuclear membrane to activate transcription.",
                "levelIdentifier": "1.08.06.00.00",
                "source": "panther"
            }
        ],
        "resourcePath": "/targets/accession/O00482"
    }
    '''
    void "test serialize json to Target"() {
        when:
        Target target = objectMapper.readValue(TARGET_NODE, Target.class)
        then:
        assert target
        assert target.acc=="Q9Y253"
        assert target.name=="DNA polymerase eta"
        assert target.description=="describe"
        assert target.status=="Reviewed"
        assert target.geneId==5429
        assert target.taxId==9606
        assert target.resourcePath=="/targets/accession/Q9Y253"
        assert target.url == "url"
        assert !target.getTargetClassifications()
    }
    void "test serialize json to Target with classification"() {
        when:
        Target target = objectMapper.readValue(TARGET_WITH_CLASSIFICATIONS_JSON, Target.class)
        then:
        assert target
        assert target.acc=="O00482"
        assert target.name=="Nuclear receptor subfamily 5 group A member 2"
        assert !target.description
        assert target.status=="Reviewed"
        assert target.geneId==2494
        assert target.taxId==9606
        assert target.resourcePath=="/targets/accession/O00482"
        assert target.url == "http://www.uniprot.org/uniprot/O00482"
        final List<TargetClassification> classifications = target.getTargetClassifications()
        assert classifications
        for(TargetClassification targetClassification:classifications){
            assert targetClassification.id
            assert targetClassification.levelIdentifier
            assert targetClassification.name
            assert targetClassification.description
            assert targetClassification.source
        }
    }
}

