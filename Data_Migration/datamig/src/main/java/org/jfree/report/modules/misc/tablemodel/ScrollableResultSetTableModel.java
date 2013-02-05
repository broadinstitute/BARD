package org.jfree.report.modules.misc.tablemodel;

/**
 * ========================================
 * JFreeReport : a free Java report library
 * ========================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * $Id: ScrollableResultSetTableModel.java,v 1.11 2007/04/01 18:49:32 taqua Exp $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;

import org.jfree.util.Log;

/**
 * A tableModel which is backed up by a java.sql.ResultSet. Use this to directly
 * feed your database data into JFreeReport. If you have trouble using this
 * TableModel and you have either enough memory or your query result is not
 * huge, you may want to use
 * <code>ResultSetTableModelFactory.generateDefaultTableModel (ResultSet rs)</code>
 * . That implementation will read all data from the given ResultSet and keep
 * that data in memory.
 * <p/>
 * Use the close() function to close the ResultSet contained in this model.
 * 
 * @author Thomas Morgner
 */
public class ScrollableResultSetTableModel extends AbstractTableModel implements CloseableTableModel {
	/**
	 * The scrollable ResultSet source.
	 */
	protected ResultSet resultset;
	/**
	 * The ResultSetMetaData object for this result set.
	 */
	private ResultSetMetaData dbmd;
	/**
	 * The number of rows in the result set.
	 */
	private int rowCount;
	/**
	 * Defines the column naming mode.
	 */
	private final boolean labelMapMode;
	/**
	 * The column types as read from the result set.
	 */
	private Class[] types;

	/**
	 * Constructs the model.
	 * 
	 * @param resultset
	 *            the result set.
	 * @param labelMapMode
	 *            defines, whether to use column names or column labels to
	 *            compute the column index.
	 * @throws SQLException
	 *             if there is a problem with the result set.
	 */
	public ScrollableResultSetTableModel(final ResultSet resultset, final boolean labelMapMode) throws SQLException {
		this.labelMapMode = labelMapMode;
		if (resultset != null) {
			updateResultSet(resultset);
		} else {
			close();
		}
	}

	/**
	 * Creates a new scrollable result set with no resultset assigned and the
	 * specified label map mode.
	 * 
	 * @param labelMapMode
	 *            defines, whether to use column names or column labels to
	 *            compute the column index.
	 */
	protected ScrollableResultSetTableModel(final boolean labelMapMode) {
		this.labelMapMode = labelMapMode;
	}

	/**
	 * Returns the column name mode used to map column names into column
	 * indices. If true, then the Label is used, else the Name is used.
	 * 
	 * @return true, if the column label is used for the mapping, false
	 *         otherwise.
	 * 
	 * @see ResultSetMetaData#getColumnLabel
	 * @see ResultSetMetaData#getColumnName
	 */
	public boolean isLabelMapMode() {
		return labelMapMode;
	}

	/**
	 * Updates the result set in this model with the given ResultSet object.
	 * 
	 * @param resultset
	 *            the new result set.
	 * @throws SQLException
	 *             if there is a problem with the result set.
	 */
	public void updateResultSet(final ResultSet resultset) throws SQLException {
		if (this.resultset != null) {
			close();
		}

		this.resultset = resultset;
		this.dbmd = resultset.getMetaData();

		if (resultset.last()) {
			rowCount = resultset.getRow();
		} else {
			rowCount = 0;
		}

		fireTableStructureChanged();
	}

	/**
	 * Clears the model of the current result set. The resultset is closed.
	 */
	public void close() {
		// Close the old result set if needed.
		if (resultset != null) {
			try {
				resultset.close();
			} catch (SQLException e) {
				// Just in case the JDBC driver can't close a result set twice.
				// e.printStackTrace();
			}
		}
		resultset = null;
		dbmd = null;
		rowCount = 0;
		fireTableStructureChanged();
	}

	/**
	 * Get a rowCount. This can be a very expensive operation on large datasets.
	 * Returns -1 if the total amount of rows is not known to the result set.
	 * 
	 * @return the row count.
	 */
	public int getRowCount() {
		if (resultset == null) {
			return 0;
		}

		try {
			if (resultset.last()) {
				rowCount = resultset.getRow();
				if (rowCount == -1) {
					rowCount = 0;
				}
			}
			else {
				rowCount = 0;
			}
		} catch (SQLException sqle) {
			// Log.debug ("GetRowCount failed, returning 0 rows", sqle);
			return 0;
		}
		return rowCount;
	}

	/**
	 * Returns the number of columns in the ResultSet. Returns 0 if no result
	 * set is set or the column count could not be retrieved.
	 * 
	 * @return the column count.
	 * 
	 * @see java.sql.ResultSetMetaData#getColumnCount()
	 */
	public int getColumnCount() {
		if (resultset == null) {
			return 0;
		}

		if (dbmd != null) {
			try {
				return dbmd.getColumnCount();
			} catch (SQLException e) {
				// Log.debug ("GetColumnCount failed", e);
			}
		}
		return 0;
	}

	/**
	 * Returns the columnLabel or column name for the given column. Whether the
	 * label or the name is returned depends on the label map mode.
	 * 
	 * @param column
	 *            the column index.
	 * @return the column name.
	 * 
	 * @see java.sql.ResultSetMetaData#getColumnLabel(int)
	 */
	public String getColumnName(final int column) {
		if (dbmd != null) {
			try {
				if (isLabelMapMode()) {
					return dbmd.getColumnLabel(column + 1);
				} else {
					return dbmd.getColumnName(column + 1);
				}
			} catch (SQLException e) {
				Log.info("ScrollableResultSetTableModel.getColumnName: SQLException.");
			}
		}
		return null;
	}

	/**
	 * Returns the value of the specified row and the specified column from
	 * within the resultset.
	 * 
	 * @param row
	 *            the row index.
	 * @param column
	 *            the column index.
	 * @return the value.
	 */
	public Object getValueAt(final int row, final int column) {
		if (resultset != null) {
			try {
				resultset.absolute(row + 1);
				return resultset.getObject(column + 1);
			} catch (SQLException e) {
				// Log.debug ("Query failed for [" + row + "," + column + "]",
				// e);
			}
		}
		return null;
	}

	/**
	 * Returns the class of the resultset column. Returns Object.class if an
	 * error occurred.
	 * 
	 * @param column
	 *            the column index.
	 * @return the column class.
	 */
	public Class getColumnClass(final int column) {
		if (types != null) {
			return types[column];
		}
		if (dbmd != null) {
			try {
				types = TypeMapper.mapTypes(dbmd);
				return types[column];
			} catch (Exception e) {
				// Log.debug ("GetColumnClass failed for " + column, e);
			}
		}
		return Object.class;
	}

	/**
	 * Returns the classname of the resultset column. Returns Object.class if an
	 * error occurred.
	 * 
	 * @param column
	 *            the column index.
	 * @return the column class name.
	 */
	public String getColumnClassName(final int column) {
		if (dbmd != null) {
			return mckoiDBFixClassName(getColumnClass(column).getName());
		}
		return Object.class.getName();
	}

	/**
	 * Just removes the word class from the start of the classname string
	 * McKoiDB version 0.92 was not able to properly return classnames of
	 * resultset elements.
	 * 
	 * @param classname
	 *            the class name.
	 * @return the modified class name.
	 */
	private String mckoiDBFixClassName(final String classname) {
		if (classname.startsWith("class ")) {
			return classname.substring(6).trim();
		}
		return classname;
	}
}