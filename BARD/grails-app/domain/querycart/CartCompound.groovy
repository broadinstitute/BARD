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

import org.apache.commons.lang.StringUtils

class CartCompound extends QueryItem {

    static final int MAXIMUM_SMILES_FIELD_LENGTH = 255

    String smiles

    CartCompound() {
        this.queryItemType = QueryItemType.Compound
    }
    //TODO: Should only be called for testing, because validation is by passed
    CartCompound(String smiles, String name, Long compoundId) {
        this.smiles = smiles
        this.name = name
        this.externalId = compoundId
        this.queryItemType = QueryItemType.Compound
    }

    /**
     * Catch the beforeValidate event and apply pre-processing to the fields
     */
    void beforeValidate() {
        super.beforeValidate()
        this.smiles = StringUtils.abbreviate(smiles?.trim(), MAXIMUM_SMILES_FIELD_LENGTH)
    }


    void setSmiles(String smilesString) {
        this.smiles = StringUtils.abbreviate(smilesString?.trim(), MAXIMUM_SMILES_FIELD_LENGTH)
    }



    static constraints = {
        smiles(nullable: false, maxSize: MAXIMUM_SMILES_FIELD_LENGTH)
    }

    static transients = ['queryService']

    @Override
    String toString() {
        if (StringUtils.isBlank(name) || name == 'null') {
            return "PubChem CID=${externalId}"
        }
        return name
    }
}
