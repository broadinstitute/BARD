package org.jfree.report.modules.misc.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WritableScrollableResultSetTableModel extends ScrollableResultSetTableModel {

	public WritableScrollableResultSetTableModel(final ResultSet resultset, final boolean labelMapMode) throws SQLException {
		super(resultset, labelMapMode);
	}
	
	@Override
	public void setValueAt(Object ob, int row, int column) {
		try {
			int r = resultset.getRow();
			int c = this.getRowCount();
			resultset.absolute(row + 1);
			if (ob == null) {
				resultset.updateNull(column + 1);
			} else {
				resultset.updateObject(column + 1, ob);
			}
			resultset.updateRow();
			this.fireTableCellUpdated(row, column);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return true;
	}
}
