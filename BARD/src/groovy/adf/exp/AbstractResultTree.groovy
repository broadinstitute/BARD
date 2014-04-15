package adf.exp

import bard.db.experiment.JsonResult
import bard.db.experiment.JsonSubstanceResults

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/8/14
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
class AbstractResultTree {

    Map<Path, Node> pathNodeMap = [:]

    public AbstractResultTree() {
        pathNodeMap[null] = new Node(path: null, groupWithParent: false, contextItems: [] as Set, children: [] as Set);
    }

    public void update(JsonSubstanceResults results) {
        updateCollection(null, results.rootElem);
    }

    protected void updateCollection(Path path, List<JsonResult> results) {
        def (singletons, multivalued) = ResultSetPipeline.findSingletons(results)

        def updateChildren = { children, isSingleton ->
            for(child in children) {
                Path childPath = new Path(child, path);
                update(childPath, child, isSingleton)
            }
        }

        updateChildren(singletons, true)
        updateChildren(multivalued, false)

    }

    protected void update(Path path, JsonResult result, boolean groupWithParent) {
        Node node = pathNodeMap[path]
        if (node == null) {
            node = new Node(path: path, groupWithParent: groupWithParent, contextItems: [] as Set, children: [] as Set);
            pathNodeMap[path] = node;

            if(path != null) {
                pathNodeMap[path.parent].children.add(node)
            }
        }

        node.contextItems.addAll( result.contextItems.collect { new ResultKey(it)  } )

        if(!groupWithParent)
            node.groupWithParent = false;

        updateCollection(path, result.related)
    }

    static class Node {
        Path path;
        boolean groupWithParent;
        Set<ResultKey> contextItems;
        Set<Node> children;

        public List transitiveGrouped() {
            List<Node> groupedTogether = []
            List<Node> otherBoxes = []

            children.each {
                if(it.groupWithParent) {
                    groupedTogether.add(it)
                    def (childGrouped, childBoxes) = it.transitiveGrouped()
                    groupedTogether.addAll(childGrouped)
                    otherBoxes.addAll(childBoxes)
                } else {
                    otherBoxes.add(it)
                }
            }

            return [groupedTogether, otherBoxes]
        }

        public String toString() {
            return "Node(g=${groupWithParent}, path=${path}, children=${children})"
        }
    }

    // given a tree, construct boxes
    public Map<Path, Box> constructBoxes() {
        Map<Path, Box> boxByPath = [:]

        List<Path> pendingPaths = [null]
        while(pendingPaths.size() > 0) {
            Path path = pendingPaths.remove(0);
            def (box, otherPaths) = constructBox(path);
            boxByPath[path] = box;
            pendingPaths.addAll(otherPaths.collect{ it.path });
        }

        return boxByPath;
    }

    // returns a list containing [box, list of other box root paths]
    private List constructBox(Path path) {
        Node node = pathNodeMap[path]
        def (groupedTogether, otherBoxes) = node.transitiveGrouped()

        // include the current node if its not the root
        def nodesInBox = new ArrayList(groupedTogether)
        if(path != null) {
            nodesInBox.add(node)
        }

        def columns = nodesInBox.collect { it.path.result }
        def ctxColumns = nodesInBox.collectMany { it.contextItems  }
        Box box = new Box(columns, ctxColumns)

        return [box, otherBoxes];
    }

    public String toString() {
        Node node = pathNodeMap.get(null)
        return node.toString()
    }
}
