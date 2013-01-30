package bard.dm.minimumassayannotation

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellReference
import org.apache.poi.ss.util.CellUtil
import bard.dm.Log
import org.apache.commons.lang3.StringUtils

/**
 * Iterates over all the attribute-pairs in each context and build a contextDTO
 */
class ContextDtoFromContextGroupCreator {

    /**
     * Iterates over all the attribute-pairs in each context and builds a contextDTO
     *
     * @param row
     * @param sheet
     * @return
     */
    ContextDTO create(ContextGroup contextGroup, Row row, Sheet sheet) {
        ContextDTO contextDTO = new ContextDTO()

        for (ContextItemDto cellLocsContextItemDto : contextGroup.contextItemDtoList) {
            //Get the attribute's key-content from cell
            String keyCellId = cellLocsContextItemDto.key
            def keyDef = getCellContent(keyCellId, row, sheet)
            String key = getTrimStringOrNullFromDef(keyDef)

            //Get the attribute's value-content from cell
            String valueCell = cellLocsContextItemDto.value
            def value = getCellContent(valueCell, row, sheet)

            //Attribute ket and value must exist
            if (!StringUtils.isEmpty(key) && value) {
                //Get the attribute's qualifier-content from cell
                String qualifierCellId = cellLocsContextItemDto.qualifier
                def qualifierDef = qualifierCellId ? getCellContent(qualifierCellId, row, sheet) : qualifierCellId
                String qualifier = getTrimStringOrNullFromDef(qualifierDef)


                //Get the attribute's concentration-unit value from cell
                String concentrationUnitsCellId = cellLocsContextItemDto.concentrationUnits
                def concentrationUnitsDef = concentrationUnitsCellId ? getCellContent(concentrationUnitsCellId, row, sheet) : concentrationUnitsCellId
                String concentrationUnits = getTrimStringOrNullFromDef(concentrationUnitsDef)

                //Create a new attribute and add it to the context's attributes collection.
                ContextItemDto contextItemDto = new ContextItemDto(key, value, cellLocsContextItemDto.attributeType,
                        cellLocsContextItemDto.typeIn, qualifier, concentrationUnits)

                contextDTO.contextItemDtoList.add(contextItemDto)
            }
        }
        return contextDTO
    }

    private static String getTrimStringOrNullFromDef(def item) {
        if (item != null) {
            String itemString = item.toString().trim()
            if (itemString.length() > 0) {
                return itemString
            }
        }

        return null
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
                Log.logger.error("Error extracting cell content: ${cell} ${cell.rowIndex} ${cell.columnIndex} ${cell.getCellType()}")
                return null
        }
    }
}


