package maas

import org.apache.commons.lang3.StringUtils
import bard.db.dictionary.Element

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
    private static final String MAPPING_FILE_NAME = "data/maas/element_id_mapping.txt"

    static Map<String, String> build(String fileName) {
        println("in build element_id_mapping")
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
