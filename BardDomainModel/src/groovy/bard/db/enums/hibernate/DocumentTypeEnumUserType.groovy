package bard.db.enums.hibernate

import bard.db.enums.DocumentType

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/6/13
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */
class DocumentTypeEnumUserType extends AbstractEnumUserType<DocumentType> {

    public DocumentTypeEnumUserType() {
        super(DocumentType.class)
    }

}