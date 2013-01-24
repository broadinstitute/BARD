package bard.core.rest.spring.compounds;


import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang.builder.EqualsBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromiscuityScaffold extends JsonUtil {
    @JsonProperty("wTested")
    private long testedWells;
    @JsonProperty("sActive")
    private long activeSubstances;
    @JsonProperty("wActive")
    private long activeWells;
    @JsonProperty("aTested")
    private long testedAssays;
    @JsonProperty("sTested")
    private long testedSubstances;
    @JsonProperty("pScore")
    private long promiscuityScore;
    @JsonProperty("scafid")
    private long scaffoldId;
    @JsonProperty("aActive")
    private long activeAssays;
    @JsonProperty("inDrug")
    private boolean inDrug;
    @JsonProperty("smiles")
    private String smiles;

    @JsonProperty("wTested")
    public long getTestedWells() {
        return testedWells;
    }

    @JsonProperty("wTested")
    public void setTestedWells(long wellsTested) {
        this.testedWells = wellsTested;
    }

    @JsonProperty("sActive")
    public long getActiveSubstances() {
        return activeSubstances;
    }

    @JsonProperty("sActive")
    public void setActiveSubstances(long sActive) {
        this.activeSubstances = sActive;
    }

    @JsonProperty("wActive")
    public long getActiveWells() {
        return activeWells;
    }

    @JsonProperty("wActive")
    public void setActiveWells(long wellsActive) {
        this.activeWells = wellsActive;
    }

    @JsonProperty("aTested")
    public long getTestedAssays() {
        return testedAssays;
    }

    @JsonProperty("aTested")
    public void setTestedAssays(long testedAssays) {
        this.testedAssays = testedAssays;
    }

    @JsonProperty("sTested")
    public long getTestedSubstances() {
        return testedSubstances;
    }

    @JsonProperty("sTested")
    public void setTestedSubstances(long substancesTested) {
        this.testedSubstances = substancesTested;
    }

    @JsonProperty("pScore")
    public long getPromiscuityScore() {
        return promiscuityScore;
    }

    @JsonProperty("pScore")
    public void setPromiscuityScore(long pScore) {
        this.promiscuityScore = pScore;
    }

    @JsonProperty("scafid")
    public long getScaffoldId() {
        return scaffoldId;
    }

    @JsonProperty("scafid")
    public void setScaffoldId(long scaffoldId) {
        this.scaffoldId = scaffoldId
    }

    @JsonProperty("aActive")
    public long getActiveAssays() {
        return activeAssays;
    }

    @JsonProperty("aActive")
    public void setActiveAssays(long aActive) {
        this.activeAssays = aActive;
    }

    @JsonProperty("inDrug")
    public boolean isInDrug() {
        return inDrug;
    }

    @JsonProperty("inDrug")
    public void setInDrug(boolean inDrug) {
        this.inDrug = inDrug;
    }

    @JsonProperty("smiles")
    public String getSmiles() {
        return smiles;
    }

    @JsonProperty("smiles")
    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    public WarningLevel getWarningLevel() {
        return WarningLevel.getWarningLevel(this.promiscuityScore)
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(this.promiscuityScore).
                append(this.testedSubstances).
                append(this.activeSubstances).
                append(this.testedAssays).
                append(this.activeAssays).
                append(this.testedWells).
                append(this.activeWells).
                append(this.smiles).
                append(this.scaffoldId).
                append(this.inDrug).
                toHashCode();
    }

    //Note: in Groovy, when compareTo() is implemented, .equals() would use compareTo to test for equality.
    int compareTo(PromiscuityScaffold thatScaffold) {
        return new CompareToBuilder().
                append(this.promiscuityScore, thatScaffold.promiscuityScore).
                append(this.testedSubstances, thatScaffold.testedSubstances).
                append(this.activeSubstances, thatScaffold.activeSubstances).
                append(this.testedAssays, thatScaffold.testedAssays).
                append(this.activeAssays, thatScaffold.activeAssays).
                append(this.testedWells, thatScaffold.testedWells).
                append(this.activeWells, thatScaffold.activeWells).
                append(this.smiles, thatScaffold.smiles).
                append(this.scaffoldId, thatScaffold.scaffoldId).
                append(this.inDrug, thatScaffold.inDrug).
                toComparison()
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != getClass()) {
            return false;
        }

        final PromiscuityScaffold thatScaffold = (PromiscuityScaffold) obj;
        return new EqualsBuilder().
                append(this.promiscuityScore, thatScaffold.promiscuityScore).
                append(this.testedSubstances, thatScaffold.testedSubstances).
                append(this.activeSubstances, thatScaffold.activeSubstances).
                append(this.testedAssays, thatScaffold.activeAssays).
                append(this.activeAssays, thatScaffold.activeAssays).
                append(this.testedWells, thatScaffold.testedWells).
                append(this.activeWells, thatScaffold.activeWells).
                append(this.smiles, thatScaffold.smiles).
                append(this.scaffoldId, thatScaffold.scaffoldId).
                append(this.inDrug, thatScaffold.inDrug).
                isEquals();
    }

}


