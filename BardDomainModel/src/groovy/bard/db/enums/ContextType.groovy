package bard.db.enums

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 8/14/13
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
enum ContextType implements IEnumUserType {
        BIOLOGY("Biology"),
        ASSAY_PROTOCOL("Assay Protocol"),
        ASSAY_DESIGN("Assay Design"),
        ASSAY_READOUT("Assay Readout"),
        ASSAY_COMPONENTS("Assay Components"),
        EXPERIMENT("Experimental Variables"),
        UNCLASSIFIED("Unclassified")

        final String id;

        private ContextType(String id) {
            this.id = id
        }

        String getId() {
            return id
        }

        static ContextType byId(String id) {
            ContextType status = values().find { it.id == id }
            if (status) {
                return status
            }
            throw new EnumNotFoundException("No enum found for id: $id")
        }
}
