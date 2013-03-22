import maas.AssayHandlerService
import maas.MustLoadAid


final List<String> inputDirs = [
        "data/maas/maasDataset1"
       // "data/maas/problemExcel"
]
final String outputDir = "data/maas/maasDataset1/"
final String runBy = "xiaorong"
def mustLoadedAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset1/aids_dataset_1.csv')

def assayHandlerService = new AssayHandlerService()
assayHandlerService.load(inputDirs, outputDir, runBy, mustLoadedAids)