package bard.core.rest.spring

import org.apache.commons.lang.StringUtils
import bard.core.rest.spring.compounds.TargetClassInfo

class SunburstRestService {
    def transactional = false
    SunburstCacheService sunburstCacheService
    /**
     *
     * @param locallyGeneratedResource
     * @param resourceFromRDM - This file was given to us by Paul Clemons - Contains
     */
    public void loadTargetsFromFile(File targets) {
        targets.eachLine { line ->
            if (StringUtils.isNotBlank(line)) {
                List<String> spreadSheetData = line.split("\t") as List<String>
                TargetClassInfo targetClassInfo = TargetClassInfo.generateClassInformation(spreadSheetData)
                sunburstCacheService.save(targetClassInfo)
            }
        }
    }


}
