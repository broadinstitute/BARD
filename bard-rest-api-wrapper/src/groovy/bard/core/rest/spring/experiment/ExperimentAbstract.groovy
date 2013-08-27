package bard.core.rest.spring.experiment
import bard.core.interfaces.ExperimentRole
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/26/12
 * Time: 8:33 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ExperimentAbstract extends JsonUtil {

    long bardExptId;
    long capExptId

    long bardAssayId
    long capAssayId

    long pubchemAid;
    long category;
    long type;
    long summary;
    long classification;

    long substances;
    long compounds;
    long activeCompounds;

    long confidenceLevel
    String name;
    String description;
    String source;
    String grantNo;
    String deposited;
    String updated;
    boolean hasProbe;
    List<Long> projectIdList = new ArrayList<Long>();
    String resourcePath;

    public ExperimentRole getRole() {
        return ExperimentRole.valueOf(this.getClassification().intValue())
    }

    public int getProjectCount() {
        return projectIdList.size()
    }

    public static Map<Long,Set<ExperimentAbstract>> groupByProjectIds(List<ExperimentAbstract> experiments) {
        Map<Long,Set<ExperimentAbstract>> grouping = [:]
        experiments.each { ExperimentAbstract experiment ->
            experiment.projectIdList.each { Long id ->
                if (!grouping.containsKey(id)) {
                    grouping[id] = []
                }
                grouping[id] << experiment
            }
        }
        return grouping
    }
}
