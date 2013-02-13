package bard.util.dbutil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.dbutils.handlers.AbstractKeyedHandler;

public class MultiKeyMapHandler<V> extends AbstractKeyedHandler {

	private int[] keyColumnIndicies;
	int valueColumnIndex;

	public MultiKeyMapHandler() {
		this(new int[] { 1,2 }, 3);
	}

	public MultiKeyMapHandler(int[] keyColumnIndicies, int valueColumnIndex) {
		this.keyColumnIndicies = keyColumnIndicies;
		this.valueColumnIndex = valueColumnIndex;
	}

	@Override
	protected Map createMap() {
		return new MultiKeyMap();
	}
	
	@Override
	protected Object createKey(ResultSet rs) throws SQLException {
		List<Object> list = new ArrayList(keyColumnIndicies.length);
		for (int idx : keyColumnIndicies) {
			Object obj = rs.getObject(idx);
//			obj = obj == null ? "" : obj;
			list.add(obj);
		}
		MultiKey key = new MultiKey(list.toArray());
		return key;
	}

	@Override
	protected V createRow(ResultSet rs) throws SQLException {
		return (V) rs.getObject(valueColumnIndex);
	}
}