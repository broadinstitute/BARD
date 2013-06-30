package molspreadsheet

import bard.core.rest.spring.util.RingNode
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
     * Now we know the names of all of those targets we accumulated earlier. Insert the names, so that we can display them
     * when asked.
     * @param mapBiologyIdToProteinAscensionNumber
     * @param mapAccessionNumberToTargetClassName
     * @return
     */
    public void backPopulateTargetNames( LinkedHashMap<Long, String> mapBiologyIdToProteinAscensionNumber,
                                        LinkedHashMap<String, String> mapAccessionNumberToTargetClassName)  {
        if ( ( totalContents != null) &&
             ( mapBiologyIdToProteinAscensionNumber != null)  &&
             ( mapAccessionNumberToTargetClassName != null)) {
            totalContents.each{key,value->
                SingleEidSummary singleEidSummary = totalContents [key]
                if ((singleEidSummary.unconvertedBiologyObjects!=null) &&
                        (singleEidSummary.unconvertedBiologyObjects.size()>0)) {
                    for (Long biologyId in singleEidSummary.unconvertedBiologyObjects) {
                        if (mapBiologyIdToProteinAscensionNumber.containsKey(biologyId)) {
                            String accessionNumber =  mapBiologyIdToProteinAscensionNumber[biologyId]
                            if (mapAccessionNumberToTargetClassName.containsKey(accessionNumber)) {
                                singleEidSummary.proteinTargetClassList<< mapAccessionNumberToTargetClassName [accessionNumber]
                            }
                        }
                    }

                }
        }
    }
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

    public addNewRecord (long eid, String assayFormat, String assayType, String assayName, String assayCapId  ) {
        if (totalContents.keySet().contains(eid)) {
            log.warn("Duplicate data coming from the backend. Repeated experiment ID =: '${eid}'")
        }  else {
            totalContents[eid] = new SingleEidSummary( eid,  assayFormat,  assayType, assayName, assayCapId )
        }

    }


    public combineInNewProteinTargetValue (long eid, String proteinTarget ) {
        if (!totalContents.keySet().contains(eid)) {
            log.warn("Error expected experiment ID =: '${eid}'")
        }  else {
            SingleEidSummary singleEidSummary = totalContents[eid]
            int proteinTargetIndex = deriveProteinTargetIndex (proteinTarget)
            if (!singleEidSummary.proteinTargetsIndexList.contains(proteinTargetIndex))
                singleEidSummary.proteinTargetsIndexList <<  proteinTargetIndex
        }

    }


    public combineInNewBiologicalProcessValue (long eid, String biologicalProcess ) {
        if (!totalContents.keySet().contains(eid)) {
            log.warn("Error expected experiment ID =:  '${eid}'")
        }  else {
            SingleEidSummary singleEidSummary = totalContents[eid]
            int biologicalProcessIndex = deriveBiologicalProcessIndex (biologicalProcess)
            if (!singleEidSummary.biologicalProcessIndexList.contains(biologicalProcessIndex))
                singleEidSummary.biologicalProcessIndexList <<  biologicalProcessIndex
        }

    }




    public updateOutcome (long eid, int outcome, List <String> unconvertedBiologyHitIdList,List <String> unconvertedBiologyMissIdList) {
        if (!totalContents.keySet().contains(eid)) {
            log.warn("Programming error. Expected to find experiment ID = '${eid}' already stored.")
        }  else {
            SingleEidSummary singleEidSummary = totalContents[eid]
            singleEidSummary.setOutcome(outcome)
            // always one or the other -- is that right?
            for (String singleUnconvertedBiologyHitId in unconvertedBiologyHitIdList){
                singleEidSummary.getUnconvertedBiologyObjects() << Long.parseLong(singleUnconvertedBiologyHitId)
            }
            for (String singleUnconvertedBiologyMissId in unconvertedBiologyMissIdList){
                singleEidSummary.getUnconvertedBiologyObjects() << Long.parseLong(singleUnconvertedBiologyMissId)
            }

        }

    }




    public String createLinkedDataAssaySection (){
        List<Long> experimentIds = totalContents.keySet().toList().sort()
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder << "[\n"
        int numberOfElements =  totalContents.size()
        int loopingCount = 0
        for (Long currentEid in experimentIds) {
            SingleEidSummary singleEidSummary = totalContents[currentEid]
            stringBuilder << "    {\n"
            stringBuilder << "        \"AssayIdx\": ${loopingCount},\n"
            stringBuilder << "        \"AssayName\": \"${singleEidSummary.getAssayName()}\",\n"
            if (singleEidSummary.outcome==2){
                stringBuilder << "        \"AssayAc\": 1,\n"
                stringBuilder << "        \"AssayIn\": 0,\n"
            } else {
                stringBuilder << "        \"AssayAc\": 0,\n"
                stringBuilder << "        \"AssayIn\": 1,\n"
            }
            stringBuilder << "        \"AssayId\": ${singleEidSummary.getAssayCapId()}\n"
            stringBuilder << "    }\n"
            if ((++loopingCount) < numberOfElements) {
                stringBuilder << ","
            }
            stringBuilder << "\n"
        }
        stringBuilder << "]\n"
        return stringBuilder.toString()
    }



    public String createLinkedDataCrossAssaySection (){
        List<Long> experimentIds = totalContents.keySet().toList().sort()
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder << "[\n"
        int numberOfElements =  totalContents.size()
        int loopingCount = 0
        for (Long currentEid in experimentIds) {
            SingleEidSummary singleEidSummary = totalContents[currentEid]
            stringBuilder << "    {\n"
            stringBuilder << "        \"AssayRef\": ${loopingCount},\n"
            stringBuilder << "        \"data\": {\n"
            stringBuilder << "            \"0\" : \"${singleEidSummary.getGoString()}\",\n"
            stringBuilder << "            \"1\" : \"${singleEidSummary.getAssayFormatString()}\",\n"
            stringBuilder << "            \"2\" : \"${singleEidSummary.getAssayTypeString()}\",\n"
            stringBuilder << "            \"3\" : \"${singleEidSummary.getTargetString()}\"\n"
            stringBuilder << "        }"
            stringBuilder << "    }\n"
            if ((++loopingCount) < numberOfElements) {
                stringBuilder << ","
            }
            stringBuilder << "\n"
        }
        stringBuilder << "]\n"
        return stringBuilder.toString()
    }

   public RingNode treeAssayLinker(RingNode ringNode) {
       List<Long> allKeys=this.totalContents*.key.toList().sort()
       // First we make a map from every assay index to its protein class name
       Integer assayIndexCounter = 0
       LinkedHashMap<Integer,String> mapAssayIndexToProteinClassName = [:]
       for (Long singleKey in allKeys) {
           if (this.totalContents[singleKey].proteinTargetClassList.size()>0){
               mapAssayIndexToProteinClassName[assayIndexCounter] =  this.totalContents[singleKey].proteinTargetClassList[0]
           } else {
               mapAssayIndexToProteinClassName[assayIndexCounter] =  "none"
           }
           assayIndexCounter++
       }
       // now invert the list, so that for each unique name we have all the assay counters associated with it
       LinkedHashMap<String,List <Integer>> mapProteinClassNameToAssayIndex = [:]
       for (Integer singleKey in mapAssayIndexToProteinClassName.keySet()) {
           String className = mapAssayIndexToProteinClassName [singleKey]
           if (mapProteinClassNameToAssayIndex.containsKey(className)) {
               mapProteinClassNameToAssayIndex[className] <<  singleKey
           } else {
               mapProteinClassNameToAssayIndex[className] = [singleKey]
           }
       }
       return descendAndLink ( ringNode,mapProteinClassNameToAssayIndex )
   }


    /**
     * Perform a recursive descent. In this case we are forming a list of everybody who is a
     * parent below the given node.
     * @param root
     * @param everyParent
     */
    private void descendAndLink (RingNode root,LinkedHashMap<String,List <Integer>> mapProteinClassNameToAssayIndex )   {
        if (root== null) {
            return
        } else {
            if (mapProteinClassNameToAssayIndex.containsKey(root.name)) {
                root.assays = mapProteinClassNameToAssayIndex[root.name]
            }  else {
                root.assays = []
            }
            for (RingNode oneKid in root.children){
                descendAndLink (oneKid,mapProteinClassNameToAssayIndex)
            }
        }

    }


    public String toString(){
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder << "[\n"
        int numberOfElements =  totalContents.size()
        int loopingCount = 0
        for (Long currentEid in totalContents.keySet()) {
            SingleEidSummary singleEidSummary = totalContents[currentEid]
            stringBuilder << "    {\n"
            stringBuilder << "        \"assayId\": \"${currentEid}\",\n"
            stringBuilder << "        \"data\": {\n"
            stringBuilder << "            \"GO_biological_process_term\" : \"${singleEidSummary.getGoString()}\",\n"
            stringBuilder << "            \"assay_format\" : \"${singleEidSummary.getAssayFormatString()}\",\n"
            stringBuilder << "            \"assay_type\" : \"${singleEidSummary.getAssayTypeString()}\",\n"
            stringBuilder << "            \"protein_target\" : \"${singleEidSummary.getTargetString()}\"\n"
            stringBuilder << "        }"
            stringBuilder << "    }\n"
            if ((++loopingCount) < numberOfElements) {
                stringBuilder << ","
            }
            stringBuilder << "\n"
        }
        stringBuilder << "]\n"
        return stringBuilder.toString()
    }


    public class SingleEidSummary {
        long eid = 0
        int assayFormatIndex = -1
        int assayTypeIndex = -1
        List<Integer> biologicalProcessIndexList = []
        List<Integer>  proteinTargetsIndexList = []
        List<Long> unconvertedBiologyObjects = []
        List <String> proteinTargetClassList = []
        int outcome = 0
        String assayName
        String assayCapId


        public SingleEidSummary(long eid, String assayFormat, String assayType, String assayName,String assayCapId) {
            this.eid =  eid
            this.assayFormatIndex = deriveAssayFormatIndex (assayFormat)
            this.assayTypeIndex = deriveAssayTypeIndex (assayType)
            this.assayName =  assayName
            this.assayCapId = assayCapId
        }


        public String getGoString() {
            String returnValue = 'none'
            if (biologicalProcessIndexList.size() > 0){
                int firstBiologicalProcessIndex = biologicalProcessIndexList [0]
                String biologicalProcess = biologicalProcessMap.find{it.value==firstBiologicalProcessIndex}.key
                if (biologicalProcess != null){
                    returnValue =  biologicalProcess
                }  else {
                    returnValue = 'disappeared'
                }
            }
            returnValue
        }


        public String getTargetString() {
            String returnValue = 'none'
            if (proteinTargetClassList.size() > 0){
                returnValue =  proteinTargetClassList[0]
            }
//            if (proteinTargetsIndexList.size() > 0){
//                int firstProteinTargetsIndex = proteinTargetsIndexList [0]
//                String proteinTarget = proteinTargetMap.find{it.value==firstProteinTargetsIndex}.key
//                if (proteinTarget != null){
//                    returnValue =  proteinTarget
//                }  else {
//                    returnValue = 'disappeared'
//                }
//            }
            returnValue
        }



        public String getAssayFormatString() {
            String returnValue = 'none'
            if (assayFormatIndex > -1){
                String assayFormat = assayFormatMap.find{it.value==assayFormatIndex}.key
                if (assayFormat != null){
                    returnValue =  assayFormat
                }  else {
                    returnValue = 'disappeared'
                }
            }
            returnValue
        }



        public String getAssayTypeString() {
            String returnValue = 'none'
            if (assayTypeIndex > -1){
                String assayType = assayTypeMap.find{it.value==assayTypeIndex}.key
                if (assayType != null){
                    returnValue =  assayType
                }  else {
                    returnValue = 'disappeared'
                }
            }
            returnValue
        }





//        public List<Long>  retrieveBiologyObjects (){
//            unconvertedBiologyObjects
//        }


    }

}
