package bard.core.rest.spring.compounds

import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import bard.core.rest.spring.util.RingNode

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 3/29/13
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
class TargetClassInfo implements Serializable {

    /**
     * For {@link Serializable}.
     */
    private static final long serialVersionUID = -2741909316970609825L;


    String id
    String name
    String description
    String levelIdentifier
    String source
    String accessionNumber
    String path

    static TargetClassInfo generateClassInformation(List<String> data) {
        final TargetClassInfo targetClassInfo = new TargetClassInfo()
        targetClassInfo.id = data.get(0).trim()
        targetClassInfo.name = data.get(1).trim()
        targetClassInfo.description = data.get(2).trim()
        targetClassInfo.levelIdentifier = data.get(3).trim()
        targetClassInfo.source = data.get(4).trim()
        targetClassInfo.accessionNumber = data.get(5).trim()
        targetClassInfo.path = data.get(6).trim()
        return targetClassInfo
    }
    public TargetClassInfo(){

    }
    public TargetClassInfo(final RingNode ringNode) {
        this.id = ringNode.ID
        this.name = ringNode.name
        this.description = ringNode.description
        this.levelIdentifier = ringNode.levelIdentifier
        this.source = ringNode.source
        this.accessionNumber = accessionNumber
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append("id:${this.id}").append(" acc:${this.accessionNumber}").append(" name:${this.name}").append(" path:${this.path}")
        return stringBuilder.toString()
    }

    @Override
    boolean equals(Object obj) {
        if (obj == null) { return false }
        if (obj.is(this)) { return true }
        if (obj.getClass() != getClass()) { return false }

        TargetClassInfo rhs = (TargetClassInfo) obj

        return new EqualsBuilder()
                .append(this.accessionNumber, rhs.accessionNumber)
                .append(this.id, rhs.id)
                .append(this.name, rhs.name)
                .append(this.path, rhs.path)
                .isEquals()
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 13)
                .append(this.accessionNumber)
                .append(this.id)
                .append(this.name)
                .append(this.path)
                .toHashCode();
    }

}