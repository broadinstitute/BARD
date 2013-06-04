package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 2/6/13
 * Time: 2:36 AM
 * To change this template use File | Settings | File Templates.
 */
public enum DocumentType implements IEnumUserType {
    DOCUMENT_TYPE_DESCRIPTION('Description'),
    DOCUMENT_TYPE_PROTOCOL('Protocol'),
    DOCUMENT_TYPE_COMMENTS('Comments'),
    DOCUMENT_TYPE_PUBLICATION('Publication'),
    DOCUMENT_TYPE_EXTERNAL_URL('External URL'),
    DOCUMENT_TYPE_OTHER('Other');

    final String id;

    private DocumentType(String id) {
        this.id = id
    }

    String getId() {
        return id
    }

    static DocumentType byId(String id) {
        DocumentType documentType = values().find { it.id == id }
        if (documentType) {
            return documentType
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }
    public static final List<String> DOCUMENT_TYPE_DISPLAY_ORDER = [
            DOCUMENT_TYPE_DESCRIPTION,
            DOCUMENT_TYPE_PROTOCOL,
            DOCUMENT_TYPE_COMMENTS,
            DOCUMENT_TYPE_PUBLICATION,
            DOCUMENT_TYPE_EXTERNAL_URL,
            DOCUMENT_TYPE_OTHER
    ]

}