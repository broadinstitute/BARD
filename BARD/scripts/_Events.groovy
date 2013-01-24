eventWarStart = { type ->
    final String version = "git rev-parse HEAD".execute().text
    final String branchName = "git rev-parse --abbrev-ref HEAD".execute().text
    metadata.'git.branch.name' = "${branchName}".toString().trim()
    metadata.'git.branch.version' = "${version}".toString().trim()
    metadata.persist()
}