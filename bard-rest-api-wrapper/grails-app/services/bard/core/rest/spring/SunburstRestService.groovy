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
    public void loadTargetsFromFile(File locallyGeneratedResource, File resourceFromRDM) {
        Map<String, String> targetIdToPath = [:]
        int index = 0
        resourceFromRDM.eachLine { line ->
            if (index > 0) {
                if (StringUtils.isNotBlank(line)) {
                    List<String> spreadSheetData = line.split("\t") as List<String>
                    targetIdToPath.put(spreadSheetData.get(0), spreadSheetData.get(3))
                }
            }
            ++index
        }
        //TargetClassInfo targetClassInfo = null
        locallyGeneratedResource.eachLine { line ->
            if (StringUtils.isNotBlank(line)) {
                List<String> spreadSheetData = line.split("\t") as List<String>
                TargetClassInfo targetClassInfo = TargetClassInfo.generateClassInformation(spreadSheetData)
                String path = targetIdToPath.get(targetClassInfo.getId());
                if (StringUtils.isNotBlank(path)) {
                    targetClassInfo.setPath(path)
                    sunburstCacheService.save(targetClassInfo)
                } else {
                    log.error("Path for " + targetClassInfo.id + " not found")
                }
            }
        }
    }


}
