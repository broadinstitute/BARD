import bard.db.registration.AttributeType
import bard.dm.minimumassayannotation.Attribute
import bard.dm.minimumassayannotation.ContextGroup
import bard.dm.minimumassayannotation.AssayContextGroupsBuilder
import bard.dm.minimumassayannotation.AttributeNameMappingBuilder
import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.minimumassayannotation.ParseAndBuildAttributeGroups
import bard.dm.minimumassayannotation.AttributesContentsCleaner
import bard.dm.minimumassayannotation.validateCreatePersist.AttributeContentAgainstElementTableValidator
import bard.dm.minimumassayannotation.validateCreatePersist.AssayContextsValidatorCreatorAndPersistor
import bard.dm.minimumassayannotation.validateCreatePersist.MeasureContextsValidatorCreatorAndPersistor
import bard.dm.Log
import org.apache.log4j.Level


final String modifiedBy = "dlahr-dna"
Log.logger.setLevel(Level.INFO)
final Integer START_ROW = 3 //1-based

List<File> inputFileList = new LinkedList<File>()

FilenameFilter xlsxExtensionFilenameFilter = new FilenameFilter() {
    boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".xlsx")
    }
}
//List<String> inputDirPathArray = ["test/exampleData/minAssayAnnotationSpreadsheets/", "test/exampleData/dnarepairmindataspreadsheets/"]
List<String> inputDirPathArray = ["test/exampleData/test/"]
for (String inputDirPath : inputDirPathArray) {
    File inputDirFile = new File(inputDirPath)
    inputFileList.addAll(inputDirFile.listFiles(xlsxExtensionFilenameFilter))
}
Log.logger.info("loading ${inputFileList.size()} files found in ${inputDirPathArray.size()} directories")


//final String inputFilePath = "test/exampleData/dnarepairmindataspreadsheets/Broad+others-DNA_repair.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/The Scripps Research Institute Molecular Screening Center-DNA repair.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/NCGC-DNA repair.short.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/Burnham Center for Chemical Genomics-DNA repair only.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/Broad+others-DNA_repair.xlsx"

//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/Broad+others-DNA_repair.xlsx");
//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/Burnham Center for Chemical Genomics-DNA repair only.xlsx");
//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/NCGC-DNA repair.short.xlsx");
//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/The Scripps Research Institute Molecular Screening Center-DNA repair.xlsx");



println("build mapping of columns to attributes and values")
List<ContextGroup> spreadsheetAssayContextGroups = (new AssayContextGroupsBuilder()).build()
List<Attribute> resultType = [new Attribute('2/Y', '$/Y', AttributeType.Fixed)]
List<ContextGroup> spreadsheetResultTypeContextGroups = [new ContextGroup(name: 'resultType', attributes: resultType)]


for (File inputFile : inputFileList) {
    Log.logger.info("processing file ${inputFile.absolutePath}")
    InputStream inp = new FileInputStream(inputFile)

    println("Build assay and measure-context (groups) and populate their attribute from the spreadsheet cell contents.")
    List<List<ContextDTO>> contextDTOGroups = (new ParseAndBuildAttributeGroups()).build(inp, START_ROW, [spreadsheetAssayContextGroups, spreadsheetResultTypeContextGroups])
    List<ContextDTO> assayContextList = contextDTOGroups[0]
    println("assayContextList size: ${assayContextList.size()}")
    List<ContextDTO> measureContextList = contextDTOGroups[1]
    println("measureContextList size: ${measureContextList.size()}")


    final Map attributeNameMapping = (new AttributeNameMappingBuilder()).build()


    println("Clean loaded attributes")
    AttributesContentsCleaner attributesContentsCleaner = new AttributesContentsCleaner()
    List<ContextDTO> assayContextListCleaned = attributesContentsCleaner.clean(assayContextList, attributeNameMapping)
    List<ContextDTO> measureContextListCleaned = attributesContentsCleaner.clean(measureContextList, attributeNameMapping)
    println("cleaned sizes ${assayContextListCleaned.size()} ${measureContextListCleaned.size()}")


    println("validate loaded attributes")
    AttributeContentAgainstElementTableValidator attributeContentAgainstElementTableValidator = new AttributeContentAgainstElementTableValidator()
    attributeContentAgainstElementTableValidator.validate(assayContextListCleaned, attributeNameMapping)
    attributeContentAgainstElementTableValidator.validate(measureContextListCleaned, attributeNameMapping)

    println("create, check for duplicates, and persist domain objects")
    (new AssayContextsValidatorCreatorAndPersistor(modifiedBy)).createAndPersist(assayContextListCleaned)
    (new MeasureContextsValidatorCreatorAndPersistor(modifiedBy)).createAndPersist(measureContextListCleaned)
}


return false



