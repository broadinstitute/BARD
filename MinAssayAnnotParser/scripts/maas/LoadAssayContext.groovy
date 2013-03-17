import maas.AssayHandlerService
import maas.MustLoadAid


final List<String> inputDirs = ["data/maas/maasDataset2"]
final String outputDir = "data/maas/maasDataset2/output/"
final String runBy = "xiaorong"
def mustLoadedAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset2/aids_dataset_2.csv')

def assayHandlerService = new AssayHandlerService()
assayHandlerService.load(inputDirs, outputDir, runBy, mustLoadedAids)