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

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/8/12
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
enum AddChildMethod implements IEnumUserType {
    RDM_REQUEST('RDM request', 'Children can only be added through a request to the RDM Team',"rdm_request"),
    DIRECT('direct', 'Any user is allowed to add a child element and use the element immediately',"direct"),
    NO('no', 'No children can be added to this element',"no_children")

    final String id;
    final String description;
    final String label;

    private AddChildMethod(String id, String description, String label) {
        this.id = id
        this.description = description
        this.label = label
    }
    String getLabel(){
        return this.label
    }
    String getDescription() {
        return this.description
    }

    String getId() {
        return id
    }

    static AddChildMethod byId(String id) {
        AddChildMethod addChildMethod = values().find { it.id == id }
        if (addChildMethod) {
            return addChildMethod
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}
