m = ctx.bardMetadataToRdfService.createModel()
ctx.bardMetadataToRdfService.addAssay(8111, m);
ctx.bardMetadataToRdfService.writeToFileInN3Format(m, "test1.n3")