package bard.dm.minimumassayannotation

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Row

/**
 * Parses the input-stream spreadsheet and extracts all the values from the cells based on the list of attribute-groups.
 * an attribute group represents a group of key/value pairs (attributes) that should all be group together in a single assay or project context.
 */
class ParseAndBuildAttributeGroups {
    private static final int maxRowNum = 6000

    private final LoadResultsWriter loadResultsWriter

    public ParseAndBuildAttributeGroups(LoadResultsWriter loadResultsWriter) {
        this.loadResultsWriter = loadResultsWriter
    }

    /**
     * Parses the input-stream spreadsheet and extracts all the values from the cells based on the list of attribute-groups.
     * an attribute group represents a group of key/value pairs (attributes) that should all be group together in a single assay or project context.
     *
     * @param inp
     * @param START_ROW
     * @param assayGroup
     * @return
     */
    List<List<ContextDTO>> build(File inputFile, int START_ROW, List<List<ContextGroup>> contextGroupList) {
        final Workbook wb
        try {
            wb = new XSSFWorkbook(new FileInputStream(inputFile))
        } catch (IllegalStateException e) {
            throw new CouldNotReadExcelFileException()
        }

        Sheet sheet = wb.getSheetAt(0);
        Integer aid = null
        Integer rowCount = 0 //rows in spreadsheet are zero-based
        List<ContextDTO> assayContextDtoList = []
        List<ContextDTO> measureContextDtoList = []

        List<ContextGroup> assayGroup = contextGroupList[0]
        List<ContextGroup> measureContextGroups = contextGroupList[1]

        for (Row row : sheet) {
            if (rowCount < START_ROW - 1) {//skip the header rows
                rowCount++
                continue
            }
            if (rowCount++ > maxRowNum) break //don't parse into the vocabulary section


            ContextDtoFromContextGroupCreator contextDtoFromContextGroupCreator = new ContextDtoFromContextGroupCreator()

            //Get the current AID
            String aidFromCell = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'A')
            //if we encountered a new AID, update the current-aid with the new one. Else, leave the existing one.
            if (aidFromCell) {
                aidFromCell = aidFromCell.trim()
                Integer newAid = null
                if (aidFromCell.isDouble()) {
                    newAid = new Integer((int)new Double(aidFromCell))
                } else if (aidFromCell.isInteger()) {
                    newAid = new Integer(aidFromCell)
                }
                aid = newAid
            }
            if (! aid) {
                loadResultsWriter.write(aidFromCell, null, null, LoadResultsWriter.LoadResultType.fail,
                        "could not parse AID on row ${row.rowNum} in file $inputFile")
            } else {
                //Iterate over all assay-groups' contexts
                assayGroup.each {ContextGroup contextGroup ->
                    ContextDTO assayContextDTO = contextDtoFromContextGroupCreator.create(contextGroup, row, sheet)

                    if (assayContextDTO.attributes) {
                        assayContextDTO.aid = aid
                        assayContextDTO.name = contextGroup.name
                        assayContextDtoList << assayContextDTO
                    }
                }

                //Iterate over all measure-groups' contexts
                measureContextGroups.each {ContextGroup contextGroup ->
                    ContextDTO measureContextDTO = contextDtoFromContextGroupCreator.create(contextGroup, row, sheet)

                    if (measureContextDTO.attributes) {
                        measureContextDTO.aid = aid
                        measureContextDTO.name = contextGroup.name
                        measureContextDtoList << measureContextDTO
                    }
                }
            }
        }

        return [assayContextDtoList, measureContextDtoList]
    }
}

class CouldNotReadExcelFileException extends Exception {
}