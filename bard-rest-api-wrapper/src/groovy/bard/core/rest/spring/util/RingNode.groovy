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
    int size =  1
    List <RingNode> children = []
    List <String> actives = []
    List <String> inactives = []
    //
    String ID = ""
    String description = ""
    String levelIdentifier = ""
    String source = ""

    /**
     * The RingNode when we use it to build the tree.  Later, when we are using  RingNode
     * only to hold a tree for the Sunburst, we don't need all these other fields ( IDE,
     * descriptor, etc.).
     * @param name
     * @param ID
     * @param description
     * @param levelIdentifier
     * @param source
     * @param size
     */
    public RingNode(String name,
                    String ID,
                    String description,
                    String levelIdentifier,
                    String source,
                    int size = 1) {
        this(name, size)
        this.ID = ID
        this.description = description
        this.levelIdentifier = levelIdentifier
        this.source = source
    }

    /**
     * The constructor when our goal is to build a sunburst.  The size is an optional
     * parameter ( set it to zero if it isn't supplied).  Eventually we use this to
     * describe how big each arc should be.
     * @param name
     * @param size
     */
    public RingNode( String name, int size = 1  ){
        this.name = name
        this.size = size
    }

    /**
     * When building a  RingNode structure statically it can be handy to assign the children
     * using this constructor, though when building the Sunburst dynamically the children are
     * assigned at runtime and without the use of the 'children' parameter.
     * @param name
     * @param children
     */
    public RingNode( String name, List <RingNode> children  ){
        this.name = name
        this.size = 1
        this.children = children
    }

    /**
     * Need to determine whether or not RingNodes already exist in a collection
     * @param o
     * @return
     */
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

    /**
     * Need to determine whether or not RingNodes already exist in a collection
     * @return
     */
    int hashCode() {
        int result
        result = (name != null ? name.hashCode(): 0)
        result = 31 * result + (ID != null ? ID.hashCode(): 0)
        result = 31 * result + (description != null ? description.hashCode(): 0)
        result = 31 * result + (levelIdentifier != null ? levelIdentifier.hashCode(): 0)
        return result
    }

    /**
     * We can analyze the Panther levelIdentifier and figure out how many levels down
     * a particular node is located in the tree.
     * @return
     */
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

    /**
     * This method serves as the launcher for  getParentsBelow, which undertakes the actual recursive descent.
     * Why do we need this routine at all?  The reason is this:  there is a point in in the RingManagerService
     * where we want to start from a given node and walk up to the root of the tree. However, our tree only has
     * has pointers going from parent to child, not the other way around. Therefore the current method
     * (listOfEverybodyWhoIsAParent) allows to find out the parent of the node, which would otherwise be
     * a more complicated operation. Note:  in an expanded tree this method would be very inefficient.  Currently
     * our trees (in terms of depth) are very small, so the approach taken here is fine
     * @return
     */
    public List <String> listOfEverybodyWhoIsAParent ( ) {
        List <String>  everyParent = []
        getParentsBelow ( this, everyParent )
        everyParent
    }

    /**
     * Perform a recursive descent. In this case we are forming a list of everybody who is a
     * parent below the given node.
     * @param root
     * @param everyParent
     */
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

    /**
     * Note that this number is zero based. This method is the launcher for the method  goDownOneLevel,
     * which performs the actual recursive descent
     * @return
     */
    public int maximumTreeHeight ( ) {
        int currentTreeLevel = 0
        int highestTreeLevelSoFar = 0
        return goDownOneLevel ( this,  currentTreeLevel,  highestTreeLevelSoFar)
    }

    /**
     * Perform a recursive descent.  Were only trying to size up the tree in this routine
     * @param root
     * @param currentTreeLevel
     * @param highestTreeLevelSoFar
     * @return
     */
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






    /**
     * Note that this number is zero based. This method is the launcher for the method  goDownOneLevel,
     * which performs the actual recursive descent
     * @return
     */
    public LinkedHashMap<String, String> determineColorMappingRange ( ) {
        final BigDecimal topOfMaximumRange = 1
        final BigDecimal bottomOfMinimumRange = 0
        BigDecimal minimumSoFar = topOfMaximumRange
        BigDecimal maximumSoFar = bottomOfMinimumRange
        LinkedHashMap<String, BigDecimal> valueForPassing = [minimumSoFar:minimumSoFar, maximumSoFar:maximumSoFar]
        goDownOneLevel ( this,  valueForPassing)
        // test for degenerate case  -- if we never received any actives or in actives then reset our values to something sensible
        if ((valueForPassing["minimumSoFar"] == topOfMaximumRange)  && (valueForPassing["maximumSoFar"] == bottomOfMinimumRange)){
            valueForPassing["minimumSoFar"] = bottomOfMinimumRange
            valueForPassing["maximumSoFar"] = bottomOfMinimumRange
        }
        return [minimumValue:valueForPassing["minimumSoFar"].toString(),maximumValue:valueForPassing["maximumSoFar"].toString()]
    }

    /**
     * Perform a recursive descent.  Were only trying to size up the tree in this routine
     * @param root
     * @param currentTreeLevel
     * @param highestTreeLevelSoFar
     * @return
     */
    private void goDownOneLevel (RingNode root, LinkedHashMap<String, BigDecimal> valueForPassing )   {
        BigDecimal minimumSoFar = 1
        BigDecimal maximumSoFar = 0
        if ((root.children == null) || (root.children.size() == 0)) {
            return
        } else {
            if ((root.actives.size()+root.inactives.size()) > 0 ) {
                BigDecimal comparisonNumber =  root.actives.size()/((root.actives.size()+root.inactives.size()))
                if (comparisonNumber>valueForPassing["maximumSoFar"])
                    valueForPassing["maximumSoFar"] =  comparisonNumber
                if (comparisonNumber < valueForPassing["minimumSoFar"])
                    valueForPassing["minimumSoFar"] =  comparisonNumber
            }
            for (RingNode oneKid in root.children){
               goDownOneLevel (oneKid, valueForPassing)
            }
            return
        }

    }


















    /**
     * Useful for testing
     * @return
     */
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

    /**
     * Write out the JavaScript code that assigns particular colors to particular elements.
     * This function will be expanded as the coloring options increase.
     * @param width
     * @param height
     * @param namesThatGetColors
     * @param maximumNumberOfColors
     * @return
     */
    public String placeSunburstOnPage(int width, int height, List <String> namesThatGetColors, int maximumNumberOfColors, int typeOfColoring ) {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder << """
        <div id="sunburstdiv">
        <script>
                createASunburst( ${width}, ${height},5,1000,continuousColorScale,'div#sunburstdiv');
        </script>

        </div>"""

                stringBuilder <<  handleColors()
        stringBuilder.toString()
    }


    public String handleColors(){
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder << """
                    var s = d3.scale.linear()
                            .domain([0,1])
                            .interpolate(d3.interpolateRgb)
                            .range(["#ff0000", "#00ff00"])
                        """.toString()
        stringBuilder.toString()

    }
//    public String handleColors(){
//        StringBuilder stringBuilder = new StringBuilder()
//        stringBuilder << """
//                    function colorArcFill( d ) {
//                        return colorByActivity(d)
//                    }
//
//                    var s = d3.scale.linear()
//                            .domain([0,1])
//                            .interpolate(d3.interpolateRgb)
//                            .range(["#ff0000", "#00ff00"])
//
//                    function colorByActivity(d){
//                        var returnValue = new String ();
//                        if (d.ac!=undefined) {
//                            var actives = parseInt(d.ac);
//                            var inactives = parseInt(d.inac);
//                            var prop = actives / (actives +inactives) ;
//                            returnValue = s(prop);
//                        } else {
//                            returnValue = "#FF00FF";
//                        }
//                        return returnValue;
//                    }""".toString()
//        stringBuilder.toString()
//
//    }


    public String deriveColors(List <String> namesThatGetColors, int maximumNumberOfColors) {
        StringBuilder stringBuilder = new StringBuilder()
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
        stringBuilder.toString()
    }

    /**
     * This routine is used to generate a string describing the position of an
     * element in the ring node tree.  It isn't used on data display at all, but
     * is used instead when the data is loaded from the backend.  The idea is that the
     * current node (this) is the one where testing, and that it represents a node in
     * any linked tree data structure. To make the process easier ( and because we already
     * had this data structure as a byproduct of previous work) we pass in a map where
     * the key is the nonzero portion of the levelIdentifier, and the value is a pointer
     * into the tree.  The result of this call is to write a string the traces backward
     * from the current node up to the root of the tree, using a recursive ascent.
     * @param ringNodeMgr
     * @return
     */
    public String writeHierarchyPath ( LinkedHashMap<String,RingNode> ringNodeMgr ) {
        StringBuilder stringBuilder = new StringBuilder()
        describeMeAndMyParent (this,ringNodeMgr, stringBuilder)
        stringBuilder.toString()
    }

    /**
     * Here's the routine does the recursive descent.  It fills up a stringBuilder
     * object as it goes.
     * @param me
     * @param ringNodeMgr
     * @param stringBuilder
     */
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




    /**
     *  Important note: this toString() method is intended to write out the contents
     *  of a tree into JavaScript for the purposes of building the sunburst  pattern
     *  using the D3 library.  As such this routine uses a recursive descent to step
     *  through all of the elements that are below the current node in the tree.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder()
        // start things out
        stringBuilder << "{"
        // start things out, and write the name
        stringBuilder <<  "\"name\":\"${name}\""
        stringBuilder <<  ", \"ac\":\"${actives.size()}\""
        stringBuilder <<  ", \"inac\":\"${inactives.size()}\""
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

    public String toStringNoText() {
        StringBuilder stringBuilder = new StringBuilder()
        // start things out
        stringBuilder << "{"
        // start things out, and write the name
        stringBuilder <<  "\"name\": \"\""
        // size or children – not both
        if ( children.size()>0 ) {
            stringBuilder <<  ", \"children\": [\n"
            int totalNumberOfKids =  children.size()
            int workingOnKidNumber = 0
            for (RingNode oneKid in children) {
                stringBuilder << oneKid.toStringNoText()
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
