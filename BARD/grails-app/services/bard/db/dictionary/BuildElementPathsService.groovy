package bard.db.dictionary

/**
 * Used to convert the ElementHierarchy graph into paths from individual elements to root elements.
 */
class BuildElementPathsService {

    final String relationshipType

    final String pathDelimeter

    /**
     * @param relationshipType    specify the relationshipType to be used when querying ElementHierarchy
     * @param pathDelimeter       delimeter to be used to separate elements within a path string
     */
    public BuildElementPathsService(String relationshipType = "subClassOf", String pathDelimeter = "/") {
        this.relationshipType = relationshipType
        this.pathDelimeter = pathDelimeter
    }

    /**
     * create a list of all the full paths (possible paths through the ElementHierarchy graph) for all the elements in
     * the system, which is sorted by the string representation of the paths.  Also calculate the maximum length of the
     * strings representing those paths
     * @param elementAndFullPathCollection
     * @return
     */
    ElementAndFullPathListAndMaxPathLength createListSortedByString(Collection<ElementAndFullPath> elementAndFullPathCollection) {
        List<ElementAndFullPath> result = new ArrayList<ElementAndFullPath>(elementAndFullPathCollection)

        Collections.sort(result, new ElementAndFullPathComparatorByString())

        int maxPathLength = 0
        int i = 0;
        for (ElementAndFullPath elementAndFullPath : result) {
            elementAndFullPath.index = i

            final int currentLength = elementAndFullPath.toString().length()
            if (currentLength > maxPathLength) {
                maxPathLength = currentLength
            }

            i++
        }

        return new ElementAndFullPathListAndMaxPathLength(result, maxPathLength)
    }

    /**
     * Build all the full paths (possible paths through the ElementHierarchy graph) for all of the elements in the system
     * @return
     * @throws BuildElementPathsServiceLoopInPathException
     */
    Set<ElementAndFullPath> buildAll() throws BuildElementPathsServiceLoopInPathException {
        Set<ElementAndFullPath> result = new HashSet<ElementAndFullPath>()

        //retrieve all element hierarchy to reduce database round trips
        List<ElementHierarchy> elementHierarchyList = ElementHierarchy.findAll()

        List<Element> elementList = Element.findAll()
        for (Element element : elementList) {
            result.addAll(build(element))
        }

        return result
    }

    String buildSinglePath(Element element){
        Set<ElementAndFullPath> paths = build(element)
        return paths?.iterator()?.next()?.toString()
    }


    /**
     * starting with the provided element, work through the element hierarchy's graph to build the paths to the root(s) of
     * the hierarchy graph
     * @param element
     * @return
     * @throws BuildElementPathsServiceLoopInPathException
     */
    Set<ElementAndFullPath> build(Element element) throws BuildElementPathsServiceLoopInPathException {
        Set<ElementAndFullPath> result = new HashSet<ElementAndFullPath>()

        Set<ElementHierarchy> elementHierarchySet = buildSetThatMatchRelationship(element.childHierarchies)

        ElementAndFullPath elementAndFullPath = new ElementAndFullPath(element: element, pathDelimeter: pathDelimeter)
        result.add(elementAndFullPath)

        if (elementHierarchySet.size() > 0) {
            Iterator<ElementHierarchy> iterator = elementHierarchySet.iterator()

            for (int i = 0; i < elementHierarchySet.size() - 1; i++) {
                ElementAndFullPath copy = elementAndFullPath.copy()
                copy.path.add(0, iterator.next())

                result.add(copy)
                result.addAll(recursiveBuild(copy))
            }

            elementAndFullPath.path.add(0, iterator.next())
            result.addAll(recursiveBuild(elementAndFullPath))
        }

        return result
    }

    /**
     * for all the element hierarchies associated with the root of the provided path in elementAndFullPath, build the
     * set ElementAndFullPath that represent all the possible paths to root elements of the element hierarchy graph
     * @param elementAndFullPath
     * @return
     * @throws BuildElementPathsServiceLoopInPathException
     */
    Set<ElementAndFullPath> recursiveBuild(ElementAndFullPath elementAndFullPath) throws BuildElementPathsServiceLoopInPathException {
        assert elementAndFullPath.path.size() > 0

        Set<ElementAndFullPath> result = new HashSet<ElementAndFullPath>()

        Element currentStartElement = elementAndFullPath.path.get(0).parentElement

        Set<ElementHierarchy> elementHierarchySet = buildSetThatMatchRelationship(currentStartElement.childHierarchies)

        if (elementHierarchySet.size() > 0) {
            Iterator<ElementHierarchy> iterator = elementHierarchySet.iterator()

            List<PathAndElementHierarchyPair> pairList = new ArrayList<PathAndElementHierarchyPair>(elementHierarchySet.size())
            pairList.add(new PathAndElementHierarchyPair(elementAndFullPath: elementAndFullPath, elementHierarchy: iterator.next()))

            while (iterator.hasNext()) {
                ElementHierarchy elementHierarchy = iterator.next()

                ElementAndFullPath copy = elementAndFullPath.copy()
                result.add(copy)

                pairList.add(new PathAndElementHierarchyPair(elementAndFullPath: copy, elementHierarchy: elementHierarchy))
            }


            for (PathAndElementHierarchyPair pair : pairList) {
                ElementAndFullPath curPath = pair.elementAndFullPath
                ElementHierarchy elementHierarchy = pair.elementHierarchy

                if (! curPath.pathContainsElement(elementHierarchy.parentElement)) {
                    curPath.path.add(0, elementHierarchy)
                    result.addAll(recursiveBuild(curPath))
                } else {
                    throw new BuildElementPathsServiceLoopInPathException(elementAndFullPath: curPath, nextTopElementHierarchy: elementHierarchy)
                }
            }
        }

        return result
    }

    /**
     * create a new set of ElementHierarchy that only contains ElementHierarchy from the input set that match the
     * relationship type specified for the service (relationshipType member of the class)
     * @param elementHierarchySet
     * @return
     */
    Set<ElementHierarchy> buildSetThatMatchRelationship(Set<ElementHierarchy> elementHierarchySet) {
        Set<ElementHierarchy> result = new HashSet<ElementHierarchy>(elementHierarchySet)

        for (ElementHierarchy elementHierarchy : elementHierarchySet) {
            if (elementHierarchy.relationshipType != relationshipType) {
                result.remove(elementHierarchy)
            }
        }

        return result
    }

    private class PathAndElementHierarchyPair {
        ElementHierarchy elementHierarchy
        ElementAndFullPath elementAndFullPath
    }
}

class BuildElementPathsServiceLoopInPathException extends Exception {
    ElementHierarchy nextTopElementHierarchy
    ElementAndFullPath elementAndFullPath
}

class ElementAndFullPathListAndMaxPathLength {
    final List<ElementAndFullPath> elementAndFullPathList
    final int maxPathLength

    ElementAndFullPathListAndMaxPathLength(List<ElementAndFullPath> elementAndFullPathList, int maxPathLength) {
        this.elementAndFullPathList = elementAndFullPathList
        this.maxPathLength = maxPathLength
    }
}
