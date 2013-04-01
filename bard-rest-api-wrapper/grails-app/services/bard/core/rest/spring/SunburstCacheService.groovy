package bard.core.rest.spring

import bard.core.rest.spring.compounds.TargetClassInfo
import grails.plugin.cache.Cacheable

class SunburstCacheService extends AbstractRestService {
    def transactional = false

    final Map<String, TargetClassInfo> targets = [:]

    //We would be using a persistent cache
    //@CachePut(value = 'target', key = '#targetClassInfo.id')
    void save(TargetClassInfo targetClassInfo) {
        //targets.put(targetClassInfo.id, targetClassInfo)
        targets.put(targetClassInfo.accessionNumber, targetClassInfo)
        //log.info("Saving " + targetClassInfo.getId())
    }

    @Cacheable(value = 'target')
    TargetClassInfo getTargetClassInfo(String accessionNumber) {
        final TargetClassInfo targetClassInfo = this.targets.get(accessionNumber)
        if (!targetClassInfo) {
            log.error("Not found ${accessionNumber}")
            //TODO: Go to NCGC then put in map
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
