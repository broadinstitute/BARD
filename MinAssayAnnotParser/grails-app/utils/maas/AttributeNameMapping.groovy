package maas

import org.apache.commons.lang3.StringUtils

/**
 * Create a map for attribute name in spreadsheet to a row in element table
 *
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/13/13
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
class AttributeNameMapping {
    // As an initial stage, let's expecting there is a file under data/maas/attributeNameMapping.txt
    private static final String MAPPING_FILE_NAME = "data/maas/attributeNameMapping.txt"

    static Map<String, String> build(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            fileName = MAPPING_FILE_NAME
        }

        Map<String, String> names = [:]
        new File(fileName).eachLine {String line, int cnt ->
            if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "//")) {     // skip comment field
                String[] elements = line.split("\t")
                names.put(StringUtils.trim(elements[0]), StringUtils.trim(elements[1]))
            }
        }
        return names
    }
}
