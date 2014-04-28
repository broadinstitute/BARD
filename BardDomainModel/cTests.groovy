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

def currentDir() { 'pwd'.execute().text.trim() }

def getGrailsPropertyArgs(group) {
	def testDir = "${currentDir()}/target/test-$group"
	[
	    "-Dgrails.work.dir=${testDir}",
	    "-Dgrails.project.class.dir=${testDir}/classes",
	    "-Dgrails.project.test.class.dir=${testDir}/test-classes",
	    "-Dgrails.project.test.reports.dir=${testDir}/test-reports"
	].join(" ")
}

synchronized out(group, message) {
    println("${group.padLeft(12, ' ')}: $message")
}
def exitStatuses = [:]
[
        project: 'unit: bard.db.project.*',
        dictionary: 'unit: bard.db.dictionary.*'
].collect { testGroup, args ->
	Thread.start {
		def command = "grails ${getGrailsPropertyArgs(testGroup)} test-app $args"
		out testGroup, command
		exitStatuses[testGroup] = command.execute().with { proc ->
			proc.in.eachLine { line -> out testGroup, line }
			proc.waitFor()
			proc.exitValue()
		}
		out testGroup, "exit value: ${exitStatuses[testGroup]}"
	}
}.each { it.join() }

def failingGroups = exitStatuses.findAll { it.value }

if (!failingGroups) {
	println "All tests were successful!"
} else {
	failingGroups.each { failingGroup, exitStatus ->
		out(failingGroup, "WARNING: '$failingGroup' exit status was $exitStatus")
	}
	System.exit(-1)
}
