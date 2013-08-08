package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 4/22/13
 * Time: 11:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ElementAndFullPath {
    private pathDelimeter

    Element element

    Integer index

    /**
     * path from root of hierarchy to element
     */
    List<ElementHierarchy> path

    public ElementAndFullPath(Element element, String pathDelimeter = "/") {
        this.element = element
        this.pathDelimeter = pathDelimeter

        path = new LinkedList<ElementHierarchy>()
    }

    public ElementAndFullPath copy() {
        ElementAndFullPath copy = new ElementAndFullPath(element, pathDelimeter)

        copy.path.addAll(path)

        return copy
    }

    /**
     * check the ElementHierarchy objects in the path to see if any of them have the provided Element as
     * either a parentElement or childElement
     * @param pathElement
     * @return
     */
    public boolean pathContainsElement(Element pathElement) {
        return elementHierarchyCollectionContainsElement(path, pathElement)
    }

    public static boolean elementHierarchyCollectionContainsElement(Collection<ElementHierarchy> elementHierarchyCollection,
                                                                    Element element) {
        for (ElementHierarchy elementHierarchy : elementHierarchyCollection) {
            if (elementHierarchy.childElement == element || elementHierarchy.parentElement == element) {
                return true
            }
        }

        return false
    }

    /**
     * Path begins and end with the delimeter
     * @return
     */
    @Override
    String toString() {
        StringBuilder builder = new StringBuilder()

        for (ElementHierarchy eh : path) {
            builder.append(pathDelimeter).append(eh.parentElement.label)
        }
        builder.append(pathDelimeter).append(element.label).append(pathDelimeter)
    }

    /**
     * from toString() above:  starts and ends with pathDelimeter, so this method will ignore entries from split
     * before the first pathDelimeter.  If the path ends with the pathDelimeter, there will be no entries after the
     * last delimeter from the split
     * @param pathString
     * @param pathDelimeter
     * @return
     */
    static List<String> splitPathIntoTokens(String pathString, String pathDelimeter) throws InvalidElementPathStringException {
        String pathStringTrim = pathString.trim()
        if (pathStringTrim.startsWith(pathDelimeter) && pathStringTrim.endsWith(pathDelimeter)) {
            String[] splitPath = pathStringTrim.split(pathDelimeter)

            List<String> result = new LinkedList<String>()

            for (int i = 1; i < splitPath.length; i++) {
                result.add(splitPath[i].trim())
            }

            return result
        } else {
            throw new InvalidElementPathStringException()
        }
    }
}

class InvalidElementPathStringException extends Exception {}