package bard.db.model

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/4/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IDocumentType {
    public static final String DOCUMENT_TYPE_DESCRIPTION = 'Description'
    public static final String DOCUMENT_TYPE_PROTOCOL = 'Protocol'
    public static final String DOCUMENT_TYPE_COMMENTS = 'Comments'
    public static final String DOCUMENT_TYPE_PAPER = 'Paper'
    public static final String DOCUMENT_TYPE_EXTERNAL_URL = 'External URL'
    public static final String DOCUMENT_TYPE_OTHER = 'Other'

    public static final List<String> DOCUMENT_TYPE_DISPLAY_ORDER = [DOCUMENT_TYPE_DESCRIPTION, DOCUMENT_TYPE_PROTOCOL, DOCUMENT_TYPE_COMMENTS, DOCUMENT_TYPE_PAPER, DOCUMENT_TYPE_EXTERNAL_URL, DOCUMENT_TYPE_OTHER]

}