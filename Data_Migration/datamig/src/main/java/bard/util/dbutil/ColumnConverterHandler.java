package bard.util.dbutil;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.balusc.util.ObjectConverter;

import org.apache.commons.dbutils.handlers.AbstractListHandler;

public class ColumnConverterHandler extends AbstractListHandler {

	private final int columnIndex;
	private final Class toClass;

	public ColumnConverterHandler(int columnIndex, Class toClass) {
		this.columnIndex = columnIndex;
		this.toClass = toClass;
	}

	protected Object handleRow(ResultSet rs) throws SQLException {
		return ObjectConverter.convert(rs.getObject(columnIndex), toClass);
	}
}