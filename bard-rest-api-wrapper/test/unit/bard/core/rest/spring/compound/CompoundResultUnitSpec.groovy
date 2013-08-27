package bard.core.rest.spring.compound

import bard.core.rest.spring.compounds.Compound
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.compounds.CompoundResult

@Unroll
class CompoundResultUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

     @Shared String COMPOUNDS_FROM_EXPERIMENT_SEARCH='''
{
   "collection":[
      {
         "cid":6603008,
         "probeId":null,
         "url":null,
         "smiles":"Cl.CCOCCCNCC(=O)NC1=CC=C(OC(F)(F)F)C=C1",
         "name":"2-(3-ethoxypropylamino)-N-[4-(trifluoromethoxy)phenyl]acetamide;hydrochloride",
         "iupacName":"2-(3-ethoxypropylamino)-N-[4-(trifluoromethoxy)phenyl]acetamide;hydrochloride",
         "mwt":356.768,
         "tpsa":59.6,
         "exactMass":356.111,
         "xlogp":null,
         "complexity":319,
         "rotatable":9,
         "hbondAcceptor":7,
         "hbondDonor":3,
         "compoundClass":"Unassigned",
         "numAssay":648,
         "numActiveAssay":3,
         "highlight":null,
         "resourcePath":"/compounds/6603008"
      },
      {
         "cid":6602571,
         "probeId":null,
         "url":null,
         "smiles":"Cl.COCCN1N=NN=C1CN2CCC(CC3=CC=CC=C3)CC2",
         "name":"4-benzyl-1-[[1-(2-methoxyethyl)tetrazol-5-yl]methyl]piperidine;hydrochloride",
         "iupacName":"4-benzyl-1-[[1-(2-methoxyethyl)tetrazol-5-yl]methyl]piperidine;hydrochloride",
         "mwt":351.874,
         "tpsa":56.1,
         "exactMass":351.183,
         "xlogp":null,
         "complexity":329,
         "rotatable":7,
         "hbondAcceptor":5,
         "hbondDonor":1,
         "compoundClass":"Unassigned",
         "numAssay":684,
         "numActiveAssay":3,
         "highlight":null,
         "resourcePath":"/compounds/6602571"
      }
      ]
    }
    '''
    @Shared
    String COMPOUNDS_FROM_FREE_TEXT_SEARCH = '''
    {
   "docs":[
      {
         "cid":"2722",
         "iso_smiles":"C1=CC2=C(C(=C(C=C2Cl)Cl)O)N=C1",
         "iupac_name":"5,7-dichloroquinolin-8-ol",
         "preferred_term":"Chloroxine",
         "compound_class":"Drug",
         "highlight":null
      },
      {
         "cid":"16760208",
         "iso_smiles":"C1=NC2=C(N1[C@H]3C([C@H]([C@H](O3)CO)O)F)N=C(N=C2N)Cl",
         "iupac_name":"(2R,3S,5R)-5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol",
         "preferred_term":"CAFdA",
         "compound_class":"Drug",
         "highlight":null
      },
      {
         "cid":"354624",
         "iso_smiles":"C1=NC2=C(N1C3C(C(C(O3)CO)O)F)N=C(N=C2N)Cl",
         "iupac_name":"5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol",
         "preferred_term":"clofarabine",
         "compound_class":"Drug",
         "highlight":null
      },
      {
         "cid":"5394",
         "iso_smiles":"CN1C(=O)N2C=NC(=C2N=N1)C(=O)N",
         "iupac_name":"3-methyl-4-oxoimidazo[5,1-d][1,2,3,5]tetrazine-8-carboxamide",
         "preferred_term":"Temozolomide",
         "compound_class":"Drug",
         "highlight":null
      },
      {
         "cid":"38911",
         "iso_smiles":"CC1=CC(=O)N(C(=C1)C2CCCCC2)O.C(CO)N",
         "iupac_name":"2-aminoethanol; 6-cyclohexyl-1-hydroxy-4-methylpyridin-2-one",
         "preferred_term":"Ciclopirox Olamine",
         "compound_class":"Drug",
         "highlight":null
      },
      {
         "cid":"3899",
         "iso_smiles":"CC1=C(C=NO1)C(=O)NC2=CC=C(C=C2)C(F)(F)F",
         "iupac_name":"5-methyl-N-[4-(trifluoromethyl)phenyl]-1,2-oxazole-4-carboxamide",
         "preferred_term":"Leflunomide",
         "compound_class":"Drug",
         "highlight":null
      }
   ],
   "metaData":{
      "nhit":6,
      "facets":[
         {
            "facetName":"compound_class",
            "counts":{
               "Drug":6
            }
         },
         {
            "facetName":"COLLECTION",
            "counts":{
               "FDA drugs@FDA":6,
               "KEGG":3
            }
         },
         {
            "facetName":"mw",
            "counts":{
               "[* TO 100]":0,
               "[100 TO 200]":1,
               "[200 TO 300]":3,
               "[300 TO *]":2
            }
         },
         {
            "facetName":"tpsa",
            "counts":{
               "[* TO 40]":1,
               "[40 TO 120]":5,
               "[120 TO 180]":0,
               "[180 TO *]":0
            }
         },
         {
            "facetName":"xlogp",
            "counts":{
               "[* TO 1]":0,
               "[1 TO 3]":1,
               "[3 TO 5]":0,
               "[5 TO *]":0
            }
         }
      ],
      "queryTime":29,
      "elapsedTime":35,
      "matchingFields":null,
      "scores":null
   },
   "etag":"2f23924ecf17e1a3",
   "link":null
    }
    '''
    /**
     *
     * http://bard.nih.gov/api/v10/search/compounds?q="dna repair" & expand=true
     *
     */
    void "test serialization to Compounds from free text search"() {
        when:
        final CompoundResult compoundResult = objectMapper.readValue(COMPOUNDS_FROM_FREE_TEXT_SEARCH, CompoundResult.class)

        then:
        assert compoundResult
        List<Compound> compounds = compoundResult.compounds
        assert compounds.size() == 6
        assert compoundResult.numberOfHits == compounds.size()
        assert compoundResult.getCompds().size() == compounds.size()
        for(Compound compound : compounds){
            assert compound.id
        }

    }
    /**
     * URL of example search would look like:
     * http://bard.nih.gov/api/v10/experiments/1048/compounds?expand=true
     */
    void "test serialization to Compounds from experiment search"() {
        when:
        final CompoundResult compoundResult = objectMapper.readValue(COMPOUNDS_FROM_EXPERIMENT_SEARCH, CompoundResult.class)

        then:
        assert compoundResult
        List<Compound> compounds = compoundResult.compounds
        assert compounds.size() == 2
        for(Compound compound : compounds){
            assert compound.id
        }

    }

}

