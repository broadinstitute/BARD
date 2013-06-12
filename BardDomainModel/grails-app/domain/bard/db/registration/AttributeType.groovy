package bard.db.registration

/**
 * Naming here can probably be improved, Fixed
 *
 * See https://github.com/broadinstitute/BARD/wiki/Business-rules#attribute_type-and-expected-value-type
 *
 * for how this interacts with expected_value_type
 */
enum AttributeType {
    /**  meaning the value portion of the AssayContextItem is set / fixed when the assayContextItem is created or edited
     */
	Fixed,   //
    /**
     *  Meaning there will be a list of values that will be entered when the assayContextItem is created or enter and used to constrain data at ResultUpload time
     */
	List,
    /**
     * Meaning a numeric range will be set at a  assayContextItem is created or enter
     */
	Range,
    /**
     * Free is a bit tough on naming, it means that the assayContextItem will be created at assay definition time to indicate that
     * at ResultUpload time contextItem with this attribute should be created at the Result and or ExperimentContext level
     *
     * This is only appropriate with expectedValueTypes of 'free text' and 'numeric'
     */
	Free

}
