package bard.db.context.item

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/3/13
 * Time: 6:05 AM
 * To change this template use File | Settings | File Templates.
 */
import bard.db.registration.AssayContextItem;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/3/13
 * Time: 5:42 AM
 * To change this template use File | Settings | File Templates.
 */
class ContextItemDTO implements Comparable<ContextItemDTO> {


    final String contextName;
    final String label;
    final String valueDisplay;
    final Long contextItemId;

    public ContextItemDTO(final String contextName, final String label, final String valueDisplay, final Long contextItemId) {
        this.contextName = contextName;
        this.label = label;
        this.valueDisplay = valueDisplay;
        this.contextItemId = contextItemId;
    }

    public ContextItemDTO(AssayContextItem assayContextItem) {
        this.contextName = assayContextItem.assayContext.preferredName;
        this.label = assayContextItem.attributeElement.label;
        this.valueDisplay = assayContextItem.valueDisplay
        this.contextItemId = assayContextItem.id;
    }
    public static final SortedMap<String, List<ContextItemDTO>> buildCardMap(Collection<ContextItemDTO> contextItems) {
        Map<String, List<ContextItemDTO>> cardMap = [:]

        for (ContextItemDTO contextItemDTO : contextItems) {
            String contextName = contextItemDTO.contextName.toLowerCase()
            if (contextName) {
                List<ContextItemDTO> contextItemDTOs = cardMap.get(contextName)
                if (contextItemDTOs == null) {
                    contextItemDTOs = []
                }
                contextItemDTOs.add(contextItemDTO)
                cardMap.put(contextName, contextItemDTOs)
            }
        }
        return new TreeMap(cardMap)
    }
    public final static List<ContextItemDTO> toContextItemDTOs(List<AssayContextItem> assayContextItems){

        List<ContextItemDTO> dtos = []
        for(AssayContextItem assayContextItem: assayContextItems){
            dtos.add(new ContextItemDTO(assayContextItem));
        }
        return dtos.sort()
    }
    @Override
    public int compareTo(ContextItemDTO thatContextItemDTO) {
        return new CompareToBuilder()
                .append(this.contextName, thatContextItemDTO.contextName)
                .append(this.label, thatContextItemDTO.label)
                .append(this.valueDisplay, thatContextItemDTO.valueDisplay)
                .toComparison();
    }

    /**
     * Define equality of state.
     */
    @Override
    public boolean equals(Object aThat) {
        if (aThat == null) {
            return false;
        }
        if (this == aThat) {
            return true;
        }
        if (!(aThat instanceof ContextItemDTO)) return false;

        ContextItemDTO that = (ContextItemDTO) aThat;
        return new EqualsBuilder().
                append(this.contextName, that.contextName).
                append(this.label, that.label).
                append(this.valueDisplay, that.valueDisplay).
                isEquals();
    }

    /**
     * A class that overrides equals must also override hashCode.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(this.contextName).
                append(this.label).
                append(this.valueDisplay).
                toHashCode();
    }
}
