/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
