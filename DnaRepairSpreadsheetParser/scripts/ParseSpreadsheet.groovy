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


final String modifiedBy = "dlahr-dna"
final String inputFilePath = "test/exampleData/dnarepairmindataspreadsheets/Broad+others-DNA_repair.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/The Scripps Research Institute Molecular Screening Center-DNA repair.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/NCGC-DNA repair.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/Burnham Center for Chemical Genomics-DNA repair only.xlsx"
//"test/exampleData/dnarepairmindataspreadsheets/Broad+others-DNA_repair.xlsx"

//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/Broad+others-DNA_repair.xlsx");
//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/Burnham Center for Chemical Genomics-DNA repair only.xlsx");
//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/NCGC-DNA repair.xlsx");
//InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/Simon/The Scripps Research Institute Molecular Screening Center-DNA repair.xlsx");



println("build mapping of columns to attributes and values")
List<ContextGroup> spreadsheetAssayContextGroups = (new AssayContextGroupsBuilder()).build()
List<Attribute> resultType = [new Attribute('2/Y', '$/Y', AttributeType.Fixed)]
List<ContextGroup> spreadsheetResultTypeContextGroups = [new ContextGroup(name: 'resultType', attributes: resultType)]


final Integer START_ROW = 3 //1-based
InputStream inp = new FileInputStream(inputFilePath);


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

println("create and persist domain objects")
(new AssayContextsValidatorCreatorAndPersistor(modifiedBy)).createAndPersist(assayContextListCleaned)
(new MeasureContextsValidatorCreatorAndPersistor(modifiedBy)).createAndPersist(measureContextListCleaned)

return false



