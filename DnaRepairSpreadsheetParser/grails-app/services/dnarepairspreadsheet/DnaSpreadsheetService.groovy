package dnarepairspreadsheet

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.util.CellReference
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.util.CellUtil

class DnaSpreadsheetService {

    def parse() {
        List<SpreadsheetAttribute> processOrTarget = [new SpreadsheetAttribute('$/C', '$/D')]
        List<SpreadsheetAttribute> assayFormat = [new SpreadsheetAttribute('1/E', '$/E')]
        List<SpreadsheetAttribute> assayType = [new SpreadsheetAttribute('2/F', '$/F')]
        List<SpreadsheetAttribute> assayComponent = [new SpreadsheetAttribute('2/G', '$/G'),
                new SpreadsheetAttribute('2/H', '$/H'),
                new SpreadsheetAttribute('2/I', '$/I'),
                new SpreadsheetAttribute('2/J', '$/J'),
                new SpreadsheetAttribute('2/K', '$/K'),
                new SpreadsheetAttribute('2/M', '$/M')]
        List<SpreadsheetAttribute> assayDetector = [new SpreadsheetAttribute('2/N', '$/N'),
                new SpreadsheetAttribute('2/O', '$/O')]
        List<SpreadsheetAttribute> assayFootprint = [new SpreadsheetAttribute('2/U', '$/U')]
        List<SpreadsheetAttribute> assayExcitation = [new SpreadsheetAttribute('2/V', '$/V'),
                new SpreadsheetAttribute('2/W', '$/W')]

        List<SpreadsheetContextGroup> assayGroup = [new SpreadsheetContextGroup(name: 'processOrTarget', contextAttributes: processOrTarget),
                new SpreadsheetContextGroup(name: 'assayFormat', contextAttributes: assayFormat),
                new SpreadsheetContextGroup(name: 'assayType', contextAttributes: assayType),
                new SpreadsheetContextGroup(name: 'assayComponent', contextAttributes: assayComponent),
                new SpreadsheetContextGroup(name: 'assayDetector', contextAttributes: assayDetector),
                new SpreadsheetContextGroup(name: 'assayFootprint', contextAttributes: assayFootprint),
                new SpreadsheetContextGroup(name: 'assayExcitation', contextAttributes: assayExcitation)]

        final Integer START_ROW = 3 //1-based

        InputStream inp = new FileInputStream("C:/Users/gwalzer/Desktop/NCGC-DNA repair.xlsx");
        Workbook wb = new XSSFWorkbook(inp);
        Sheet sheet1 = wb.getSheetAt(0);

        Integer rowCount = 0 //rows in spreadsheet are zero-based
        Integer aid
        for (Row row : sheet1) {

            if (rowCount < START_ROW - 1) {//skip the header rows
                rowCount++
                continue
            }

            //Get the current AID
            Integer aidFromCell = new Integer(getCellContent(row, 'A'))
            //if we encountered a new AID, update the current-aid with the new one. Else, leave the existing one.
            if (aidFromCell) {
                aid = aidFromCell
            }
            assert aid, "Couldn't find AID"

            //Iterate over all assay-groups' contexts
            assayGroup.each {SpreadsheetContextGroup contextGroup ->
                //TODO - create a new assay-context
                println("Assay context: ${contextGroup.name}\n")
                //Iterate over all the attribute-pairs in each context
                contextGroup.contextAttributes.each {SpreadsheetAttribute attribute ->
                    //Get the attribute's key-value from cell
                    String attKeyCell = attribute.attKeyCell
                    Row attKeyCellRow = attKeyCell.split('/')[0] == '$' ? row : sheet1.getRow(attKeyCell.split('/')[0] - 1)
                    String attrKey = getCellContent(attKeyCellRow, attKeyCell.split('/')[1])
                    //Get the attribute's value-value from cell
                    String attValueCell = attribute.attrValueCell
                    Row attValueCellRow = attValueCell.split('/')[0] == '$' ? row : sheet1.getRow(attValueCell.split('/')[0] - 1)
                    String attrValue = getCellContent(attValueCellRow, attValueCell.split('/')[1])

                    if (!(attrKey && attrValue)) return

                    println("\tKey: '${attrKey}' / Value: '${attrValue}'")
                }
            }

//    for (Cell cell : row) {
//        CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
//        System.out.print(cellRef.formatAsString());
//        System.out.print(" - ");

            rowCount++
        }
    }

    def getCellContent(Row row, String columnString) {
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

class SpreadsheetAttribute {
    String attKeyCell;
    String attrValueCell;

    public SpreadsheetAttribute(String attKeyCell, String attrValueCell) {
        this.attKeyCell = attKeyCell
        this.attrValueCell = attrValueCell
    }
}

class SpreadsheetContextGroup {
    String name;
    List<SpreadsheetAttribute> contextAttributes;
}