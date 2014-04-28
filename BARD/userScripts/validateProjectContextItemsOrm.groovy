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

import bard.db.project.ProjectContextItem
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Created with IntelliJ IDEA.
 * User: ycruz
 * Date: 6/25/13
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */


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

writeToFile = {message ->
    fileWriter.write(message + "\n")
    fileWriter.flush()
}

println("Retrieving ProjectContextItems...")

List<ProjectContextItem> projectContextItems = ProjectContextItem.list()
println("Ready to process ${projectContextItems.size()} of Project Context Items in BARD")
writeToFile("# of Project Context Items in BARD: ${projectContextItems.size()}")
writeToFile("List of ProjectContextItems that don't pass validation: \n")
writeToFile("Project ID\tProjectContextItem ID\tError(s)")

int itemsWithErrors = 0;
println("Detecting ProjectContextItems with errors...")
projectContextItems.each { ProjectContextItem item ->
    if (!item.validate()) {
        itemsWithErrors++
        item.errors.allErrors.each{
            writeToFile("${item.context.projectId}\t${item.id}\t${it.getCode()}")
        }
    }
}
writeToFile("\n# of ProjectContextItems with Errors: ${itemsWithErrors}")
println("Processing has finished. Output file: " + outputFile)
println("# of ProjectContextItems with Errors: ${itemsWithErrors}")
fileWriter.close()
