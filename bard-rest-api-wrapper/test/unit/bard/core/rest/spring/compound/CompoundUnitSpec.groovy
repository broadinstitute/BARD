package bard.core.rest.spring.compound

import bard.core.rest.spring.compounds.Compound
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CompoundUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String COMPOUND = '''
        {
           "cid": "3899",
           "iso_smiles": "CC1=C(C=NO1)C(=O)NC2=CC=C(C=C2)C(F)(F)F",
           "iupac_name": "5-methyl-N-[4-(trifluoromethyl)phenyl]-1,2-oxazole-4-carboxamide",
           "preferred_term": "Leflunomide",
           "compound_class": "Drug",
           "highlight": null
       }
       '''

    void "test serialization to Compound"() {
        when:
        final Compound compound = objectMapper.readValue(COMPOUND, Compound.class)
        compound.setEtag("etag")
        compound.setAnno_key([])
        compound.setAnno_val([])
        then:
        assert compound.getCid() == 3899

        assert compound.getSmiles() == "CC1=C(C=NO1)C(=O)NC2=CC=C(C=C2)C(F)(F)F"
        assert compound.getIupacName() == "5-methyl-N-[4-(trifluoromethyl)phenyl]-1,2-oxazole-4-carboxamide"
        assert compound.getName() == "Leflunomide"
        assert compound.getFreeTextName() =="Leflunomide"
        assert compound.getFreeTextCompoundClass() == compound.getCompoundClass()
        assert compound.getFreeTextIupacName() == compound.getIupacName()
        assert compound.getFreeTextName() == compound.name
        assert compound.getFreeTextSmiles() == compound.smiles
        assert compound.getCompoundClass() == "Drug"
        assert !compound.getHighlight()
        assert !compound.getComplexity()
        assert compound.getId()==3899
        assert compound.getEtag() == "etag"
        assert !compound.getResourcePath()
        assert !compound.getAnno_key()
        assert !compound.getAnno_val()
    }


}

