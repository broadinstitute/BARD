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

    private final ContextLoadResultsWriter loadResultsWriter

    public ParseAndBuildAttributeGroups(ContextLoadResultsWriter loadResultsWriter) {
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
    List<AssayDto> build(File inputFile, int START_ROW, List<List<ContextGroup>> contextGroupList) {
        final Workbook wb
        try {
            wb = new XSSFWorkbook(new FileInputStream(inputFile))
        } catch (Exception e) {
            throw new CouldNotReadExcelFileException(e.message, e)
        }

        List<AssayDto> assayDtoList = new LinkedList<AssayDto>()
        AssayDto currentAssayDto = null

        Sheet sheet = wb.getSheetAt(0);
        Integer aid = null
        Integer rowCount = 0 //rows in spreadsheet are zero-based

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

                if ((currentAssayDto != null && currentAssayDto.aidFromCell != aidFromCell)
                        || null == currentAssayDto) {

                    currentAssayDto = new AssayDto(aidFromCell, inputFile, row.rowNum)
                    assayDtoList.add(currentAssayDto)
                }
            }


            //Iterate over all assay-groups' contexts
            assayGroup.each {ContextGroup contextGroup ->
                ContextDTO assayContextDTO = contextDtoFromContextGroupCreator.create(contextGroup, row, sheet)

                if (assayContextDTO.attributes) {
                    assayContextDTO.aid = currentAssayDto.aid
                    assayContextDTO.name = contextGroup.name

                    currentAssayDto.assayContextDTOList.add(assayContextDTO)
                }
            }

            //Iterate over all measure-groups' contexts
            measureContextGroups.each {ContextGroup contextGroup ->
                ContextDTO measureContextDTO = contextDtoFromContextGroupCreator.create(contextGroup, row, sheet)

                if (measureContextDTO.attributes) {
                    measureContextDTO.aid = currentAssayDto.aid
                    measureContextDTO.name = contextGroup.name
                    currentAssayDto.measureContextDTOList.add(measureContextDTO)
                }
            }

        }

        return assayDtoList
    }
}

class CouldNotReadExcelFileException extends Exception {
    CouldNotReadExcelFileException(String message, Throwable cause) {
        super(message, cause)
    }
}