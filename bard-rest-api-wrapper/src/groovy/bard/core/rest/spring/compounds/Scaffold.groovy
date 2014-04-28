/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
