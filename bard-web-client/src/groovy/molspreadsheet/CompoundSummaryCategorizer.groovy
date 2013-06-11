package molspreadsheet

import org.apache.log4j.Level
import org.apache.log4j.Logger

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/10/13
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
class CompoundSummaryCategorizer {
    final LinkedHashMap<Long, SingleEidSummary> totalContents = [:]
    final LinkedHashMap<String, Integer> assayFormatMap = [:]
    final LinkedHashMap<String, Integer> assayTypeMap = [:]
    final LinkedHashMap<String, Integer> biologicalProcessMap = [:]
    final LinkedHashMap<String, Integer> proteinTargetMap = [:]


    static Logger log
    static {
        this.log = Logger.getLogger(RingManagerService.class)
        log.setLevel(Level.ERROR)
    }



    public CompoundSummaryCategorizer() {

    }




    /***
     * store a key in a map with a unique value as an index
     * @param arbitraryMap
     * @param arbitraryKey
     * @return the unique index we generated
     */
    int deriveArbitraryIndex (LinkedHashMap<String, Integer> arbitraryMap,String arbitraryKey) {
        int returnValue = 0
        if (arbitraryMap.size() == 0) {
            arbitraryMap[arbitraryKey]  =  returnValue
        } else {
            if (arbitraryMap.containsKey(arbitraryKey)) {
                returnValue =  arbitraryMap [arbitraryKey]
            }  else {
                returnValue = arbitraryMap.max {it.value}.value  + 1
                arbitraryMap [arbitraryKey] =  returnValue
            }
        }
        return returnValue
    }


    int deriveAssayFormatIndex (String assayFormat) {
        deriveArbitraryIndex (assayFormatMap,assayFormat)
    }


    int deriveAssayTypeIndex(String assayType) {
        deriveArbitraryIndex (assayTypeMap,assayType)
    }


    int deriveBiologicalProcessIndex (String biologicalProcess) {
        deriveArbitraryIndex (biologicalProcessMap,biologicalProcess)
    }


    int deriveProteinTargetIndex (String proteinTargets) {
        deriveArbitraryIndex (proteinTargetMap,proteinTargets)
    }

    public addNewRecord (long eid, String assayFormat, String assayType ) {
        if (totalContents.keySet().contains(eid)) {
            log.warn("Error aggregating experiment data. Repeated experiment ID =: '${eid}'")
        }  else {
            totalContents[eid] = new SingleEidSummary( eid,  assayFormat,  assayType)
        }

    }

    class SingleEidSummary {
        long eid = 0
        int assayFormatIndex = 0
        int assayTypeIndex = 0
        int biologicalProcessIndex = 0
        int proteinTargetsIndex = 0


        public SingleEidSummary(long eid, String assayFormat, String assayType) {
            this.eid =  eid
            this.assayFormatIndex = deriveAssayFormatIndex (assayFormat)
            this.assayTypeIndex = deriveAssayTypeIndex (assayType)
        }


    }

}
