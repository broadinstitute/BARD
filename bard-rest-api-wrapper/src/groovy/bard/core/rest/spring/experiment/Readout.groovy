package bard.core.rest.spring.experiment

import bard.core.DataSource
import bard.core.HillCurveValue
import bard.core.Value
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Readout extends  JsonUtil{

    @JsonProperty("name")
    private String name;
    @JsonProperty("s0")
    private Double s0;
    @JsonProperty("sInf")
    private Double sInf;
    @JsonProperty("hill")
    private Double coef;
    @JsonProperty("ac50")
    private Double slope;
    @JsonProperty("cr")
    private List<List<Double>> cr = new ArrayList<List<Double>>();
    @JsonProperty("npoint")
    private Long numberOfPoints;
    @JsonProperty("concUnit")
    private String concentrationUnits;
    @JsonProperty("responseUnit")
    private String responseUnit;
    // data
    protected List<Double> conc = new ArrayList<Double>();
    protected List<Double> response = new ArrayList<Double>();

    public void addPoint(Double conc, Double response) {
        this.conc.add(conc);
        this.response.add(response);
    }

    public int size() { return conc.size(); }

    public Double[] getConc() {
        return conc.toArray(new Double[0]);
    }

    public List<Double> getConcAsList() {
        return conc
    }

    public List<Double> getResponseAsList() {
        return response;
    }

    public Double[] getResponse() {
        return response.toArray(new Double[0]);
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("s0")
    public Double getS0() {
        return s0;
    }

    @JsonProperty("s0")
    public void setS0(Double s0) {
        this.s0 = s0;
    }

    @JsonProperty("sInf")
    public Double getSInf() {
        return sInf;
    }

    @JsonProperty("sInf")
    public void setSInf(Double sInf) {
        this.sInf = sInf;
    }

    @JsonProperty("hill")
    public Double getCoef() {
        return coef;
    }

    @JsonProperty("hill")
    public void setCoef(Double coef) {
        this.coef = coef;
    }

    @JsonProperty("ac50")
    public Double getSlope() {
        return slope;
    }

    @JsonProperty("ac50")
    public void setAc50(Double slope) {
        this.slope = slope;
    }

    @JsonProperty("cr")
    public List<List<Double>> getCr() {
        return cr;
    }

    @JsonProperty("cr")
    public void setCr(List<List<Double>> cr) {
        addCrcs(cr)
        this.cr = cr;
    }
    //We extract the x and y coordinates here
    public void addCrcs(List<List<Double>> cr) {
        for (List<Double> point : cr) {
            //first value is concentration
            //second value is activity
            if (!point.isEmpty()) {
                this.conc.add(point.get(0))
                if (point.size() == 2) {
                    this.response.add(point.get(1))
                } else {
                    this.response.add(0)
                }
            }
        }
        this.cr = cr;
    }

    @JsonProperty("npoint")
    public Long getNumberOfPoints() {
        return this.numberOfPoints;
    }

    @JsonProperty("npoint")
    public void setNumberOfPoints(Long numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    @JsonProperty("concUnit")
    public String getConcentrationUnits() {
        return concentrationUnits;
    }

    @JsonProperty("concUnit")
    public void setConcentrationUnits(String concentrationUnits) {
        this.concentrationUnits = concentrationUnits;
    }

    @JsonProperty("responseUnit")
    public String getResponseUnit() {
        return responseUnit;
    }

    @JsonProperty("responseUnit")
    public void setResponseUnit(String responseUnit) {
        this.responseUnit = responseUnit;
    }

    HillCurveValue toHillCurveValue() {
        final Value parent = new Value(DataSource.DEFAULT)
        HillCurveValue hillCurveValue = new HillCurveValue(parent, this.name)
        hillCurveValue.coef = this.coef
        hillCurveValue.slope = this.slope
        hillCurveValue.conc = this.conc?:[]
        hillCurveValue.response = this.response?:[]
        hillCurveValue.s0 = this.s0
        hillCurveValue.sinf = this.sInf
        hillCurveValue.concentrationUnits = this.concentrationUnits
        return hillCurveValue
    }
}

