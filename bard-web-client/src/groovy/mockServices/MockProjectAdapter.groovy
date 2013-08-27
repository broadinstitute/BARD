package mockServices
import bard.core.Probe
import bard.core.interfaces.ProjectAdapterInterface
import bard.core.rest.spring.assays.BardAnnotation
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
    Integer numExperiments
    BardAnnotation annotations = null

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
    List<Probe> getProbes() {
        return [new Probe(2, "ML18", "http://bard.org", "CCC", 1, 2),
                new Probe(28, "ML20", "http://bard.org", "CCCC", 2,2)]  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Integer getNumberOfExperiments() {
        return numExperiments ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    BardAnnotation getAnnotations() {
        return annotations
    }

    @Override
    String getExperimentType(Long experimentId) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Map<Long, String> getExperimentTypes() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

}
