package bard.db.dictionary

class BuildElementPathsService {

    final String relationshipType

    final String pathDelimeter

    public BuildElementPathsService(String relationshipType = "subClassOf", String pathDelimeter = "/") {
        this.relationshipType = relationshipType
        this.pathDelimeter = pathDelimeter
    }

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
