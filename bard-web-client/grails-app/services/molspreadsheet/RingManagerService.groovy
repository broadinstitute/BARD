package molspreadsheet

import bard.core.rest.spring.compounds.CompoundSummary
import bard.core.rest.spring.compounds.TargetClassInfo

class RingManagerService {

    String writeRingTree( RingNode ringNode ) {
        StringBuilder stringBuilder = new StringBuilder("var \$data = [")
        if (ringNode)
            stringBuilder << ringNode.toString()
        stringBuilder << "]"
        stringBuilder.toString()
    }


    Boolean classDataExistsForThisCompound (CompoundSummary compoundSummary){
        Boolean returnValue = true
        return returnValue
    }


    RingNode createStub () {
        RingNode.createStubRing ()
    }

    String defineColors ( RingNode ringNode ) {
        StringBuilder stringBuilder = new StringBuilder("")
        int numberOfColors = ringNode.maximumTreeHeight()
        List <String>  everyParent = ringNode.listOfEverybodyWhoIsAParent()
        List <String>  everyUniqueParent =  everyParent.unique().sort()
            stringBuilder << ringNode.deriveColors(685,500,everyUniqueParent,numberOfColors)
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
    public static  RingNode ringNodeFactory ( List<TargetClassInfo> targetClassInfoList ) {
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
                    }
                }

            }
        }
        return  rootRingNode
    }




}
