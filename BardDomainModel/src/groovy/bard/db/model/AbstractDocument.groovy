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

package bard.db.model

import bard.db.enums.DocumentType
import org.apache.commons.lang.BooleanUtils
import org.apache.commons.validator.UrlValidator
import org.springframework.validation.Errors
import org.apache.commons.lang3.StringUtils


/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/5/12
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractDocument {

    private static final int DOCUMENT_NAME_MAX_SIZE = 500
    private static final int DOCUMENT_TYPE_MAX_SIZE = 20
    private static final int MODIFIED_BY_MAX_SIZE = 40

    String documentName
    DocumentType documentType
    String documentContent
    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String modifiedBy

    static constraints = {
        documentName(blank: false, maxSize: DOCUMENT_NAME_MAX_SIZE)
        documentType(nullable:true)
        documentContent(nullable: true, blank: false, validator: {val, self, errors-> validContentUrl(val,self,errors)})
        dateCreated(nullable: false)
        lastUpdated(nullable: false)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    /**
     * make sure the content looks like a url for publications and external URLs
     *
     * @param val the value of the documentContent field
     * @param self reference the other document object
     * @param errors Spring Errors object so we can add errors
     */
    private static void validContentUrl(Object val, Object self, Errors errors){
        if ([DocumentType.DOCUMENT_TYPE_PUBLICATION, DocumentType.DOCUMENT_TYPE_EXTERNAL_URL].contains(self.documentType)) {
            if (StringUtils.isNotBlank(val) && BooleanUtils.isFalse(new UrlValidator().isValid(val))) {
                String field = 'documentContent'
                errors.rejectValue(field, 'document.invalid.url.message', [field, self.documentType.id] as Object[], "Valid URL expected for ${self.documentType.id}")
            }
        }
    }

    public abstract Object getOwner();
}
