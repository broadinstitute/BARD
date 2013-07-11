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
