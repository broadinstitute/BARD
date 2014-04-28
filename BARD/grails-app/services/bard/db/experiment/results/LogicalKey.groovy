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

package bard.db.experiment.results;

import bard.db.dictionary.Element;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/29/13
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogicalKey {
    Integer replicateNumber;
    Long substanceId;

    Element resultType;
    Element statsModifier;
    Float valueNum;
    String qualifier;
    Float valueMin;
    Float valueMax;
    Element valueElement;
    String valueDisplay;
    LogicalKey parentKey;

    java.util.Set<LogicalKeyItem> items = new HashSet();

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        LogicalKey that = (LogicalKey) o

        if (items != that.items) return false
        if (parentKey != that.parentKey) return false
        if (qualifier != that.qualifier) return false
        if (replicateNumber != that.replicateNumber) return false
        if (resultType != that.resultType) return false
        if (statsModifier != that.statsModifier) return false
        if (substanceId != that.substanceId) return false
        if (valueDisplay != that.valueDisplay) return false
        if (valueElement != that.valueElement) return false
        if (valueMax != that.valueMax) return false
        if (valueMin != that.valueMin) return false
        if (valueNum != that.valueNum) return false

        return true
    }

    int hashCode() {
        int result
        result = (replicateNumber != null ? replicateNumber.hashCode() : 0)
        result = 31 * result + (parentKey != null ? parentKey.hashCode() : 0)
        result = 31 * result + (substanceId != null ? substanceId.hashCode() : 0)
        result = 31 * result + (resultType != null ? resultType.hashCode() : 0)
        result = 31 * result + (statsModifier != null ? statsModifier.hashCode() : 0)
        result = 31 * result + (valueNum != null ? valueNum.hashCode() : 0)
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0)
        result = 31 * result + (valueMin != null ? valueMin.hashCode() : 0)
        result = 31 * result + (valueMax != null ? valueMax.hashCode() : 0)
        result = 31 * result + (valueElement != null ? valueElement.hashCode() : 0)
        result = 31 * result + (valueDisplay != null ? valueDisplay.hashCode() : 0)
        result = 31 * result + (items != null ? items.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "{" +
                "replicateNumber=" + replicateNumber +
                ", substance=" + substanceId +
                ", resultType=" + (resultType == null ? null : resultType.getLabel()) +
                ", statsModifier=" + (statsModifier == null ? null :statsModifier.getLabel()) +
                ", valueNum=" + valueNum +
                ", qualifier='" + qualifier + '\'' +
                ", valueMin=" + valueMin +
                ", valueMax=" + valueMax +
                ", valueElement=" + valueElement +
                ", valueDisplay='" + valueDisplay + '\'' +
                ", items=" + items +
                ", parent=" + parentKey +
                '}';
    }

    public Integer getReplicateNumber() {
        return replicateNumber;
    }

    public void setReplicateNumber(Integer replicateNumber) {
        this.replicateNumber = replicateNumber;
    }

    public Long getSubstanceId() {
        return substanceId;
    }

    public void setSubstanceId(Long substanceId) {
        this.substanceId = substanceId;
    }

    public Element getResultType() {
        return resultType;
    }

    public void setResultType(Element resultType) {
        this.resultType = resultType;
    }

    public Element getStatsModifier() {
        return statsModifier;
    }

    public void setStatsModifier(Element statsModifier) {
        this.statsModifier = statsModifier;
    }

    public Float getValueNum() {
        return valueNum;
    }

    public void setValueNum(Float valueNum) {
        this.valueNum = valueNum;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public Float getValueMin() {
        return valueMin;
    }

    public void setValueMin(Float valueMin) {
        this.valueMin = valueMin;
    }

    public Float getValueMax() {
        return valueMax;
    }

    public void setValueMax(Float valueMax) {
        this.valueMax = valueMax;
    }

    public Element getValueElement() {
        return valueElement;
    }

    public void setValueElement(Element valueElement) {
        this.valueElement = valueElement;
    }

    public String getValueDisplay() {
        return valueDisplay;
    }

    public void setValueDisplay(String valueDisplay) {
        this.valueDisplay = valueDisplay;
    }

    public Set<LogicalKeyItem> getItems() {
        return items;
    }

    public void setItems(Set<LogicalKeyItem> items) {
        this.items = items;
    }


}
