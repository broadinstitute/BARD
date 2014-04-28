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
 * User: ddurkin
 * Date: 9/25/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class Descriptor<T extends Descriptor> {

    private static final int LABEL_MAX_SIZE = 128
    private static final int DESCRIPTION_MAX_SIZE = 1000
    private static final int ABBREVIATION_MAX_SIZE = 20
    private static final int SYNONYMS_MAX_SIZE = 1000
    private static final int EXTERNAL_URL_MAX_SIZE = 1000
    private static final int ELEMENT_STATUS_MAX_SIZE = 20
    private static final int FULL_PATH_MAX_SIZE = 3000


    T parent
    Element element
    ElementStatus elementStatus = ElementStatus.Pending

    String label
    Boolean leaf
    String description
    String fullPath
    String abbreviation

    String synonyms
    String externalURL
    Element unit




    static constraints = {

        parent(nullable: true)
        element()
        elementStatus(nullable: false)

        label(nullable: false, unique: true, maxSize: LABEL_MAX_SIZE)
        leaf()
        description(nullable: true, maxSize: DESCRIPTION_MAX_SIZE)
        fullPath(nullable: true, blank: false, maxSize: FULL_PATH_MAX_SIZE)
        abbreviation(nullable: true, maxSize: ABBREVIATION_MAX_SIZE)

        synonyms(nullable: true, maxSize: SYNONYMS_MAX_SIZE)
        externalURL(nullable: true, maxSize: EXTERNAL_URL_MAX_SIZE)
        unit(nullable: true)
    }
/**
 * the mapping block isn't additive so it needs to be in the subclass to allow specifying the table
 */
//    static mapping = {
//            table('ASSAY_DESCRIPTOR_TREE')
//            id(column: 'NODE_ID', generator: 'assigned')
//            version(false)
//            bardURI(column: 'BARD_URI')
//            externalURL(column: 'EXTERNAL_URL')
//            parent(column: 'PARENT_NODE_ID')
//        }

    /**
     *
     * @return a string representing the ontology path hierarchy in total
     */
    String generateOntologyBreadCrumb() {
        generateOntologyBreadCrumb(path.size())
    }

    /**
     *
     * @param pathLength
     * @return a string representing the ontology path hierarchy to the specified pathLength
     */
    String generateOntologyBreadCrumb(int pathLength) {
        int toIndexExclusive = Math.min(pathLength, path.size())
        path.subList(0, toIndexExclusive).collect { it.label }.join('> ') + '>'
    }


    List<T> getPath() {
        if (parent) {
            parent.getPath() << this
        } else {
            [this]
        }
    }

    List<T> getPath(BardDescriptor root) {
        if (this.equals(root) || parent == null) {
            [this]
        } else {
            parent.getPath(root) << this
        }
    }

}

