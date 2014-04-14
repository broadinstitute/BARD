m = ctx.bardMetadataToRdfService.createModel()
ctx.bardMetadataToRdfService.addAssay(8111, m);
ctx.bardMetadataToRdfService.writeToFileInN3Format(m, "test1.n3")
m = ctx.bardMetadataToRdfService.createModel()
ctx.bardMetadataToRdfService.addExperiment(5631, m);
ctx.bardMetadataToRdfService.writeToFileInN3Format(m, "test1Experiment.n3")
m = ctx.bardMetadataToRdfService.createModel()
ctx.bardMetadataToRdfService.addProject(3, m);
ctx.bardMetadataToRdfService.writeToFileInN3Format(m, "test1Project.n3")