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

package bard.db.enums

public enum ReadyForExtraction implements IEnumUserType {
    NOT_READY("Not Ready"),
    READY("Ready"),
    STARTED("Started"),
    COMPLETE("Complete"),
    FAILED("Failed")

    final String id

    private ReadyForExtraction(String id) {
        this.id = id
    }

    String getId() {
        return id
    }
    /**
     * Indicates whether a client can change
     * the current status of a resource to the given value.
     * The data export API is one such client, and is only allowed
     * to set specific values
     * @return
     */
    static boolean isAllowed(ReadyForExtraction readyForExtraction) {
        boolean isAllowed = false
        switch (readyForExtraction) {
            case READY:
            case STARTED:
            case COMPLETE:
            case FAILED:
                isAllowed = true
            default:
                // no op
            break
        }
        isAllowed
    }

    static ReadyForExtraction byId(String id) {
        ReadyForExtraction readyForExtraction = values().find { it.id == id }
        if (readyForExtraction) {
            return readyForExtraction
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}
