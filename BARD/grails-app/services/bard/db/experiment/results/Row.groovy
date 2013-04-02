package bard.db.experiment.results;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/29/13
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class Row {
    int lineNumber;

    Integer rowNumber;
    Integer replicate;
    Integer parentRowNumber;
    Long sid;

    List<RawCell> cells = new ArrayList();

    RawCell find(String columnName) {
        RawCell found = null;

        for(RawCell cell : cells) {
            if (cell.getColumnName().equals(columnName)) {
                found = cell;
                break;
            }
        }

        return found;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getReplicate() {
        return replicate;
    }

    public void setReplicate(Integer replicate) {
        this.replicate = replicate;
    }

    public Integer getParentRowNumber() {
        return parentRowNumber;
    }

    public void setParentRowNumber(Integer parentRowNumber) {
        this.parentRowNumber = parentRowNumber;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public List<RawCell> getCells() {
        return cells;
    }

    public void setCells(List<RawCell> cells) {
        this.cells = cells;
    }
}
