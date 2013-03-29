package molspreadsheet

class RingManagerService {

    String writeRingTree( RingNode ringNode ) {
        StringBuilder stringBuilder = new StringBuilder("var \$data = [")
        if (ringNode)
            stringBuilder << ringNode.toString()
        stringBuilder << "]"
        stringBuilder.toString()
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

}
