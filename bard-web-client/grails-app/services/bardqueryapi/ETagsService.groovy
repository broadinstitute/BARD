package bardqueryapi

import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ProjectRestService
import bard.core.rest.spring.AssayRestService
import org.apache.commons.lang3.RandomStringUtils
import bard.core.rest.spring.ETagRestService

class ETagsService {
    final static int RANDOM_STRING_LENGTH = 9
    final static String CHAR_SET = (('A'..'Z') + ('0'..'9')).join()
    CompoundRestService compoundRestService
    ProjectRestService projectRestService
    AssayRestService assayRestService
    ETagRestService eTagRestService

    protected String createETag(final EntityType entityType, final List<Long> resourceIds, final List<String> etags) {
        final String eTagName = RandomStringUtils.random(RANDOM_STRING_LENGTH, CHAR_SET.toCharArray())
        String eTag = null
        switch (entityType) {
            case EntityType.COMPOUND:
                eTag = compoundRestService.newETag(eTagName, resourceIds)
                break
            case EntityType.ASSAY:
                eTag = assayRestService.newETag(eTagName, resourceIds)
                break
            case EntityType.PROJECT:
                eTag = projectRestService.newETag(eTagName, resourceIds)
                break
            case EntityType.COMPOSITE:
                eTag = eTagRestService.newCompositeETag(eTagName, etags)
                break
            default:
                throw new Exception("Unhandled Type : " + entityType)
        }
        return eTag
    }

    public String createCompositeETags(final List<Long> cids, final List<Long> pids, final List<Long> adids) {

        final List<String> etags = []
        if (cids) {
            final String compoundETag = createETag(EntityType.COMPOUND, cids, etags)
            if (compoundETag) {
                etags.add(compoundETag)
            }
        }
        if (pids) {
            final String projectETag = createETag(EntityType.PROJECT, pids, etags)
            if (projectETag) {
                etags.add(projectETag);
            }
        }
        if (adids) {
            final String assayETag = createETag(EntityType.ASSAY, adids, etags)
            if (assayETag) {
                etags.add(assayETag)
            }
        }
        if (etags) {
            return createETag(EntityType.COMPOSITE, [], etags)
        }
        return null
    }
}
