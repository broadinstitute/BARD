import org.apache.commons.lang3.StringUtils

/**
 * use assay 8111, experiment 5631, project 3 to test
 */
String entityType =  System.getProperty("entity")
String filename = System.getProperty("outputfile")
String entityid = System.getProperty("entityid")
println "Usage: grails -Dentity=assay|experiment|project -Dentityid=entityid [-Doutputfile=filename] run-script scripts/export-metadata.groovy "
if (!entityType) {
    println("Please provide entity type: -Dentity=assay|experiment|project")
    return
}
if (!entityid && !StringUtils.isNumeric(entityid)) {
    println("Please provide a valid entity id: -Dentityid=somenumber")
    return
}

if (!filename) {
    filename = entityType + "_" + entityid + ".n3"
}

m = ctx.bardMetadataToRdfService.createModel()
if (entityType.equals('assay')) {
    ctx.bardMetadataToRdfService.addAssay(Integer.parseInt(entityid), m);
}
else if (entityType.equals('experiment')) {
    ctx.bardMetadataToRdfService.addExperiment(Integer.parseInt(entityid), m);
}
else if (entityType.equals('project')) {
    ctx.bardMetadataToRdfService.addProject(Integer.parseInt(entityid), m);
}
ctx.bardMetadataToRdfService.writeToFileInN3Format(m, filename)