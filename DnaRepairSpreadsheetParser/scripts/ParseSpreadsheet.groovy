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


def out = new File('DnaSpreadsheetParsing' + '_' + System.currentTimeMillis() + '.txt')

List<ContextGroup> spreadsheetAssayContextGroups = (new AssayContextGroupsBuilder()).build()

List<Attribute> resultType = [new Attribute('2/Y', '$/Y', AttributeType.Fixed)]
List<ContextGroup> spreadsheetResultTypeContextGroups = [new ContextGroup(name: 'resultType', attributes: resultType)]

Map attributeNameMapping = (new AttributeNameMappingBuilder()).build()

final Integer START_ROW = 3 //1-based
InputStream inp = new FileInputStream(inputFilePath);


//Build assay and measure-context (groups) and populate their attribute from the spreadsheet cell contents.
List<List<ContextDTO>> contextDTOGroups = (new ParseAndBuildAttributeGroups()).build(inp, START_ROW, [spreadsheetAssayContextGroups, spreadsheetResultTypeContextGroups])
List<ContextDTO> assayContextList = contextDTOGroups[0]
println("assayContextList size: ${assayContextList.size()}")
List<ContextDTO> measureContextList = contextDTOGroups[1]
println("measureContextList size: ${measureContextList.size()}")

////print out to a file the raw grouped attributes (key/value pairs)
//out.withWriterAppend { writer ->
//    assayContextList.each {AssayContextDTO assayContextDTO ->
//        writer.writeLine("\nAID=${assayContextDTO.aid}; ${assayContextDTO.name}")
//        assayContextDTO.attributes.each {Attribute attribute ->
//            writer.writeLine("\t'${attribute.key}'/'${attribute.value}'; typeIn=${attribute.typeIn}; qualifier='${attribute.qualifier}' type=${attribute.attributeType}")
//        }
//    }
//}

AttributesContentsCleaner attributesContentsCleaner = new AttributesContentsCleaner()
List<ContextDTO> assayContextListCleaned = attributesContentsCleaner.clean(assayContextList, attributeNameMapping)
List<ContextDTO> measureContextListCleaned = attributesContentsCleaner.clean(measureContextList, attributeNameMapping)

////Print out the cleaned grouped attributes (key/value pairs)
//out = new File('DnaSpreadsheetParsingCleaned' + '_' + System.currentTimeMillis() + '.txt')
//out.withWriterAppend { writer ->
//    assayContextList.each {AssayContextDTO assayContextDTO ->
//        writer.writeLine("\nAID=${assayContextDTO.aid}; ${assayContextDTO.name}")
//        assayContextDTO.attributes.each {Attribute attribute ->
//            writer.writeLine("\t'${attribute.key}'/'${attribute.value}'; typeIn=${attribute.typeIn}; qualifier='${attribute.qualifier}' type=${attribute.attributeType}")
//        }
//    }
//}

AttributeContentAgainstElementTableValidator attributeContentAgainstElementTableValidator = new AttributeContentAgainstElementTableValidator()
attributeContentAgainstElementTableValidator.validate(assayContextListCleaned, attributeNameMapping)
attributeContentAgainstElementTableValidator.validate(measureContextListCleaned, attributeNameMapping)

(new AssayContextsValidatorCreatorAndPersistor(modifiedBy)).createAndPersist(assayContextListCleaned)
(new MeasureContextsValidatorCreatorAndPersistor(modifiedBy)).createAndPersist(measureContextListCleaned)

return false

