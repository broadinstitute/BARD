package bard.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.balusc.util.ObjectConverter;

import org.apache.commons.dbutils.handlers.AbstractListHandler;

public class ColumnConverterHandler<T> extends AbstractListHandler<T> {

	private final int columnIndex;
	private final Class<T> toClass;

	public ColumnConverterHandler(int columnIndex, Class<T> toClass) {
		this.columnIndex = columnIndex;
		this.toClass = toClass;
	}

	protected T handleRow(ResultSet rs) throws SQLException {
		return ObjectConverter.convert(rs.getObject(columnIndex), toClass);
	}
}