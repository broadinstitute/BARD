package bard.util.dbutil;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.handlers.AbstractKeyedHandler;

public class SimpleMapHandler extends AbstractKeyedHandler {

	private int keyColumnIndex, valueColumnIndex;
	private String keyColumnName, valueColumnName;

	public SimpleMapHandler() {
		this(1, 2);
	}

	public SimpleMapHandler(int keyColumnIndex, int valueColumnIndex) {
		this.keyColumnIndex = keyColumnIndex;
		this.valueColumnIndex = valueColumnIndex;
	}

	@Override
	protected Object createKey(ResultSet rs) throws SQLException {
		return (keyColumnName == null) ? rs.getObject(keyColumnIndex) : rs.getObject(keyColumnName);
	}

	@Override
	protected Object createRow(ResultSet rs) throws SQLException {
		return (valueColumnName == null) ? rs.getObject(valueColumnIndex) : rs.getObject(valueColumnName);
	}
}