package bard.db.context.item

import org.apache.commons.lang3.builder.CompareToBuilder

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/22/13
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
class ContextDTOComparator implements Comparator<ContextDTO> {

    @Override
    int compare(ContextDTO first, ContextDTO second) {
        return new CompareToBuilder()
                .append(first.contextName.toLowerCase(), second.contextName.toLowerCase())
                .append(first.contextId, second.contextId)
                .toComparison();
    }
}
