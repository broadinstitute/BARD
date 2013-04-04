package bard.core.rest.spring.util

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 4/3/13
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
class RingNode {

    String name = ""
    int size =  0
    List <RingNode> children = []
    //
    String ID = ""
    String description = ""
    String levelIdentifier = ""
    String source = ""


    public RingNode(String name,
                    String ID,
                    String description,
                    String levelIdentifier,
                    String source,
                    int size = 0) {
        this(name, size)
        this.ID = ID
        this.description = description
        this.levelIdentifier = levelIdentifier
        this.source = source
    }

    public RingNode( String name, int size = 0  ){
        this.name = name
        this.size = size
    }

    public RingNode( String name, List <RingNode> children  ){
        this.name = name
        this.size = 0
        this.children = children
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        RingNode ringNode = (RingNode) o

        if (ID != ringNode.ID) return false
        if (description != ringNode.description) return false
        if (levelIdentifier != ringNode.levelIdentifier) return false
        if (name != ringNode.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode(): 0)
        result = 31 * result + (ID != null ? ID.hashCode(): 0)
        result = 31 * result + (description != null ? description.hashCode(): 0)
        result = 31 * result + (levelIdentifier != null ? levelIdentifier.hashCode(): 0)
        return result
    }

    public int numberOfLevels () {
        int returnValue = 0
        if (levelIdentifier != null) {
            List <String> listOfGroups = levelIdentifier.split(/\./)
            for ( String oneGroup in listOfGroups) {
                if (oneGroup != "00"){
                    returnValue++
                }
            }
        }
        return returnValue
    }


    public String shortenedIdentifierString () {
        StringBuilder stringBuilder = new StringBuilder()
        if (levelIdentifier != null) {
            List <String> listOfGroups = levelIdentifier.split(/\./)
            for ( String oneGroup in listOfGroups) {
                if (oneGroup != "00"){
                    stringBuilder << "${oneGroup}.".toString()
                }
            }
        }
        return returnValue
    }



    public List <String> listOfEverybodyWhoIsAParent ( ) {
        List <String>  everyParent = []
        getParentsBelow ( this, everyParent )
        everyParent
    }

    private void getParentsBelow (RingNode root, List <String>  everyParent )   {
        if ((root.children == null) || (root.children.size() == 0)) {
            return
        } else {
            everyParent << root.name
            for (RingNode oneKid in root.children){
                getParentsBelow (oneKid, everyParent)
            }
        }

    }




    public int maximumTreeHeight ( ) {
        int currentTreeLevel = 0
        int highestTreeLevelSoFar = 0
        return goDownOneLevel ( this,  currentTreeLevel,  highestTreeLevelSoFar)
    }


    private int goDownOneLevel (RingNode root, int currentTreeLevel, int highestTreeLevelSoFar)   {
        if ((root.children == null) || (root.children.size() == 0)) {
            return  highestTreeLevelSoFar
        } else {
            int digDeeper = currentTreeLevel+1
            int deepestLevel = highestTreeLevelSoFar
            int deepestLevelBelow
            if (digDeeper > highestTreeLevelSoFar)
                deepestLevel = digDeeper
            for (RingNode oneKid in root.children){
                deepestLevelBelow = goDownOneLevel (oneKid, digDeeper, deepestLevel)
                deepestLevel = ((deepestLevelBelow > deepestLevel) ?  deepestLevelBelow :  deepestLevel)
            }
            return deepestLevel
        }

    }



    static  RingNode createStubRing () {
        RingNode ringNode1 = new RingNode ("B",[ new RingNode ("A",  1500),
                new RingNode ("ABC",  500),
                new RingNode ("C",  500) ] )
        RingNode ringNode2 = new RingNode ("A",[ new RingNode ("ABC",  1500),
                ringNode1,
                new RingNode ("C",  50) ] )
        return  new RingNode ("AA",[ new RingNode ("FLINA",  1500),
                ringNode1,
                new RingNode ("FLINC",  1500),
                ringNode2] )
    }




    public String deriveColors(int width, int height, List <String> namesThatGetColors, int maximumNumberOfColors) {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder << """  var width = ${width},
                height = ${height},
                radius = Math.min(width, height) / 2,
                color = d3.scale.category10().domain([""".toString()
        int numberOfLoops = 0
        for (String name in namesThatGetColors){
            stringBuilder << "\"${name}\""
            ++numberOfLoops
            if ((numberOfLoops < maximumNumberOfColors)&&(numberOfLoops<  namesThatGetColors.size())){
                stringBuilder << ",\n"
            } else  {
                stringBuilder << "\n"
                break
            }
        }
        stringBuilder << "   ]);"
        stringBuilder.toString()
    }



    public String writeHierarchyPath ( LinkedHashMap<String,RingNode> ringNodeMgr ) {
        StringBuilder stringBuilder = new StringBuilder()
        describeMeAndMyParent (this,ringNodeMgr, stringBuilder)
        stringBuilder.toString()
    }

    public void describeMeAndMyParent ( RingNode me, LinkedHashMap<String,RingNode> ringNodeMgr, StringBuilder stringBuilder ) {
        RingNode myParent = null
        // find my parent, if I have one
        for (RingNode oneRingNode in ringNodeMgr.values()) {
            if (oneRingNode.children.contains(me)) {
                myParent = oneRingNode
                break
            }
        }
        if (myParent == null) { // we are at the root
            stringBuilder << me.name
        } else {  // keep going up
            describeMeAndMyParent(myParent,ringNodeMgr,stringBuilder)
            stringBuilder << "${me.name}\\".toString()
        }
    }




    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder()
        // start things out
        stringBuilder << "{"
        // start things out, and write the name
        stringBuilder <<  "\"name\":"
        if (name?.size()>0)
            stringBuilder <<  "\"${name}\""
        // size or children – not both
        if ( children.size()>0 ) {
            stringBuilder <<  ", \"children\": [\n"
            int totalNumberOfKids =  children.size()
            int workingOnKidNumber = 0
            for (RingNode oneKid in children) {
                stringBuilder << oneKid.toString()
                workingOnKidNumber++
                if (workingOnKidNumber < totalNumberOfKids)
                    stringBuilder <<  ","
                stringBuilder << "\n"
            }
            stringBuilder << "]"
        }  else if (size  > 0)  {
            stringBuilder <<  ", "
            stringBuilder <<  "\"size\":"
            stringBuilder << size
        }
        stringBuilder << "}"
        return stringBuilder.toString()
    }
}
