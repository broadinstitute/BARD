import maas.AssayHandlerService
import maas.MustLoadAid


String dir = "data/maas/maasDataset2/"
String aidsFile = "aids_dataset_2.csv"
final String runBy = "xx"
final List<String> inputDirs = [dir]
final String outputDir = "${dir}/output/"
def mustLoadedAids = MustLoadAid.mustLoadedAids("${dir}${aidsFile}")

def assayHandlerService = new AssayHandlerService()
assayHandlerService.load(inputDirs, outputDir, runBy, mustLoadedAids)