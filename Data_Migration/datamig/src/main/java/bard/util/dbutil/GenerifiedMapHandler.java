package bard.util.dbutil;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.handlers.AbstractKeyedHandler;

public class GenerifiedMapHandler<K,V> extends AbstractKeyedHandler {

	private int keyColumnIndex;
	private int valueColumnIndex;

	public GenerifiedMapHandler() {
		this(1,2);
	}
	
	public GenerifiedMapHandler(int keyColumnIndex, int valueColumnIndex) {
		this.keyColumnIndex = keyColumnIndex;
		this.valueColumnIndex = valueColumnIndex;
	}
	
	@Override
	protected K createKey(ResultSet rs) throws SQLException {
		return (K) rs.getObject(keyColumnIndex);
	}

	@Override
	protected V createRow(ResultSet rs) throws SQLException {
		return (V) rs.getObject(valueColumnIndex);
	}
}