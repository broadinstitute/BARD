import bard.db.project.ProjectContextItem
import bard.db.registration.AssayContextItem
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Created with IntelliJ IDEA.
 * User: ycruz
 * Date: 6/25/13
 * Time: 2:02 PM
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
String outputFile =  dirLocation + "/project_context_items_validation.txt"
FileWriter fileWriter = new FileWriter(outputFile, false)
fileWriter.write("List of ProjectContextItems that don't pass validation: \n\n")
fileWriter.flush()
writeToFile = {message ->
    fileWriter.write(message + "\n")
    fileWriter.flush()
}

println("Retrieving ProjectContextItems...")

List<ProjectContextItem> projectContextItems = ProjectContextItem.list()
println("Ready to process ${projectContextItems.size()} of Project Context Items in BARD")
writeToFile("# of Project Context Items in BARD: ${projectContextItems.size()}")

int itemsWithErrors = 0;
println("Detecting ProjectContextItems with errors...")
projectContextItems.each { ProjectContextItem item ->
    if (!item.validate()) {
        writeToFile("Project [ID: ${item.context.projectId}] ProjectContextItem [ID: ${item.id}]-> # of Errors: [${item.errors.errorCount}]")
        itemsWithErrors++
        item.errors.allErrors.each{
            writeToFile(it.toString())
        }
    }
}
writeToFile("# of ProjectContextItems with Errors: ${itemsWithErrors}")
println("Processing has finished. Output file: " + outputFile)
println("# of ProjectContextItems with Errors: ${itemsWithErrors}")
fileWriter.close()