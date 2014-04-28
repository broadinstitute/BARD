/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

        builder.append(getParentFullPath()).append(pathDelimeter).append(element.label).append(pathDelimeter)

        return builder.toString()
    }

    String getParentFullPath() {
        StringBuilder builder = new StringBuilder()

        for (ElementHierarchy eh : path) {
            builder.append(pathDelimeter).append(eh.parentElement.label)
        }

        return builder.toString()
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
