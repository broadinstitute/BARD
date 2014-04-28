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

/**
 * Created by IntelliJ IDEA.
 * User: jasiedu
 * Date: 1/25/12
 * Time: 9:27 AM
 * To change this template use File | Settings | File Templates.
 */
eventWarStart = { type ->
    String version
    String branchName
    String buildDate = new Date()
    try {
        version = "git rev-parse HEAD".execute().text
        branchName = "git rev-parse --abbrev-ref HEAD".execute().text
        // prefer tagNames to branch names.  So if we have a tag, use that as the name
        def tagName = "git tag --points-at HEAD".execute().text
        if(tagName.length() > 0) {
            branchName = tagName;
        }
    } catch (e) {
        // couldn't access git.  Retrieved from the user interactively
        println "In order to build a war file we need the Git hash and name for the most recent build"
        println "Please enter Git hash for this branch and name (example: a03995d31d5c72a2dbd52c4ac5b5a344da5cbf3e iteration.023)"
        String tempReadInput
        System.in.withReader { tempReadInput=it.readLine() }
        String[] parsedInput = tempReadInput.split()
        if (parsedInput.size() != 2) {
            println "Unable to retrieve hash name and branch name. Aborting generation of war file. Script name=_Events.gsp"
            System.exit(0)
        }
        version= parsedInput[0].trim()
        branchName= parsedInput[1].trim()
        println "Proceeding with hash = $version and branch = $branchName"
    }
    metadata.'git.branch.name' = "${branchName}".toString().trim()
    metadata.'git.branch.version' = "${version}".toString().trim()
    metadata.'war.created'=buildDate.toString()
    metadata.persist()
}
