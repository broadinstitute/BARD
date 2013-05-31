package bard.core.rest.spring

import bard.core.rest.spring.compounds.TargetClassInfo
import bard.core.rest.spring.util.Target
import grails.plugin.cache.Cacheable

class SunburstCacheService extends AbstractRestService {
    def transactional = false
    TargetRestService targetRestService

    final Map<String, List<TargetClassInfo>> targets = [:]

    //@CachePut(value = 'target', key = '#targetClassInfo.id')
    void save(TargetClassInfo targetClassInfo) {
         List<TargetClassInfo> targetClassInfos = targets.get(targetClassInfo.accessionNumber)
        if(targetClassInfos == null){
            targetClassInfos = new ArrayList<TargetClassInfo>()
        }
        if(!targetClassInfos.contains(targetClassInfo)){
            targetClassInfos.add(targetClassInfo)
            targets.put(targetClassInfo.accessionNumber, targetClassInfos)
        }
     }

    @Cacheable(value = 'target')
    List<TargetClassInfo> getTargetClassInfo(String accessionNumber) {
        List<TargetClassInfo> targetClassInfo = this.targets.get(accessionNumber)
        if (!targetClassInfo) {
            log.info("Not found ${accessionNumber} going to REST-API")
            Target target = null
            try{
                target = this.targetRestService.getTargetByAccessionNumber(accessionNumber)
            }catch (Exception) {
                log.info("No information available for ${accessionNumber}")
                return null
            }
            targetClassInfo = Target.constructTargetInformation(target)
             this.targets.put(accessionNumber,targetClassInfo)
        }
        return targetClassInfo
    }

    @Override
    String getResource() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getSearchResource() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getResourceContext() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}
