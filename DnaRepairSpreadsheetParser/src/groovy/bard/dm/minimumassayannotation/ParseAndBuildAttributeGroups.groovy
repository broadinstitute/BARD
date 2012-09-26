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

    /**
     * Parses the input-stream spreadsheet and extracts all the values from the cells based on the list of attribute-groups.
     * an attribute group represents a group of key/value pairs (attributes) that should all be group together in a single assay or project context.
     *
     * @param inp
     * @param START_ROW
     * @param assayGroup
     * @return
     */
     List<List<ContextDTO>> build(FileInputStream inp, int START_ROW, List<List<ContextGroup>> contextGroupList) {
        Workbook wb = new XSSFWorkbook(inp);
        Sheet sheet = wb.getSheetAt(0);
        Integer aid
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
            if (rowCount++ > 6000) break //don't parse into the vocabulary section


            ContextDtoFromContextGroupCreator contextDtoFromContextGroupCreator = new ContextDtoFromContextGroupCreator()

            //Get the current AID
            String aidFromCell = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'A')
            //if we encountered a new AID, update the current-aid with the new one. Else, leave the existing one.
            if (aidFromCell) {
                Integer newAid = new Integer(aidFromCell.trim())
                aid = newAid
            }
            assert aid, "Couldn't find AID"


            //Iterate over all assay-groups' contexts
            assayGroup.each {ContextGroup contextGroup ->
                ContextDTO assayContextDTO = contextDtoFromContextGroupCreator.create(contextGroup, row, sheet)

                if (assayContextDTO.attributes) {
                    assayContextDTO.aid = aid
                    assayContextDTO.name = contextGroup.name
                    assayContextDtoList << assayContextDTO
                }
            }

            //Iterate over all assay-groups' contexts
            measureContextGroups.each {ContextGroup contextGroup ->
                ContextDTO measureContextDTO = contextDtoFromContextGroupCreator.create(contextGroup, row, sheet)

                if (measureContextDTO.attributes) {
                    measureContextDTO.aid = aid
                    measureContextDTO.name = contextGroup.name
                    measureContextDtoList << measureContextDTO
                }
            }
        }

        return [assayContextDtoList, measureContextDtoList]
    }
}
