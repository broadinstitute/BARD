package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.JsonUtil


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConcentrationResponseSeries extends JsonUtil {


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


    public String getYAxisLabel(){
        String label = null
        if(dictElemId){
            label = CR_DICT_ID_TO_TERMS.get(dictElemId.intValue())
        }
        if(!label){
            label = responseUnit
        }
        return label
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

    public final static Map<Integer, String> CR_DICT_ID_TO_TERMS = [
            982: "percent response",
            986: "percent activity",
            991: "percent G2-M arrested",
            992: "percent viability",
            994: "percent bound",
            996: "percent activation",
            998: "percent inhibition",
            1004: "percent augmentation",
            1005: "percent count",
            1006: "percent current ",
            1007: "percent displacement ",
            1008: "percent efficacy ",
            1009: "percent efflux ",
            1010: "percent emax bottom ",
            1011: "percent emax top ",
            1012: "percent of control ",
            1013: "percent survival ",
            1014: "percent toxicity ",
            1016: "percent effect",
            1322: "percent cellular ATP content",
            1324: "percent side scatter shift",
            1330: "percent capsule formation",
            1340: "blank percent cv",
            1359: "percent area",
            1361: "percent induction",
            1415: "percent G1 arrested",
            1416: "percent S arrested",
            1417: "percent subG1 arrested",
            1448: "control percent cv",
            1483: "percent intensity",
            1484: "percent cells",
            1485: "percent increase",
            1486: "percent remaining",
            1487: "percent unblocked"
    ]
}
