List sids = []

String experimentIdStr = System.getProperty("experimentId")
if (experimentIdStr == null) {
    throw new RuntimeException("Need to run with -DexperimentId=EXPERIMENT")
}

Long experimentId = Long.parseLong(experimentIdStr);

ctx.resultsExportService.dumpFromDb(experimentId)

