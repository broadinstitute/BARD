package bard.core.rest.spring

import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.util.Target
import bard.core.rest.spring.util.TargetClassification

class TargetRestService extends AbstractRestService {
    def transactional=false
    public String getResourceContext() {
        return RestApiConstants.TARGETS_RESOURCE
    }

    /**
     *
     * @return a url prefix for free text compound searches
     */
    @Override
    public String getSearchResource() {
        return null
    }

    @Override
    public String getResource() {
        String resourceName = RestApiConstants.TARGETS_RESOURCE
        return new StringBuilder(externalUrlDTO.baseUrl).
                append(resourceName).
                append(RestApiConstants.FORWARD_SLASH).
                toString();
    }
    /**
     *  Get classifications from source for a given target
     *   targets/{acc}/classification/{source}*   Example http://bard.nih.gov/api/v15/targets/accession/P20393/classification/panther
     *
     * @param accessionNumber
     * @param source
     * @return list of target classifications
     *
     *
     */
    public List<TargetClassification> getClassificationsFromSourceWithTarget(final String source,final String targetAccessionNumber) {
        final StringBuilder resource =
            new StringBuilder(this.getResource()).
                    append(RestApiConstants.ACCESSION).
                    append(RestApiConstants.FORWARD_SLASH).
                    append(targetAccessionNumber).
                    append(RestApiConstants.CLASSIFICATION).
                    append(RestApiConstants.FORWARD_SLASH).
                    append(source)

        final URL url = new URL(resource.toString())
        Map<String, List<TargetClassification>> targetClassificationsMap = getForObject(url.toURI(), HashMap.class)
        List<TargetClassification> targetClassifications = targetClassificationsMap.get(targetAccessionNumber) as List<TargetClassification>
        return targetClassifications
    }
    /**
     * Get target accessions for the specific classification id.
     * /targets/classification/{source}/{id}
     * Example http://bard.nih.gov/api/v15/targets/classification/panther/PC00169
     *
     * @param targetClassificationId
     * @return
     */
    public List<Target> getTargetsFromClassificationId(final String source,final String targetClassificationId) {
        final StringBuilder resource =
            new StringBuilder(this.getResource()).
                    append(RestApiConstants.CLASSIFICATION).
                    append(RestApiConstants.FORWARD_SLASH).
                    append(source).
                     append(RestApiConstants.FORWARD_SLASH).
                    append(targetClassificationId)

        final URL url = new URL(resource.toString())
        List<Target> targets = getForObject(url.toURI(), Target[].class) as List<Target>
        return targets
    }
    /**
     * Get a target given an accession number
     *
     * Example http://bard.nih.gov/api/v15/targets/accession/P20393
     * @param accessionNumber
     * @return  Target
     */
    public Target getTargetByAccessionNumber(final String accessionNumber) {
        final StringBuilder resource =
            new StringBuilder(this.getResource()).
                    append(RestApiConstants.ACCESSION).
                    append(RestApiConstants.FORWARD_SLASH).
                     append(accessionNumber)

        final URL url = new URL(resource.toString())
        Target target = getForObject(url.toURI(), Target.class)
        return target
    }
}
