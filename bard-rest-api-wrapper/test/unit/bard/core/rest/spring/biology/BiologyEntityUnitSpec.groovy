package bard.core.rest.spring.biology

import spock.lang.Unroll
import spock.lang.Specification
import spock.lang.Shared
import com.fasterxml.jackson.databind.ObjectMapper
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.Readout

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 5/17/13
 * Time: 7:54 AM
 * To change this template use File | Settings | File Templates.
 */
    @Unroll
    class BiologyEntityUnitSpec extends Specification {
        @Shared
        ObjectMapper objectMapper = new ObjectMapper()

        final String BIOLOGY_DATA_JSON = '''
[
    {
        "biology": "PROTEIN",
        "entityId": 43,
        "name": "Scavenger receptor class B member 1",
        "entity": "assay",
        "extId": "",
        "extRef": null,
        "dictLabel": "UniProt accession number",
        "dictId": 1398,
        "serial": 232,
        "updated": null,
        "resourcePath": "/biology/232"
    },
    {
        "biology": "PROCESS",
        "entityId": 44,
        "name": "cholesterol import",
        "entity": "assay",
        "extId": "GO:0070508",
        "extRef": null,
        "dictLabel": "GO biological process term",
        "dictId": 1419,
        "serial": 233,
        "updated": null,
        "resourcePath": "/biology/233"
    }
]'''

        void setup() {

        }

        void tearDown() {
            // Tear down logic here
        }

        void "test experiment data"() {
            when:
            List<BiologyEntity> biologyEntityList = objectMapper.readValue(BIOLOGY_DATA_JSON, BiologyEntity[].class)
            BiologyEntity biologyEntity0 = biologyEntityList.get(0);
            BiologyEntity biologyEntity1 = biologyEntityList.get(1);

            then:
            biologyEntityList
            biologyEntityList.size () == 2
            biologyEntity0
            biologyEntity1

             biologyEntity0.biology  == "PROTEIN"
             biologyEntity0.entityId == 43
             biologyEntity0.name == "Scavenger receptor class B member 1"
             biologyEntity0.entity == "assay"
             biologyEntity0.extId==""
             biologyEntity0.extRef== null
             biologyEntity0.dictLabel=="UniProt accession number"
             biologyEntity0.dictId== 1398
             biologyEntity0.serial== 232
             biologyEntity0.updated== null
             biologyEntity0.resourcePath== "/biology/232"


            biologyEntity1.biology  == "PROCESS"
            biologyEntity1.entityId == 44
            biologyEntity1.name == "cholesterol import"
            biologyEntity1.entity == "assay"
            biologyEntity1.extId=="GO:0070508"
            biologyEntity1.extRef== null
            biologyEntity1.dictLabel=="GO biological process term"
            biologyEntity1.dictId== 1419
            biologyEntity1.serial== 233
            biologyEntity1.updated== null
            biologyEntity1.resourcePath== "/biology/233"

        }

    }



