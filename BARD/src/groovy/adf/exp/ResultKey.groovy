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

package adf.exp

import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/8/14
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
class ResultKey {
    Long resultTypeId;
    Long statsModifierId;
    String resultType;

    public ResultKey(JsonResult jr) {
        this.resultType = jr.resultType
        this.resultTypeId = jr.resultTypeId;
        this.statsModifierId = jr.statsModifierId;
    }

    public ResultKey(JsonResultContextItem ci) {
        this.resultTypeId = ci.attributeId
        this.resultType = ci.attribute
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof ResultKey)) return false

        ResultKey resultKey = (ResultKey) o

        if (resultTypeId != resultKey.resultTypeId) return false
        if (statsModifierId != resultKey.statsModifierId) return false

        return true
    }

    int hashCode() {
        int result
        result = (resultTypeId != null ? resultTypeId.hashCode() : 0)
        result = 31 * result + (statsModifierId != null ? statsModifierId.hashCode() : 0)
        return result
    }

    public String toString() {
        return resultType
    }
}
