package dataexport.registration

/**
 * Encapsulates all the media type needed to generate assay definitions for extraction
  */
class MediaTypesDTO {
    final String elementMediaType
    final String assayMediaType
    final String assayDocMediaType
    final String assaysMediaType
    final String resultTypeMediaType

    MediaTypesDTO( final String elementMediaType, String resultTypeMediaType,
                        final String assayMediaType,final String assayDocMediaType,
                        final String assaysMediaType){
        this.elementMediaType = elementMediaType
        this.resultTypeMediaType = resultTypeMediaType
        this.assaysMediaType = assaysMediaType
        this.assayMediaType = assayMediaType
        this.assayDocMediaType = assayDocMediaType

    }
}
