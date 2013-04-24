package bard.db.dictionary

import org.grails.datastore.mapping.query.api.Criteria

class BuildElementPathsService {

    final String relationshipType

    public BuildElementPathsService(String relationshipType = "subClassOf") {
        this.relationshipType = relationshipType
    }

    Set<ElementAndFullPath> build(Element element) {
        Set<ElementAndFullPath> elementAndFullPathSet = new HashSet<ElementAndFullPath>()

        Set<ElementHierarchy> elementHierarchySet = buildSetThatMatchRelationship(element.childHierarchies)

        if (elementHierarchySet.size() == 0) {
            ElementAndFullPath elementAndFullPath = new ElementAndFullPath(element: element)
            elementAndFullPathSet.add(elementAndFullPath)
        } else {
            for (ElementHierarchy elementHierarchy : elementHierarchySet) {
                ElementAndFullPath elementAndFullPath = new ElementAndFullPath(element: element)
                elementAndFullPath.path.add(elementHierarchy)

                elementAndFullPathSet.add(elementAndFullPath)

                elementAndFullPathSet.addAll(recursiveBuild(elementAndFullPath))
            }
        }

        return elementAndFullPathSet
    }


    Set<ElementAndFullPath> recursiveBuild(ElementAndFullPath elementAndFullPath) {
        assert elementAndFullPath.path.size() > 0

        Set<ElementAndFullPath> result = new HashSet<ElementAndFullPath>()

        Element currentStartElement = elementAndFullPath.path.get(0).parentElement

        Set<ElementHierarchy> elementHierarchySet = buildSetThatMatchRelationship(currentStartElement.childHierarchies)

        if (elementHierarchySet.size() > 0) {
            Iterator<ElementHierarchy> elementHierarchyIterator = elementHierarchySet.iterator()

            Map<ElementHierarchy,ElementAndFullPath> addToPathMap = new HashMap<ElementHierarchy, ElementAndFullPath>()
            addToPathMap.put(elementHierarchyIterator.next(), elementAndFullPath)

            while (elementHierarchyIterator.hasNext()) {
                ElementAndFullPath copy = elementAndFullPath.copy()
                addToPathMap.put(elementHierarchyIterator.next(), copy)
            }

            for (ElementHierarchy elementHierarchy : addToPathMap.keySet()) {
                ElementAndFullPath currentElementAndFullPath = addToPathMap.get(elementHierarchy)

                currentElementAndFullPath.path.add(0, elementHierarchy)
                result.add(currentElementAndFullPath)

                result.addAll(recursiveBuild(currentElementAndFullPath))
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
}
