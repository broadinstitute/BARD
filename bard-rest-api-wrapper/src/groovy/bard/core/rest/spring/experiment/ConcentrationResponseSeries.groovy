package bard.core.rest.spring.experiment

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import bard.core.rest.spring.util.DictionaryElement
import bard.rest.api.wrapper.Dummy
import org.apache.log4j.Logger
import org.apache.log4j.Level

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConcentrationResponseSeries extends JsonUtil {

    @JsonIgnore
    Dummy dummy = new Dummy()
    @JsonIgnore
    static Logger log
    static {
        this.log = Logger.getLogger(ConcentrationResponseSeries.class)
        log.setLevel(Level.INFO)
    }


    @JsonProperty("responseUnit")
    private String responseUnit;
    @JsonProperty("testConcUnit")
    private String testConcentrationUnit;
    @JsonProperty("crSeriesDictId")
    private long dictElemId;
    @JsonProperty("concRespParams")
    private CurveFitParameters curveFitParameters;
    @JsonProperty("concRespPoints")
    private List<ConcentrationResponsePoint> concentrationResponsePoints = new ArrayList<ConcentrationResponsePoint>();
    @JsonProperty("miscData")
    private List<ActivityData> miscData = new ArrayList<ActivityData>();

    public boolean hasChildElements() {
        for (ConcentrationResponsePoint concentrationResponsePoint : concentrationResponsePoints) {
            if (concentrationResponsePoint.childElements) {
                return true
            }
        }
        return false
    }

    public String getDictionaryLabel() {
        if (dictElemId) {

            final DictionaryElement dictionaryElement = dummy.dataExportRestService.findDictionaryElementById(this.dictElemId)
            if (dictionaryElement) {
                return dictionaryElement.label
            }
        }
        return ""
    }

    public String getDictionaryDescription() {
        if (dictElemId) {
            final DictionaryElement dictionaryElement = dummy.dataExportRestService.findDictionaryElementById(this.dictElemId)
            if (dictionaryElement) {
                return dictionaryElement.description
            }
        }
        return ""
    }

    public String getYAxisLabel() {

        return getDictionaryLabel()
    }

    @JsonProperty("responseUnit")
    public String getResponseUnit() {
        return responseUnit;
    }

    @JsonProperty("responseUnit")
    public void setResponseUnit(String responseUnit) {
        this.responseUnit = responseUnit;
    }

    @JsonProperty("testConcUnit")
    public String getTestConcentrationUnit() {
        return testConcentrationUnit;
    }

    @JsonProperty("testConcUnit")
    public void setTestConcentrationUnit(String testConcUnit) {
        this.testConcentrationUnit = testConcUnit;
    }

    @JsonProperty("crSeriesDictId")
    public long getDictElemId() {
        return dictElemId;
    }

    @JsonProperty("crSeriesDictId")
    public void setDictElemId(long dictElemId) {
        this.dictElemId = dictElemId;
    }

    @JsonProperty("concRespParams")
    public CurveFitParameters getCurveFitParameters() {
        return curveFitParameters;
    }

    @JsonProperty("concRespParams")
    public void setCurveFitParameters(CurveFitParameters curveFitParameters) {
        this.curveFitParameters = curveFitParameters;
    }

    @JsonProperty("concRespPoints")
    public List<ConcentrationResponsePoint> getConcentrationResponsePoints() {
        return concentrationResponsePoints;
    }

    @JsonProperty("concRespPoints")
    public void setConcentrationResponsePoints(List<ConcentrationResponsePoint> concentrationResponsePoints) {
        this.concentrationResponsePoints = concentrationResponsePoints;
    }

    @JsonProperty("miscData")
    public List<ActivityData> getMiscData() {
        return miscData;
    }

    @JsonProperty("miscData")
    public void setMiscData(List<ActivityData> miscData) {
        this.miscData = miscData;
    }

    public List<Double> sorterdActivities() {
        List<Double> activities = []

        for (ConcentrationResponsePoint concentrationResponsePoint : this.concentrationResponsePoints) {
            if (concentrationResponsePoint.value != null) {
                if (concentrationResponsePoint.value.isNumber()) {
                    activities.add(new Double(concentrationResponsePoint.value))
                }
            }
        }
        return activities.sort()
    }

    public static ActivityConcentrationMap toDoseResponsePoints(List<ConcentrationResponsePoint> concentrationResponsePoints) {
        List<Double> concentrations = []
        List<Double> activities = []

        for (ConcentrationResponsePoint concentrationResponsePoint : concentrationResponsePoints) {
            if (concentrationResponsePoint.testConcentration != null && concentrationResponsePoint.value != null) {
                if (concentrationResponsePoint.value.isNumber() && !concentrationResponsePoint.testConcentration.isNaN()) {
                    concentrations.add(concentrationResponsePoint.testConcentration)
                    activities.add(new Double(concentrationResponsePoint.value))
                }

            }
            else {
                log.warn("Concentration point/value can not be empty: '${concentrationResponsePoint.testConcentration}/${concentrationResponsePoint.value}'")
            }
        }

        ActivityConcentrationMap activityConcentrationMap = new ActivityConcentrationMap(activities: activities, concentrations: concentrations)
        //return [activities: activities, concentrations: concentrations]
        return activityConcentrationMap
    }
}
public class ActivityConcentrationMap {
    List<Double> activities
    List<Double> concentrations
}
