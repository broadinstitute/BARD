package bard.db.experiment.results

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/29/13
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
class Row {
        int lineNumber;

        Integer rowNumber;
        Integer replicate;
        Integer parentRowNumber;
        Long sid;

        List<RawCell> cells = [];

        RawCell find(String columnName) {
            RawCell found = null;

            for(cell in cells) {
                if (cell.columnName == columnName) {
                    found = cell;
                    break;
                }
            }

            return found;
        }
}
