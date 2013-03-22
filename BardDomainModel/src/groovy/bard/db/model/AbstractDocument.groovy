package bard.db.model

import org.apache.commons.lang.BooleanUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.validator.UrlValidator
import org.springframework.validation.Errors

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/5/12
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractDocument implements IDocumentType {

    private static final int DOCUMENT_NAME_MAX_SIZE = 500
    private static final int DOCUMENT_TYPE_MAX_SIZE = 20
    private static final int MODIFIED_BY_MAX_SIZE = 40

    String documentName
    String documentType
    String documentContent
    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static constraints = {
        documentName(blank: false, maxSize: DOCUMENT_NAME_MAX_SIZE)
        documentType(blank: false, maxSize: DOCUMENT_TYPE_MAX_SIZE, inList: [DOCUMENT_TYPE_DESCRIPTION, DOCUMENT_TYPE_PROTOCOL, DOCUMENT_TYPE_COMMENTS, DOCUMENT_TYPE_PUBLICATION, DOCUMENT_TYPE_EXTERNAL_URL, DOCUMENT_TYPE_OTHER])
        documentContent(nullable: true, blank: false, validator: {val, self, errors-> validContentUrl(val,self,errors)})
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    /**
     * make sure the content looks like a url for publictions and external URLs
     *
     * @param val the value of the documentContent field
     * @param self reference the other document object
     * @param errors Spring Errors object so we can add errors
     */
    private static void validContentUrl(Object val, Object self, Errors errors){
        if ([IDocumentType.DOCUMENT_TYPE_PUBLICATION, IDocumentType.DOCUMENT_TYPE_EXTERNAL_URL].contains(self.documentType)) {
            if (StringUtils.isNotBlank(val) && BooleanUtils.isFalse(new UrlValidator().isValid(val))) {
                String field = 'documentContent'
                errors.rejectValue(field, 'document.invalid.url.message', [field, self.documentType] as Object[], "Valid URL expected for ${self.documentType}")
            }
        }
    }

    public abstract Object getOwner();
}
