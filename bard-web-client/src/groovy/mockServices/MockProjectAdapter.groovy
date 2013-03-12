package mockServices

import bard.core.interfaces.ProjectAdapterInterface
import bard.core.Value
import bard.core.Probe
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.interfaces.EntityNamedSources

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 3/10/13
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
class MockProjectAdapter implements ProjectAdapterInterface {
    Long id
    String name
    String description
    String grantNumber
    String labName
    Integer numExperiments
    Collection<Value> annotations = []

    @Override
    String getHighlight() {
        return "Score: 2.0, Matched Field: Name"
    }

    @Override
    Long getId() {
        return id ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getName() {
        return name ?: "Project Name" //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getDescription() {
        return description ?: '''
                    Assay Provider: David M. Wilson, III, National Institute on Aging, NIH Screening Center PI: Austin, C.P. Screening Center: NIH Chemical Genomics Center [NCGC]
                    The apurinic/apyrimidinic endonuclease APE1 is the primary mammalian enzyme responsible for the removal of abasic (or AP) sites in DNA and functions centrally
                    in the base excision DNA repair (BER) pathway. Recent studies suggested a link between an overexpression of APE1 in many cancers and resistance of these tumor
                    cells to radio- and chemotherapy. Thus, targeting APE1 could improve the efficacy of current treatment paradigms by promoting selective sensitization or
                    protection of diseased and normal cells, respectively. This assay will summarize the probe development efforts that are currently ongoing.
                 '''
    }

    @Override
    String getGrantNumber() {
        return grantNumber ?: "GI2"  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getLaboratoryName() {
        return labName ?: "Broad" //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List<Probe> getProbes() {
        return [new Probe(2, "ML18", "http://bard.org", "CCC", 1, 2),
                new Probe(28, "ML20", "http://bard.org", "CCCC", 2,2)]  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Integer getNumberOfExperiments() {
        return numExperiments ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List<BardAnnotation> getAnnotations() {
        if (!annotations) {
            this.annotations = new ArrayList<BardAnnotation>();
//            final Map<String, String> terms = getDictionaryTerms()
//            for (String key : terms.keySet()) {
//                Value value = new bard.core.StringValue(DataSource.DEFAULT, key, terms.get(key))
//                this.annotations.add(value)
//            }
        }
        return annotations//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getExperimentType(Long experimentId) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Map<Long, String> getExperimentTypes() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Map<String, String> getDictionaryTerms() {
        return ["grant number": "X01 MH083262-01", "laboratory name": "NCGC", "protein": "gi|92096784|gb|AAI14949.1|Microtubule-associated protein tau [Homo sapiens]"]
    }

    @Override
    Map<String, List<String>> getKeggAnnotations() {
        Map<String, List<String>> annos = new HashMap<String, List<String>>()
        annos.put(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource, ["Amyotrophic lateral sclerosis (ALS)", "Lou Gehrig's disease", "Progressive supranuclear palsy (PSP)"])
        annos.put(EntityNamedSources.KEGGDiseaseNameAnnotationSource, ["Neurodegenerative disease", "Neurodegenerative disease", "Neurodegenerative disease"])
        return annos;

    }
}
