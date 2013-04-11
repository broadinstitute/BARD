package molspreadsheet

import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.SunburstCacheService
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.compounds.CompoundSummary
import bard.core.rest.spring.compounds.TargetClassInfo
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.util.RingNode


class RingManagerService {
    CompoundRestService compoundRestService
    SunburstCacheService sunburstCacheService

    String writeRingTree( RingNode ringNode, boolean includeText, int typeOfColoring = 0 ) {
        if (typeOfColoring  == 2) {
            ringNode = ringNode //We will need to modify the tree
        }
        StringBuilder stringBuilder = new StringBuilder("var \$data = [")
        if (ringNode) {
               if (includeText) {
                   stringBuilder << ringNode.toString()
               } else {
                   stringBuilder << ringNode.toStringNoText()
               }
        }
        stringBuilder << "]"

        stringBuilder.toString()
    }


    Boolean classDataExistsForThisCompound (CompoundSummary compoundSummary){
        Boolean returnValue = false
        if (compoundSummary != null){
            for (Assay assay in compoundSummary.testedAssays) {
                if (assay.targetIds?.size()  > 0)  {  // At least one assay has at least one target -- better make a sunburst
                    returnValue = true
                    break
                }
            }
        }
        return returnValue
    }

    /**
     * The ideas to bring back a map which contains two lists, identified by the keys "hits" and "misses".  For
     * each one of these we will bring back a list of strings, where each string represents a target. Strings in
     * this list are not meant to be unique necessarily.  So we might have:
     *  "hits": "Q123", "Q123", "P456"
     *  "misses": "R789"
     * A particular protein target could conceivably be in both the hits and the misses category.
     *
     * @param compoundSummary
     * @return
     */
    LinkedHashMap<String, List <String>> retrieveActiveInactiveDataFromCompound (CompoundSummary compoundSummary){
        LinkedHashMap<String, List <String>> returnValue = [:]
        returnValue ["hits"]  = []
        returnValue ["misses"]  = []
        if (compoundSummary != null){
           for (Assay assay in compoundSummary.testedAssays) {
               List<String>  currentExperimentIds = assay.experiments
               List<String>  currentTargets = assay.targetIds
               if (currentTargets != null)  {  // If the assay has no targets there is nothing for us to do
                   for (String currentExperimentId in currentExperimentIds) {
                       Long  experimentIdAsLong
                       try {
                           experimentIdAsLong = Long.parseLong(currentExperimentId)
                       } catch (NumberFormatException nfe) {
                           println"Unexpected error. Failure parsing currentExperimentId long"
                       }
                       List<Activity> testedExperimentList = compoundSummary.getTestedExptdata().findAll {Activity activity -> activity.bardExptId == experimentIdAsLong}
                       if (testedExperimentList?.size() > 0)   {
                           for (Activity testedExperiment in testedExperimentList)   {
                               if (testedExperiment.outcome==2) {  // It's a hit!  Save all targets!
                                   for (String oneTarget in currentTargets) {
                                       returnValue ["hits"] <<  oneTarget
                                   }
                               }  else { // It's a miss.  Save all the targets and a different list.
                                   for (String oneTarget in currentTargets) {
                                       returnValue ["misses"] <<  oneTarget
                                   }
                               }
                           }
                       }
                   }

               }
             }

        }
        return returnValue
    }


    RingNode createStub () {
        RingNode.createStubRing ()
    }

    String placeSunburstOnPage ( int width, int height, RingNode ringNode, int typeOfColoring ) {
        StringBuilder stringBuilder = new StringBuilder("")
        int numberOfColors = ringNode.maximumTreeHeight()
        List <String>  everyParent = ringNode.listOfEverybodyWhoIsAParent()
        List <String>  everyUniqueParent =  everyParent.unique().sort()
        stringBuilder << ringNode.placeSunburstOnPage(width,height,everyUniqueParent,numberOfColors,typeOfColoring)
        stringBuilder.toString()
    }


    LinkedHashMap<String, Integer> accumulateAccessionNumbers( List<String> listOfAllTargets ) {
        LinkedHashMap<String, Integer> returnValue = [:]
        for ( String oneTarget in listOfAllTargets)  {
            if (returnValue.containsKey(oneTarget))  {
                returnValue[oneTarget]   =  returnValue[oneTarget]+1
            }  else {
                returnValue[oneTarget]  = 1
            }
        }
        return returnValue
    }



    /**
     * Convert the class information we have into a tree of StubRings suitable for building a sunburst
     *
     * Here are some corner cases:
     *      input empty or null: return a single root node
     *      single input element: single root node with a child
     *      three input elements, two of which are identical: single root node with two children, one of which will have weight==2
     *
     * @param targetClassInfoList
     * @return
     */
    public ringNodeFactory ( List<TargetClassInfo> targetClassInfoList, LinkedHashMap activeInactiveData = [:] ) {
        RingNode rootRingNode = new RingNode("/")
        if (targetClassInfoList?.size()  > 0){
            LinkedHashMap<String, RingNode> ringNodeMgr = [:]
            ringNodeMgr["/"] = rootRingNode
            for (TargetClassInfo targetClassInfo in targetClassInfoList) {
                RingNode currentRingNode = ringNodeMgr["/"]
                List<String> pathElements = targetClassInfo.path?.split("\\\\")
                for (String onePathElements in pathElements) {
                    if (onePathElements?.size()>0) {
                        // is this piece of path in the tree already? If not then add it, otherwise boost the reference count of the existing element
                        if (ringNodeMgr.containsKey(onePathElements)) {
                            currentRingNode =  ringNodeMgr[onePathElements]
                            currentRingNode.size += 1
                        }  else {
                            ringNodeMgr[onePathElements] = new RingNode (onePathElements)
                            currentRingNode.children << ringNodeMgr[onePathElements]
                        }
                        if (activeInactiveData["hits"]?.contains (targetClassInfo.accessionNumber)){
                            ringNodeMgr[onePathElements].actives <<   targetClassInfo.accessionNumber
                        }
                        if (activeInactiveData["misses"]?.contains (targetClassInfo.accessionNumber)){
                            ringNodeMgr[onePathElements].inactives <<   targetClassInfo.accessionNumber
                        }
                    }
                }

            }
        }
        return  rootRingNode
    }


    public  RingNode convertCompoundSummaryIntoSunburst (CompoundSummary compoundSummary, Boolean includeHits, Boolean includeNonHits ){
        LinkedHashMap activeInactiveData = retrieveActiveInactiveDataFromCompound(compoundSummary)
        final List<String> targets = []
        if (includeHits) {
            activeInactiveData["hits"].each {targets <<  it }
        }
        if (includeNonHits) {
            activeInactiveData["misses"].each {targets <<  it }
        }
        LinkedHashMap<String, Integer> accumulatedTargets = accumulateAccessionNumbers( targets )
        List<List<TargetClassInfo>> accumulatedMaps = []
        accumulatedTargets.each{k,v->
            List<String> hierarchyDescription = sunburstCacheService.getTargetClassInfo(k)
            if (hierarchyDescription != null){
                accumulatedMaps<<sunburstCacheService.getTargetClassInfo(k)
            }
        }
        return ringNodeFactory(accumulatedMaps.flatten(),activeInactiveData )
    }

    /**
     * Here's a wrapper routine in case someone wants to start with a CID as opposed to a fully constructed CompoundSummary
     * @param cid
     * @param includeHits
     * @param includeNonHits
     * @return
     */
    public  RingNode convertCompoundIntoSunburst (Long cid, Boolean includeHits, Boolean includeNonHits ){
        CompoundSummary compoundSummary = compoundRestService.getSummaryForCompoundFROM_PREVIOUS_VERSION(cid)
        convertCompoundSummaryIntoSunburst ( compoundSummary,  includeHits,  includeNonHits )
    }



}
