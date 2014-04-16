import adf.exp.JsonTransform
import bard.db.experiment.ArchivePathService
import bard.db.experiment.Experiment

ArchivePathService archivePathService = ctx.archivePathService

String experimentId = System.getProperty("expId")
if(experimentId == null) {
    throw new RuntimeException("need parameter -DexpId=X which identifies which experiment to export results for")
}
Experiment experiment = Experiment.get(experimentId)
if(experiment == null) {
    throw new RuntimeException("Could not find experiment with id ${experimentId}")
}

String exportDir = System.getProperty("exportDir")
if(exportDir == null) {
    throw new RuntimeException("need parameter -DexportDir=X which identifies the directory to write the files to")
}

println("exporting experiment ${experimentId} exported to ${exportDir}")

JsonTransform transform = new JsonTransform()
File path = archivePathService.getEtlExportFile(experiment)
transform.transform(path, "${exportDir}/dataset_")
