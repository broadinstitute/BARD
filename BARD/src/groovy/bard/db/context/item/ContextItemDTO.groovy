package bard.db.context.item

import bard.db.registration.AssayContext

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/3/13
 * Time: 6:05 AM
 * To change this template use File | Settings | File Templates.
 */
import bard.db.registration.AssayContextItem
import org.apache.commons.collections.CollectionUtils;
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
    final String label;
    final String valueDisplay;
    final Long contextItemId;
    final ContextDTO contextDTO
    boolean isExclusiveToCard = false

    ContextItemDTO(final String contextName, final String label, final String valueDisplay, final Long contextItemId, final Long contextId) {
        this.label = label;
        this.valueDisplay = valueDisplay;
        this.contextItemId = contextItemId;
        this.contextDTO = new ContextDTO(contextName, contextId)
    }

    ContextItemDTO(AssayContextItem assayContextItem) {
        this.label = assayContextItem.attributeElement.label;
        this.valueDisplay = assayContextItem.valueDisplay
        this.contextItemId = assayContextItem.id;
        this.contextDTO = new ContextDTO(assayContextItem.assayContext)
    }

    static final void addCommonItemsToEachCard(final SortedMap<ContextDTO, List<ContextItemDTO>> cardMapForAssay) {
        for (ContextDTO contextDTO : cardMapForAssay.keySet()) { //loop over the exclusive items and find the context to which they belong
            AssayContext assayContext = AssayContext.findById(contextDTO.contextId)

            //get all of the items for this context and diff with exclusive context items to find the items that are not exclusive
            final List<ContextItemDTO> allContextItemsForCard = ContextItemDTO.toContextItemDTOs(assayContext.assayContextItems)
            final List<ContextItemDTO> contextItemDTOList = cardMapForAssay.get(contextDTO)

            for (ContextItemDTO contextItemDTO : allContextItemsForCard) {
                final Collection subtract = CollectionUtils.subtract(allContextItemsForCard, contextItemDTOList)
                contextItemDTOList.addAll(subtract)
            }

        }
    }
    //We mark each item as exclusive
    static final SortedMap<String, List<ContextItemDTO>> buildCardMap(Collection<ContextItemDTO> contextItems) {
        Map<String, List<ContextItemDTO>> cardMap = [:]

        for (ContextItemDTO contextItemDTO : contextItems) {
            ContextDTO contextName = contextItemDTO.contextDTO
            if (contextName) {
                List<ContextItemDTO> contextItemDTOs = cardMap.get(contextName)
                if (contextItemDTOs == null) {
                    contextItemDTOs = []
                }
                contextItemDTO.isExclusiveToCard = true
                contextItemDTOs.add(contextItemDTO)
                cardMap.put(contextName, contextItemDTOs)
            }
        }
        return new TreeMap(cardMap)
    }

    public final static List<ContextItemDTO> toContextItemDTOs(List<AssayContextItem> assayContextItems) {

        List<ContextItemDTO> dtos = []
        for (AssayContextItem assayContextItem : assayContextItems) {
            dtos.add(new ContextItemDTO(assayContextItem));
        }
        return dtos.sort()
    }

    @Override
    public int compareTo(ContextItemDTO thatContextItemDTO) {
        return new CompareToBuilder()
                .append(this.contextDTO.contextName, thatContextItemDTO.contextDTO.contextName)
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
                append(this.contextDTO.contextName, that.contextDTO.contextName).
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
                append(this.contextDTO.contextName).
                append(this.label).
                append(this.valueDisplay).
                toHashCode();
    }
}
