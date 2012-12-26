package bard.core.rest.spring.compounds

import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.assays.Assay

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompoundSummary extends JsonUtil {
    @JsonProperty("ntest")
    private int ntest;

    @JsonProperty("testedExptdata")
    private List<Activity> testedExptdata = new ArrayList<Activity>();

    @JsonProperty("testedAssays")
    private List<Assay> testedAssays = new ArrayList<Assay>();


    @JsonProperty("nhit")
    private int nhit;

    @JsonProperty("hitAssays")
    private List<String> hitAssays = new ArrayList<String>();

    @JsonProperty("hitExptdata")
    private List<Activity> hitExptdata = new ArrayList<Activity>();



    @JsonProperty("testedExptdata")
    public List<Activity> getTestedExptdata() {
        return testedExptdata;
    }

    @JsonProperty("testedExptdata")
    public void setTestedExptdata(List<Activity> testedExptdata) {
        this.testedExptdata = testedExptdata;
    }

    @JsonProperty("testedAssays")
    public List<Assay> getTestedAssays() {
        return testedAssays;
    }

    @JsonProperty("testedAssays")
    public void setTestedAssays(List<Assay> testedAssays) {
        this.testedAssays = testedAssays;
    }

    @JsonProperty("nhit")
    public int getNhit() {
        return nhit;
    }

    @JsonProperty("nhit")
    public void setNhit(int nhit) {
        this.nhit = nhit;
    }

    @JsonProperty("hitAssays")
    public List<String> getHitAssays() {
        return hitAssays;
    }

    @JsonProperty("hitAssays")
    public void setHitAssays(List<String> hitAssays) {
        this.hitAssays = hitAssays;
    }

    @JsonProperty("ntest")
    public int getNtest() {
        return ntest;
    }

    @JsonProperty("ntest")
    public void setNtest(int ntest) {
        this.ntest = ntest;
    }

    @JsonProperty("hitExptdata")
    public List<Activity> getHitExptdata() {
        return hitExptdata;
    }

    @JsonProperty("hitExptdata")
    public void setHitExptdata(List<Activity> hitExptdata) {
        this.hitExptdata = hitExptdata;
    }

    CompoundBioActivity buildBioActivitySummary(){
        CompoundBioActivity compoundBioActivity = new CompoundBioActivity()
        compoundBioActivity.numberActive = this.nhit
        compoundBioActivity.numberTested=this.ntest



    }

}
