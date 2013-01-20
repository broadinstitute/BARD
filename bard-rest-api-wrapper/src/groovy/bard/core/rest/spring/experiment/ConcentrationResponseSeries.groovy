package bard.core.rest.spring.experiment

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import bard.core.rest.spring.util.DictionaryElement
import bard.rest.api.wrapper.Dummy

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConcentrationResponseSeries extends JsonUtil {

   @JsonIgnore
   Dummy dummy = new Dummy()
//    def ctx = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
//    @JsonIgnore
//    def dataExportRestService = ctx.dataExportRestService



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


    public String getDictionaryLabel() {
        if (dictElemId) {

            final DictionaryElement dictionaryElement = dummy.dataExportRestService.findDictionaryElementById(this.dictElemId)
            String label = null
            if(dictionaryElement){
              label = dictionaryElement.label
            }
            if (label) {
                return label
            }
        }
        return responseUnit
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

    public static Map toDoseResponsePoints(List<ConcentrationResponsePoint> concentrationResponsePoints) {
        List<Double> concentrations = []
        List<Double> activities = []

        for (ConcentrationResponsePoint concentrationResponsePoint : concentrationResponsePoints) {
            concentrations.add(concentrationResponsePoint.testConcentration)
            activities.add(new Double(concentrationResponsePoint.value))
        }
        return [activities: activities, concentrations: concentrations]

    }
}
