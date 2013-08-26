package bard.db.experiment

import bard.db.enums.hibernate.DocumentTypeEnumUserType
import bard.db.model.AbstractDocument
import bard.db.registration.Assay

class ExperimentDocument extends AbstractDocument {

    Experiment experiment

    static belongsTo = [experiment: Experiment]

    static mapping = {
        table('EXPERIMENT_DOCUMENT')
        id(column: 'EXPERIMENT_DOCUMENT_ID', generator: 'sequence', params: [sequence: 'EXPERIMENT_DOCUMENT_ID_SEQ'])
        documentContent(type: "text", sqlType: 'clob')
        documentType(type:DocumentTypeEnumUserType)
    }

    public Object getOwner() {
        return experiment;
    }

    public ExperimentDocument clone() {
        ExperimentDocument document = new ExperimentDocument(documentName: documentName,
                documentType: documentType,
                documentContent: documentContent);

        return document;
    }
}
