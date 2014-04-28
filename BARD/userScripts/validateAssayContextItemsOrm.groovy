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
