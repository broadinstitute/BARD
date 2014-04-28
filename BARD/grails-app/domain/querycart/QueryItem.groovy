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

package querycart

import com.metasieve.shoppingcart.Shoppable
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.StringUtils

class QueryItem extends Shoppable {

    static final int MAXIMUM_NAME_FIELD_LENGTH = 4000

    QueryItemType queryItemType
    Long internalId
    Long externalId
    String name

    QueryItem() {

    }

    QueryItem(String name, Long internalId, Long externalId, QueryItemType queryItemType) {
        this.name = name
        this.externalId = externalId
        this.internalId = internalId
        this.queryItemType = queryItemType
    }

   /**
     * Catch the beforeValidate event and apply pre-processing to the fields
     */
    void beforeValidate() {
        this.name = StringUtils.abbreviate(name?.trim(), MAXIMUM_NAME_FIELD_LENGTH)
    }

    static constraints = {
        queryItemType nullable: false
        internalId nullable: true, min: 1L
        externalId nullable: false, min: 1L, unique: 'queryItemType'
        name nullable: false, blank: false, maxSize: MAXIMUM_NAME_FIELD_LENGTH
    }

    @Override
    String toString() {
        if (this.name == null || this.name == 'null') {
            return ""
        }
        return name
    }

    /**
     *  equals
     * @param o
     * @return
     */
    boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        QueryItem that = (QueryItem) obj;
        return new EqualsBuilder().
                append(this.externalId, that.externalId).
                append(this.queryItemType, that.queryItemType).
                isEquals();
    }

    /**
     *  hashCode
     * @return
     */
    int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(this.externalId).
                append(this.queryItemType).
                toHashCode();
    }

}

enum QueryItemType {
    AssayDefinition,
    Compound,
    Project
}
