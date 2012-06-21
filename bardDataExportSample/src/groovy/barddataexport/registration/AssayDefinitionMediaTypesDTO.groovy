package barddataexport.registration

/**
 * Encapsulates all the media type needed to generate assay definitions for extraction
  */
class AssayDefinitionMediaTypesDTO {
    final String elementMediaType
    final String resultTypeMediaType
    final String assayMediaType
    final String assayDocMediaType
    final String assaysMediaType

    AssayDefinitionMediaTypesDTO( final String elementMediaType,final String resultTypeMediaType,
                        final String assayMediaType,final String assayDocMediaType,
                        final String assaysMediaType){
        this.elementMediaType = elementMediaType
        this.resultTypeMediaType = resultTypeMediaType
        this.assaysMediaType = assaysMediaType
        this.assayMediaType = assayMediaType
        this.assayDocMediaType = assayDocMediaType

    }
}
