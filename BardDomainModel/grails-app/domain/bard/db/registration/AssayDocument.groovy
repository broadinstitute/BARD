package bard.db.registration

import bard.db.model.AbstractDocument

class AssayDocument extends AbstractDocument {

    Assay assay

    static belongsTo = [assay: Assay]

    static mapping = {
        table('ASSAY_DOCUMENT')
        id(column: 'ASSAY_DOCUMENT_ID', generator: 'sequence', params: [sequence: 'ASSAY_DOCUMENT_ID_SEQ'])
        documentContent(type: "text", sqlType: 'clob')
    }

    public Object getOwner() {
        return assay;
    }
}
