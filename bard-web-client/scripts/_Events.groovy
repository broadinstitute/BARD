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
    try {
        version = "git rev-parse HEAD".execute().text
        branchName =  "git rev-parse --abbrev-ref HEAD".execute().text
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
    metadata.persist()
}