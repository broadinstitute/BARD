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
        documentType(nullable:true, maxSize: DOCUMENT_TYPE_MAX_SIZE)
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
            if (StringUtils.isNotBlank(val) && BooleanUtils.isFalse(new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES).isValid(val))) {
                String field = 'documentContent'
                errors.rejectValue(field, 'document.invalid.url.message', [field, self.documentType.id] as Object[], "Valid URL expected for ${self.documentType.id}")
            }
        }
    }

    public abstract Object getOwner();
}
