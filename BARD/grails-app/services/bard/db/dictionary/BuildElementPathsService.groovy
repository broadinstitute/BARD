package bard.db.dictionary

import org.apache.commons.lang.StringUtils

/**
 * Used to convert the ElementHierarchy graph into paths from individual elements to root elements.
 */
class BuildElementPathsService {

    final String relationshipType

    final String pathDelimeter

    final boolean includeRetiredElements

    final static Long bardDictionaryElementId = 893

    Set<ElementHierarchy> dictionaryHierarchiesToExclude

    Set<Element> dictionarySet

    /**
     * @param relationshipType    specify the relationshipType to be used when querying ElementHierarchy
     * @param pathDelimeter       delimeter to be used to separate elements within a path string
     * @param includeRetiredElements    flag indicating whether the paths should be built including elements that
     *                                  have status of Retired
     */
    public BuildElementPathsService(String relationshipType = "subClassOf", String pathDelimeter = "/",
                                    boolean includeRetiredElements = false) {
        this.relationshipType = relationshipType
        this.pathDelimeter = pathDelimeter
        this.includeRetiredElements = includeRetiredElements

        dictionarySet = null
        dictionaryHierarchiesToExclude = null
    }

    void initializeDictionarySetsIfNeeded() {
        if (null == dictionarySet) {
            Element dictionaryRoot = Element.findById(bardDictionaryElementId)
            if (dictionaryRoot) {
                //retrieve all element hierarchy to reduce database round trips
                ElementHierarchy.findAll()

                dictionarySet = findDictionaries(dictionaryRoot)
                dictionaryHierarchiesToExclude = findDictionaryHierarchiesToExclude(dictionaryRoot, dictionarySet)
            } else {
                dictionarySet = new HashSet<Element>()
                dictionaryHierarchiesToExclude = new HashSet<ElementHierarchy>()
            }
        }
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
        ElementHierarchy.findAll()
        initializeDictionarySetsIfNeeded()

        List<Element> elementList = Element.findAll()
        for (Element element : elementList) {
            Set<ElementAndFullPath> elementAndFullPathSet = build(element)

            //if this is an element of dictionary
            boolean isDictionaryChild = false
            for (ElementHierarchy childHierarchy : element.childHierarchies) {
                isDictionaryChild = isDictionaryChild || dictionarySet.contains(childHierarchy.parentElement)
            }
            if (isDictionaryChild) {
                removeInvalidDictionaryPaths(elementAndFullPathSet)
            }

            result.addAll(elementAndFullPathSet)
        }

        if (! includeRetiredElements) {
            Iterator<ElementAndFullPath> iter = result.iterator()
            while (iter.hasNext()) {
                ElementAndFullPath elementAndFullPath = iter.next()

                if (elementAndFullPath.element.elementStatus.equals(ElementStatus.Retired)) {
                    iter.remove()
                } else {
                    for (ElementHierarchy eh : elementAndFullPath.path) {
                        if (eh.parentElement.elementStatus.equals(ElementStatus.Retired)) {
                            iter.remove()
                            break
                        }
                    }
                }
            }
        }

        return result
    }

    /**
     * Display all of the paths as a csv String
     *
     * Note that, we remove the root nodes from the message
     * @param element
     * @return
     */
    String buildSinglePath(final Element element){
        initializeDictionarySetsIfNeeded()

        Set<ElementAndFullPath> paths = build(element)
        List<String> pathMessage = []
        for(ElementAndFullPath elementAndFullPath: paths){
            String pathAsString = elementAndFullPath.toString()
            if(pathAsString.startsWith("/BARD/")){  //remove the root node. This method should change as soon as we start using trees other than the BARD tree
                pathAsString = pathAsString.replaceFirst("/BARD/", "/")
                pathMessage.add(pathAsString)
            }

        }
        if(!pathMessage.isEmpty()) {
            return StringUtils.join(pathMessage,",")
        }
        return ""
    }


    /**
     * starting with the provided element, work through the element hierarchy's graph to build the paths to the root(s) of
     * the hierarchy graph
     * @param element
     * @return
     * @throws BuildElementPathsServiceLoopInPathException
     */
    Set<ElementAndFullPath> build(Element element)
            throws BuildElementPathsServiceLoopInPathException {

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


    void removeInvalidDictionaryPaths(Set<ElementAndFullPath> elementAndFullPathSet) {
        Iterator<ElementAndFullPath> iterator = elementAndFullPathSet.iterator()

        while (iterator.hasNext()) {
            ElementAndFullPath elementAndFullPath = iterator.next()

            ListIterator<ElementHierarchy> elementHierarchyIterator = elementAndFullPath.path.listIterator(elementAndFullPath.path.size())
            boolean found = false
            while (! found && elementHierarchyIterator.hasPrevious()) {
                if (dictionaryHierarchiesToExclude.contains(elementHierarchyIterator.previous())) {
                    iterator.remove()
                    found = true
                }
            }
        }
    }

    static Set<ElementHierarchy> findDictionaryHierarchiesToExclude(Element dictionaryRoot, Set<Element> dictionaries) {
        Set<ElementHierarchy> result = new HashSet<ElementHierarchy>()

//        result.addAll(ElementHierarchy.withCriteria({
//            inList("childElement", dictionaries)
//            not {
//                eq("parentElement", dictionaryRoot)
//            }
//        }))

        for (Element dictionary : dictionaries) {
            for (ElementHierarchy childHierarchy : dictionary.childHierarchies) {
                if (! childHierarchy.parentElement.equals(dictionaryRoot)) {
                    result.add(childHierarchy)
                }
            }
        }


        return result
    }

    static Set<Element> findDictionaries(Element dictionaryRoot) {
        Set<Element> result = new HashSet<Element>()

        for (ElementHierarchy dictionaryHierarchy : dictionaryRoot.parentHierarchies) {
            result.add(dictionaryHierarchy.childElement)
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

class DictionariesAndCores {
    final List<ElementAndFullPathListAndMaxPathLength> dictionaryList
    final ElementAndFullPathListAndMaxPathLength core

    DictionariesAndCores() {
        dictionaryList = new LinkedList<ElementAndFullPathListAndMaxPathLength>()
    }
}

class ElementAndFullPathListAndMaxPathLength {
    final List<ElementAndFullPath> elementAndFullPathList
    final int maxPathLength

    ElementAndFullPathListAndMaxPathLength(List<ElementAndFullPath> elementAndFullPathList, int maxPathLength) {
        this.elementAndFullPathList = elementAndFullPathList
        this.maxPathLength = maxPathLength
    }
}
