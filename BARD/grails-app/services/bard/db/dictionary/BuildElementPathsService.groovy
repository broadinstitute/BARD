package bard.db.dictionary

import org.grails.datastore.mapping.query.api.Criteria

class BuildElementPathsService {

    final String relationshipType

    public BuildElementPathsService(String relationshipType = "subClassOf") {
        this.relationshipType = relationshipType
    }

    Set<ElementAndFullPath> buildAll() {
        Set<ElementAndFullPath> result = new HashSet<ElementAndFullPath>()

        List<Element> elementList = Element.findAll()
        for (Element element : elementList) {
            result.addAll(build(element))
        }

        return result
    }

    Set<ElementAndFullPath> build(Element element) {
        Set<ElementAndFullPath> result = new HashSet<ElementAndFullPath>()

        Set<ElementHierarchy> elementHierarchySet = buildSetThatMatchRelationship(element.childHierarchies)

        ElementAndFullPath elementAndFullPath = new ElementAndFullPath(element: element)
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


    Set<ElementAndFullPath> recursiveBuild(ElementAndFullPath elementAndFullPath) {
        assert elementAndFullPath.path.size() > 0

        Set<ElementAndFullPath> result = new HashSet<ElementAndFullPath>()

        Element currentStartElement = elementAndFullPath.path.get(0).parentElement

        Set<ElementHierarchy> elementHierarchySet = buildSetThatMatchRelationship(currentStartElement.childHierarchies)

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
