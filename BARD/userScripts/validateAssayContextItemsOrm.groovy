import bard.db.registration.Assay
import bard.db.registration.AssayContextItem
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Created with IntelliJ IDEA.
 * User: ycruz
 * Date: 6/24/13
 * Time: 9:01 AM
 * To change this template use File | Settings | File Templates.
 */

SpringSecurityUtils.reauthenticate('integrationTestUser', null)

println("Initializing output file...")
String outputFile =  "assay_context_items_validation.txt"
FileWriter fileWriter = new FileWriter(outputFile, false)

writeToFile = {message ->
    fileWriter.write(message + "\n")
    fileWriter.flush()
}

println("Retrieving AssayContextItems...")

List<AssayContextItem> assayContextItems = AssayContextItem.list()
println("Ready to process ${assayContextItems.size()} of Assay Context Items in BARD")
writeToFile("# of Assay Context Items in BARD: ${assayContextItems.size()}")
writeToFile("List of AssayContextItems that don't pass validation: \n")
writeToFile("Assay ID\tAssayContextItem ID\tError(s)")

int itemsWithErrors = 0;
println("Detecting AssayContextItems with errors...")
assayContextItems.each { AssayContextItem item ->
    if (!item.validate()) {
        itemsWithErrors++
        item.errors.allErrors.each{
            writeToFile("${item.assayContext.assayId}\t${item.id}\t${it.getCode()}")
        }
    }
}
writeToFile("# of AssayContextItems with Errors: ${itemsWithErrors}")
println("Processing has finished. Output file: " + outputFile)
println("# of AssayContextItems with Errors: ${itemsWithErrors}")
fileWriter.close()