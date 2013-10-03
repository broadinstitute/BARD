package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 9/25/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class Descriptor<T extends Descriptor> {

    private static final int LABEL_MAX_SIZE = 128
    private static final int DESCRIPTION_MAX_SIZE = 1000
    private static final int ABBREVIATION_MAX_SIZE = 20
    private static final int SYNONYMS_MAX_SIZE = 1000
    private static final int EXTERNAL_URL_MAX_SIZE = 1000
    private static final int ELEMENT_STATUS_MAX_SIZE = 20
    private static final int FULL_PATH_MAX_SIZE = 3000


    T parent
    Element element
    ElementStatus elementStatus = ElementStatus.Pending

    String label
    Boolean leaf
    String description
    String fullPath
    String abbreviation

    String synonyms
    String externalURL
    Element unit




    static constraints = {

        parent(nullable: true)
        element()
        elementStatus(nullable: false)

        label(nullable: false, unique: true, maxSize: LABEL_MAX_SIZE)
        leaf()
        description(nullable: true, maxSize: DESCRIPTION_MAX_SIZE)
        fullPath(nullable: true, blank: false, maxSize: FULL_PATH_MAX_SIZE)
        abbreviation(nullable: true, maxSize: ABBREVIATION_MAX_SIZE)

        synonyms(nullable: true, maxSize: SYNONYMS_MAX_SIZE)
        externalURL(nullable: true, maxSize: EXTERNAL_URL_MAX_SIZE)
        unit(nullable: true)
    }
/**
 * the mapping block isn't additive so it needs to be in the subclass to allow specifying the table
 */
//    static mapping = {
//            table('ASSAY_DESCRIPTOR_TREE')
//            id(column: 'NODE_ID', generator: 'assigned')
//            version(false)
//            bardURI(column: 'BARD_URI')
//            externalURL(column: 'EXTERNAL_URL')
//            parent(column: 'PARENT_NODE_ID')
//        }

    /**
     *
     * @return a string representing the ontology path hierarchy in total
     */
    String generateOntologyBreadCrumb() {
        generateOntologyBreadCrumb(path.size())
    }

    /**
     *
     * @param pathLength
     * @return a string representing the ontology path hierarchy to the specified pathLength
     */
    String generateOntologyBreadCrumb(int pathLength) {
        int toIndexExclusive = Math.min(pathLength, path.size())
        path.subList(0, toIndexExclusive).collect { it.label }.join('> ') + '>'
    }


    List<T> getPath() {
        if (parent) {
            parent.getPath() << this
        } else {
            [this]
        }
    }

    List<T> getPath(BardDescriptor root) {
        if (this.equals(root) || parent == null) {
            [this]
        } else {
            parent.getPath(root) << this
        }
    }

}

