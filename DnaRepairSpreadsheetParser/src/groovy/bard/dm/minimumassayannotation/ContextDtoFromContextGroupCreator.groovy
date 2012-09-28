package bard.dm.minimumassayannotation

import bard.dm.minimumassayannotation.ContextDTO
import bard.dm.minimumassayannotation.ContextGroup
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import bard.dm.minimumassayannotation.Attribute
import bard.db.registration.AttributeType
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.util.CellUtil
import org.apache.poi.ss.util.CellReference
import org.apache.poi.ss.usermodel.DateUtil

/**
 * Iterates over all the attribute-pairs in each context and build a contextDTO
 */
class ContextDtoFromContextGroupCreator {
    /**
     * Iterates over all the attribute-pairs in each context and build a contextDTO
     *
     * @param row
     * @param sheet
     * @return
     */
    ContextDTO create(ContextGroup contextGroup, Row row, Sheet sheet) {
        ContextDTO contextDTO = new ContextDTO()

        contextGroup.attributes.each {Attribute attribute ->
            //Get the attribute's key-content from cell
            String attKeyCellId = attribute.key
            def attrKey = getCellContent(attKeyCellId, row, sheet)
            if (attrKey)
                assert attrKey instanceof String, "Key must be a string"


            //Get the attribute's qualifier-content from cell
            String attQualifierCellId = attribute.qualifier
            def attrQualifier = attQualifierCellId ? getCellContent(attQualifierCellId, row, sheet) : attQualifierCellId
            if (attrQualifier)
                assert attrQualifier instanceof String, "Qualifier must be a string"

            //Get the attribute's value-content from cell
            String attValueCell = attribute.value
            def attrValue = getCellContent(attValueCell, row, sheet)

            //Attribute key must exist; attribute value must exist UNLESS it is of type Free
            if ((!attrKey) || (!attrValue && (attribute.attributeType != AttributeType.Free))) return

            //Get the attribute's concentration-unit value from cell
            String attConcentrationUnitsCellId = attribute.concentrationUnits
            def attrConcentrationUnits = attConcentrationUnitsCellId ? getCellContent(attConcentrationUnitsCellId, row, sheet) : attConcentrationUnitsCellId
            if (attrConcentrationUnits)
                assert attrConcentrationUnits instanceof String, "ConcentrationUnits must be a string"

            //Create a new attribute and add it to the context's attributes collection.
            Attribute attr = new Attribute(attrKey, attrValue, attribute.attributeType, attribute.typeIn, attrQualifier, attrConcentrationUnits)

            contextDTO.attributes << attr
        }
        return contextDTO
    }

    /**
     * Extracts spreadsheet cell content given a cellId (e.g., '3/C' == row 3, column C (3))
     *
     * @param cellId
     * @param row - the default current-row (from the row-iterator)
     * @param sheet - the current sheet we are working on
     * @return Cell content; type varies based on CELL_CONTENT_TYPE in the spreadsheet
     */
    private def getCellContent(String cellId, Row row, Sheet sheet) {
        if (!(cellId && row && sheet)) return null

        Row cellRow = getCellRowFromCellId(cellId, row, sheet)
        def content = getCellContentByRowAndColumnIds(cellRow, cellId.split('/')[1].trim())
        return content
    }

    /**
     * Get the correct Row based on the row-number in the cell-ID
     * If the cell's row-ID is '$', then we return the current row. Otherwise, get the cell from the current sheet.
     *
     * @param cellId
     * @param currentRow
     * @param currentSheet
     * @return
     */
    private Row getCellRowFromCellId(String cellId, Row currentRow, Sheet currentSheet) {
        String rowString = cellId.split('/')[0]
        Row cellRow
        if (rowString == '$') {//replace $ with the current row
            cellRow = currentRow
        } else {
            Integer rowIndex = new Integer(rowString.trim())
            cellRow = currentSheet.getRow(rowIndex - 1)//spreadsheet is 1-based while the POI objects are zero-based
        }
        return cellRow
    }

    /**
     * Returns the cell content based on the CELL_TYPE
     *
     * @param row
     * @param columnString
     * @return
     */
    def getCellContentByRowAndColumnIds(Row row, String columnString) {
        Cell cell = CellUtil.getCell(row, CellReference.convertColStringToIndex(columnString))

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                return null;
                break;
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula() as String;
                break;
            default:
                throw new RuntimeException("Error extracting cell content: ${cell}")
        }
    }
}


