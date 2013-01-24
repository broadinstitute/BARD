package bard.core.rest.spring.compounds

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.CompareToBuilder

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 9/19/12
 * Time: 8:18 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "hscaf")
@Deprecated
//Use PromiscuityScaffold Instead
public class Scaffold implements Comparable<Scaffold> {


    Long scafid
    Double pScore
    String scafsmi
    Long sTested
    Long sActive
    Long aTested
    Long aActive
    Long wTested
    Long wActive
    Boolean inDrug



    public WarningLevel getWarningLevel() {
        return WarningLevel.getWarningLevel(pScore)
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(this.pScore).
                append(this.sTested).
                append(this.sActive).
                append(this.aTested).
                append(this.aActive).
                append(this.wTested).
                append(this.wActive).
                append(this.scafsmi).
                append(this.scafid).
                append(this.inDrug).
                toHashCode();
    }

    //Note: in Groovy, when compareTo() is implemented, .equals() would use compareTo to test for equality.
    int compareTo(Scaffold thatScaffold) {
        return new CompareToBuilder().
                append(this.pScore, thatScaffold.pScore).
                append(this.sTested, thatScaffold.sTested).
                append(this.sActive, thatScaffold.sActive).
                append(this.aTested, thatScaffold.aTested).
                append(this.aActive, thatScaffold.aActive).
                append(this.wTested, thatScaffold.wTested).
                append(this.wActive, thatScaffold.wActive).
                append(this.scafsmi, thatScaffold.scafsmi).
                append(this.scafid, thatScaffold.scafid).
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

        final Scaffold thatScaffold = (Scaffold) obj;
        return new EqualsBuilder().
                append(this.pScore, thatScaffold.pScore).
                append(this.sTested, thatScaffold.sTested).
                append(this.sActive, thatScaffold.sActive).
                append(this.aTested, thatScaffold.aTested).
                append(this.aActive, thatScaffold.aActive).
                append(this.wTested, thatScaffold.wTested).
                append(this.wActive, thatScaffold.wActive).
                append(this.scafsmi, thatScaffold.scafsmi).
                append(this.scafid, thatScaffold.scafid).
                append(this.inDrug, thatScaffold.inDrug).
                isEquals();
    }

}

/**
 * Warning Levels
 */
public enum WarningLevel {
    none(0, 100),
    moderate(100, 300),
    severe(300, Double.MAX_VALUE)

    WarningLevel(Double minThreshold, Double maxThreshold) {
        this.minThreshold = minThreshold
        this.maxThreshold = maxThreshold
    }

    String getDescription() {
        if (this.maxThreshold != Double.MAX_VALUE) {
            return this.name() + " (between " + this.minThreshold + " and " + this.maxThreshold + ")"
        }
        return this.name() + " (> " + this.minThreshold + ")"

    }

    static WarningLevel getWarningLevel(Double pScore) {
        WarningLevel.values().find {
            if (pScore >= it.minThreshold && pScore < it.maxThreshold) {
                return it
            }
        }
    }

    Double minThreshold
    Double maxThreshold
}
