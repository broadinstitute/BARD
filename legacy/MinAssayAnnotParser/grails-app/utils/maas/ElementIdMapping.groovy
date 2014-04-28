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

package maas

import org.apache.commons.lang3.StringUtils
import bard.db.dictionary.Element
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 5/3/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
class ElementIdMapping {
    // By default, put file under data/maas, it is a tab delimited file with vocabulary used in spreadsheet and mapped id in element table
    // This is a way to handle if terms used to annotate does not match terms defined in element table
   // private static final String MAPPING_FILE_NAME = "data/maas/element-id-mapping.txt"
    private static final String MAPPING_FILE_NAME = ConfigurationHolder.config.data.migration.termmapping.base + 'element-id-mapping.txt'

    private static final Map<String, String> elementIdMapping

    static Map<String, String> build(String fileName) {
        if (elementIdMapping)
            return elementIdMapping
        println("in build element-id-mapping, read from ${MAPPING_FILE_NAME}")
        if (StringUtils.isBlank(fileName)) {
            fileName = MAPPING_FILE_NAME
        }

        Map<String, String> names = [:]
        new File(fileName).eachLine {String line, int cnt ->
            if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "//")) {     // skip comment field
                String[] elements = line.split("\t")
                def elementIdInDict = StringUtils.trim(elements[1])
                def foundElement = Element.findById(elementIdInDict)
                if (foundElement)
                    names.put(StringUtils.trim(elements[0]), foundElement.label)
                else
                    names.put(StringUtils.trim(elements[0]), StringUtils.trim(elements[0]))   // if not found, keep original, throw errors later
            }
        }
        return names
    }
}
