package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.JsonUtil

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 1/9/13
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurveFitParameters extends JsonUtil {

    @JsonProperty("s0")
    private double s0;
    @JsonProperty("sInf")
    private double sInf;
    @JsonProperty("hillCoef")
    private double hillCoef;

    //TODO: this is dynamic, for now lets assume it is not
    //we should get the list of possible values
    @JsonProperty("logEc50")
    private double logEc50;

    @JsonProperty("s0")
    public double getS0() {
        return s0;
    }

    @JsonProperty("s0")
    public void setS0(double s0) {
        this.s0 = s0;
    }

    @JsonProperty("sInf")
    public double getSInf() {
        return sInf;
    }

    @JsonProperty("sInf")
    public void setSInf(double sInf) {
        this.sInf = sInf;
    }

    @JsonProperty("hillCoef")
    public double getHillCoef() {
        return hillCoef;
    }

    @JsonProperty("hillCoef")
    public void setHillCoef(double hillCoef) {
        this.hillCoef = hillCoef;
    }

    @JsonProperty("logEc50")
    public double getLogEc50() {
        return logEc50;
    }

    @JsonProperty("logEc50")
    public void setLogEc50(double logEc50) {
        this.logEc50 = logEc50;
    }
}
