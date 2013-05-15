package maas

import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Read a file containing mapping between terms that need to be treated as external ontology, and its ID. For example:
 * "dna damage response, signal transduction by p53 class mediator": "0030330"
 *
 * It will be displayed in the UI as a URL
 *
 *
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/13/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
class ExternalTermMapping {
    // As an initial stage, let's expecting there is a file under data/maas/externalTermMapping.txt
    //private static final String MAPPING_FILE_NAME = "data/maas/externalTermMapping.txt"
    private static final String MAPPING_FILE_NAME = ConfigurationHolder.config.data.migration.termmapping.base + 'external_term_mapping.txt'

    private static final Map<String, String> externalTerms

    static Map<String, String> build(String fileName) {
        if (externalTerms)
            return externalTerms
        println("in build external term mapping, read term from ${MAPPING_FILE_NAME}")
        if (StringUtils.isBlank(fileName)) {
           fileName = MAPPING_FILE_NAME
        }

        Map<String, String> externalTerms = [:]
        new File(fileName).eachLine {String line ->
            String[] elements = line.split("\t")
            if (StringUtils.isNotBlank(StringUtils.trim(elements[0]))) {
                externalTerms.put(StringUtils.trim(elements[0]).toLowerCase(), StringUtils.trim(elements[1]))
            }
         }
        return externalTerms
    }
}
