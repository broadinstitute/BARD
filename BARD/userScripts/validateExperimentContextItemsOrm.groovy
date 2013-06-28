import bard.db.experiment.ExperimentContextItem
import bard.db.registration.AssayContextItem
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Created with IntelliJ IDEA.
 * User: ycruz
 * Date: 6/26/13
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */

SpringSecurityUtils.reauthenticate('integrationTestUser', null)

println("Initializing output file...")
String dirLocation = "C:\\BardData"
File dir = new File(dirLocation)
if(!dir.exists()){
    if(dir.mkdir())
        println("Output directory created")
    else
        println("Failed to create directory for output file")
}
String outputFile =  dirLocation + "/experiment_context_items_validation.txt"
FileWriter fileWriter = new FileWriter(outputFile, false)
fileWriter.write("List of ExperimentContextItems that don't pass validation: \n\n")
fileWriter.flush()
writeToFile = {message ->
    fileWriter.write(message + "\n")
    fileWriter.flush()
}

println("Retrieving ExperimentContextItems...")

List<ExperimentContextItem> experimentContextItems = ExperimentContextItem.list()
println("Ready to process ${experimentContextItems.size()} of Experiment Context Items in BARD")
writeToFile("# of Experiment Context Items in BARD: ${experimentContextItems.size()}")
writeToFile("List of ExperimentContextItems that don't pass validation: \n")
writeToFile("Experiment ID\tExperimentContextItem ID\tError(s)")

int itemsWithErrors = 0;
println("Detecting ExperimentContextItems with errors...")
experimentContextItems.each { ExperimentContextItem item ->
    if (!item.validate()) {
        itemsWithErrors++
        item.errors.allErrors.each{
            writeToFile("${item.experimentContext.experimentId}\t${item.id}\t${it.getCode()}")
        }
    }
}
writeToFile("# of ExperimentContextItems with Errors: ${itemsWithErrors}")
println("Processing has finished. Output file: " + outputFile)
println("# of ExperimentContextItems with Errors: ${itemsWithErrors}")
fileWriter.close()
