package bard.db.project

import bard.db.enums.hibernate.DocumentTypeEnumUserType
import bard.db.model.AbstractDocument

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/5/12
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectDocument extends AbstractDocument {

    Project project

    static belongsTo = [project: Project]

    static mapping = {
        table('PROJECT_DOCUMENT')
        id(column: 'PROJECT_DOCUMENT_ID', generator: 'sequence', params: [sequence: 'PROJECT_DOCUMENT_ID_SEQ'])
        documentContent(type: "text", sqlType: 'clob')
        documentType(type:DocumentTypeEnumUserType)
    }

    @Override
    Object getOwner() {
        return project;
    }
}
