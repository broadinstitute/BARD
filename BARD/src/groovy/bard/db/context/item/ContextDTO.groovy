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

package bard.db.context.item

import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/3/13
 * Time: 6:05 AM
 * To change this template use File | Settings | File Templates.
 */
import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/3/13
 * Time: 5:42 AM
 * To change this template use File | Settings | File Templates.
 */
class ContextDTO implements Comparable<ContextDTO> {


    final String contextName;
    final Long contextId

    public ContextDTO(final String contextName, final Long contextId) {
        this.contextName = contextName;
        this.contextId = contextId;
    }

    public ContextDTO(AssayContext assayContext) {
        this.contextName = assayContext.preferredName;
        this.contextId = assayContext.id
    }


    @Override
    public int compareTo(ContextDTO thatContextDTO) {
        return new CompareToBuilder()
                .append(this.contextId, thatContextDTO.contextId)
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
        if (!(aThat instanceof ContextDTO)) return false;

        ContextDTO that = (ContextDTO) aThat;
        return new EqualsBuilder().
                append(this.contextId, that.contextId).
                isEquals();
    }

    /**
     * A class that overrides equals must also override hashCode.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(this.contextId).
                toHashCode();
    }
}
