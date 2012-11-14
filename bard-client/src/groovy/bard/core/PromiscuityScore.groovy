package bard.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.CompareToBuilder

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 9/19/12
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class PromiscuityScore implements Comparable<PromiscuityScore> {
    Long cid
    List<Scaffold> scaffolds

    public String toString(){
        List<String> list = []
        list.add("CID: ${cid}")
        for(Scaffold scaffold : scaffolds){
            list.add("${scaffold.scafsmi}")
        }
        return list.join("\n").toString()
    }
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(this.cid).
                append(this.scaffolds).
                toHashCode();
    }

    //Note: in Groovy, when compareTo() is implemented, .equals() would use compareTo to test for equality.
    int compareTo(PromiscuityScore thatPromiscuityScore) {
        return new CompareToBuilder()
                .append(this.cid, thatPromiscuityScore.cid)
                .append(this.scaffolds, thatPromiscuityScore.scaffolds)
                .toComparison()
    }

    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if (obj.getClass() != getClass()){
            return false;
        }

        PromiscuityScore rhs = (PromiscuityScore) obj;
        return new EqualsBuilder().
                append(this.cid, rhs.cid).
                append(this.scaffolds, rhs.scaffolds).
                isEquals();
    }
}

