package bard.dm.minimumassayannotation

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Row
import org.apache.commons.lang3.StringUtils

/**
 * Parses the input-stream spreadsheet and extracts all the values from the cells based on the list of attribute-groups.
 * an attribute group represents a group of key/value pairs (attributes) that should all be group together in a single assay or project context.
 */
class ParseAndBuildAttributeGroups {
    private static final String finishedMark = "finished"

    private final int maxRowNum

    private final ContextLoadResultsWriter loadResultsWriter

    private final ContextDtoFromContextGroupCreator contextDtoFromContextGroupCreator

    public ParseAndBuildAttributeGroups(ContextLoadResultsWriter loadResultsWriter, int maxRowNum) {
        this.loadResultsWriter = loadResultsWriter
        this.maxRowNum = maxRowNum

        contextDtoFromContextGroupCreator = new ContextDtoFromContextGroupCreator()
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
    List<AssayDto> build(File inputFile, int START_ROW, List<List<ContextGroup>> contextGroupListList) {

        try {
            final Workbook wb = new XSSFWorkbook(new FileInputStream(inputFile))

            Sheet sheet = wb.getSheetAt(0);

            List<AssayDto> assayDtoList = new LinkedList<AssayDto>()

            AssayDto currentAssayDto = getFirstAssayDto(sheet, START_ROW, inputFile)
            if (currentAssayDto != null) {
                assayDtoList.add(currentAssayDto)
            } else {
                return assayDtoList
            }

            List<ContextGroup> assayContextGroupList = contextGroupListList[0]
            List<ContextGroup> measureContextGroupList = contextGroupListList[1]

            boolean foundFinishedMark = false
            Iterator<Row> rowIterator = getIteratorAtStartOfAids(sheet, START_ROW)
            Row row
            while (rowIterator.hasNext() && !foundFinishedMark && (row = rowIterator.next()).rowNum < maxRowNum) {

                //Get the current AID
                String aidFromCell = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'A')
                //if we encountered a new AID, update the current-aid with the new one. Else, leave the existing one.
                if (aidFromCell) {
                    aidFromCell = aidFromCell.trim()

                    foundFinishedMark = aidFromCell.equalsIgnoreCase(finishedMark)

                    if (!foundFinishedMark && currentAssayDto.aidFromCell != aidFromCell) {
                        currentAssayDto = new AssayDto(aidFromCell, inputFile, row.rowNum)
                        assayDtoList.add(currentAssayDto)
                    }
                }

                if (!foundFinishedMark) {
                    //Iterate over all assay-groups' contexts
                    for (ContextGroup assayContextGroup : assayContextGroupList) {
                        ContextDTO assayContextDTO = contextDtoFromContextGroupCreator.create(assayContextGroup, row, sheet)

                        if (assayContextDTO != null && assayContextDTO.contextItemDtoList) {
                            assayContextDTO.aid = currentAssayDto.aid
                            assayContextDTO.name = assayContextGroup.name

                            currentAssayDto.assayContextDTOList.add(assayContextDTO)
                        }
                    }

                    //Iterate over all measure-groups' contexts
                    for (ContextGroup measureContextGroup : measureContextGroupList) {
                        ContextDTO measureContextDTO = contextDtoFromContextGroupCreator.create(measureContextGroup, row, sheet)

                        if (measureContextDTO != null && measureContextDTO.contextItemDtoList) {
                            measureContextDTO.aid = currentAssayDto.aid
                            measureContextDTO.name = measureContextGroup.name
                            currentAssayDto.measureContextDTOList.add(measureContextDTO)
                        }
                    }
                }
            }

            return assayDtoList
        } catch (Exception e) {
            throw new CouldNotReadExcelFileException(e.message, e)
        }
    }


    private AssayDto getFirstAssayDto(Sheet sheet, int START_ROW, File inputFile) {
        Iterator<Row> rowIterator = getIteratorAtStartOfAids(sheet, START_ROW)

        int rowNum = -1;
        String aidFromCell = null
        while (rowIterator.hasNext() && null == aidFromCell) {
            Row row = rowIterator.next();
            aidFromCell = contextDtoFromContextGroupCreator.getCellContentByRowAndColumnIds(row, 'A')
            rowNum = row.rowNum
        }

        if (! StringUtils.isEmpty(aidFromCell)) {
            return new AssayDto(aidFromCell.trim(), inputFile, rowNum)
        } else {
            return null
        }
    }

    private static Iterator<Row> getIteratorAtStartOfAids(Sheet sheet, int START_ROW) {
        Iterator<Row> rowIterator = sheet.rowIterator();

        while (rowIterator.hasNext() && rowIterator.next().rowNum < (START_ROW - 1)) { }

        return rowIterator;
    }
}

class CouldNotReadExcelFileException extends Exception {
    CouldNotReadExcelFileException(String message, Throwable cause) {
        super(message, cause)
    }
}