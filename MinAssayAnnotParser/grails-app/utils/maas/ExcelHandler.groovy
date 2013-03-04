package maas

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Row
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.util.CellUtil
import org.apache.poi.ss.util.CellReference
import org.apache.poi.ss.usermodel.DateUtil

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/1/13
 * Time: 10:27 PM
 * To change this template use File | Settings | File Templates.
 */
class ExcelHandler {
    public static final String finishedMark = "finished"

    static List<Dto> buildDto(File inputFile, int START_ROW, List<ContextGroup> contextGroups, int maxRowNum) {

        try {
            final Workbook wb = new XSSFWorkbook(new FileInputStream(inputFile))
            Sheet sheet = wb.getSheetAt(0);
            Dto currentDto = getFirstDto(sheet, START_ROW, inputFile)

            List<Dto> dtoList = []
            if (currentDto != null) {
                dtoList.add(currentDto)
            } else {
                return dtoList
            }

            boolean foundFinishedMark = false
            Iterator<Row> rowIterator = getIteratorAtStartOfAids(sheet, START_ROW)

            Row row
            while (rowIterator.hasNext() && !foundFinishedMark && (row = rowIterator.next()).rowNum < maxRowNum) {

                //Get the current AID
                String aidFromCell = getCellContentByRowAndColumnIds(row, 'A')
                aidFromCell = getTrimStringOrNullFromDef(aidFromCell)
                foundFinishedMark = aidFromCell.equalsIgnoreCase(finishedMark)
                if (foundFinishedMark)
                    break
                //if we encountered a new AID, update the current-aid with the new one. Else, leave the existing one.
                if (aidFromCell) {
                    if (!foundFinishedMark && currentDto.aidFromCell != aidFromCell) {
                        currentDto = new Dto(aidFromCell, inputFile, row.rowNum)
                        dtoList.add(currentDto)
                    }
                }

                //Iterate over all assay-groups' contexts
                for (ContextGroup contextGroup : contextGroups) {
                    ContextDTO contextDTO = createContextDtoFromContextGroup(contextGroup, row, sheet)
                    if (contextDTO.contextItemDtoList.size() != 0) {
                        contextDTO.aid = currentDto.aid
                        contextDTO.name = contextGroup.name
                        currentDto.contextDTOs.add(contextDTO)
                    }
                }
            }

            return dtoList
        } catch (Exception e) {
            throw new CouldNotReadExcelFileException(e.message, e)
        }
    }

    /**
     * Iterates over all the attribute-pairs in each context and builds a contextDTO from defined group
     *
     * @param row
     * @param sheet
     * @return
     */
    static ContextDTO createContextDtoFromContextGroup(ContextGroup contextGroup, Row row, Sheet sheet) {
        ContextDTO contextDTO = new ContextDTO()
        contextGroup.contextItemDtoList.each {ContextItemDto dto ->
            //Get the attribute's key-content from cell
            def keyDef = getCellContent(dto.key, row, sheet)
            String key = getTrimStringOrNullFromDef(keyDef)
            key = dto.assignedName ? dto.assignedName : key  // use assigned name if there is any

            //Get the attribute's value-content from cell
            def value = getCellContent(dto.value, row, sheet)

            //Attribute ket and value must exist
            if (key && value) {
                //Get the attribute's qualifier-content from cell
                String qualifierCellId = dto.qualifier
                def qualifierDef = qualifierCellId ? getCellContent(qualifierCellId, row, sheet) : qualifierCellId
                String qualifier = getTrimStringOrNullFromDef(qualifierDef)

                //Get the attribute's concentration-unit value from cell
                String concentrationUnitsCellId = dto.concentrationUnits
                def concentrationUnitsDef = concentrationUnitsCellId ? getCellContent(concentrationUnitsCellId, row, sheet) : concentrationUnitsCellId
                String concentrationUnits = getTrimStringOrNullFromDef(concentrationUnitsDef)

                //Create a new attribute and add it to the context's attributes collection.
                ContextItemDto contextItemDto = new ContextItemDto(key:key, value:value, attributeType:dto.attributeType,
                        typeIn:dto.typeIn, qualifier:qualifier, concentrationUnits:concentrationUnits, assignedName: dto.assignedName)

                contextDTO.contextItemDtoList.add(contextItemDto)
            }
        }
        return contextDTO
    }



    public static class CouldNotReadExcelFileException extends Exception {
        CouldNotReadExcelFileException(String message, Throwable cause) {
            super(message, cause)
        }
    }


    private static Dto getFirstDto(Sheet sheet, int START_ROW, File inputFile) {
        Iterator<Row> rowIterator = getIteratorAtStartOfAids(sheet, START_ROW)

        int rowNum = -1;
        String aidFromCell = null
        while (rowIterator.hasNext() && null == aidFromCell) {
            Row row = rowIterator.next();
            aidFromCell = getCellContentByRowAndColumnIds(row, 'A')
            rowNum = row.rowNum
        }

        if (!StringUtils.isEmpty(aidFromCell)) {
            return new Dto(aidFromCell.trim(), inputFile, rowNum)
        } else {
            return null
        }
    }

    /**
     * Find start point, and get iterator
     * @param sheet
     * @param START_ROW
     * @return
     */
    private static Iterator<Row> getIteratorAtStartOfAids(Sheet sheet, int START_ROW) {
        Iterator<Row> rowIterator = sheet.rowIterator();

        while (rowIterator.hasNext() && rowIterator.next().rowNum < (START_ROW - 1)) {
        }

        return rowIterator;
    }


    private static String getTrimStringOrNullFromDef(def item) {
        if (!item)
            return null
        if (StringUtils.isEmpty(item.toString()))
            return null
        return item.toString().trim()
    }

    /**
     * Extracts spreadsheet cell content given a cellId (e.g., '3/C' == row 3, column C (3))
     *
     * @param cellId
     * @param row - the default current-row (from the row-iterator)
     * @param sheet - the current sheet we are working on
     * @return Cell content; type varies based on CELL_CONTENT_TYPE in the spreadsheet
     */
    private static getCellContent(String cellId, Row row, Sheet sheet) {
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
    private static Row getCellRowFromCellId(String cellId, Row currentRow, Sheet currentSheet) {
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
    static def getCellContentByRowAndColumnIds(Row row, String columnString) {
        Cell cell = CellUtil.getCell(row, CellReference.convertColStringToIndex(columnString))
        def ret

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                break
            case Cell.CELL_TYPE_STRING:
                ret = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    ret = cell.getDateCellValue();
                } else {
                    ret = cell.getNumericCellValue();
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                ret = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                ret = cell.getCellFormula() as String;
                break;
            default:
                println("Error extracting cell content: ${cell} ${cell.rowIndex} ${cell.columnIndex} ${cell.getCellType()}")
        }
        return ret
    }

    static void constructInputFileList(List<String> inputDirPathArray, List<File> inputFileList) {
        FilenameFilter xlsxExtensionFilenameFilter = new FilenameFilter() {
            boolean accept(File dir, String name) {
                final String lower = name.toLowerCase()
                return lower.endsWith(".xlsx") || lower.endsWith(".xls")
            }
        }
        for (String inputDirPath : inputDirPathArray) {
            File inputDirFile = new File(inputDirPath)
            assert inputDirFile, inputDirPath

            List<File> fileList = inputDirFile.listFiles(xlsxExtensionFilenameFilter)
            inputFileList.addAll(fileList)

            println("loading files from directory: ${inputDirPath} contains ${fileList.size()}")
        }

        println("loading ${inputFileList.size()} files found in ${inputDirPathArray.size()} directories")
    }
}
