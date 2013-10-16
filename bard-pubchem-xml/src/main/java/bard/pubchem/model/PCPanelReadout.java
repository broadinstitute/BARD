package bard.pubchem.model;

import java.util.HashMap;
import java.util.Map;

public class PCPanelReadout {

	private static Map<String, Integer> readoutIds = new HashMap();
	static {
		PCPanelReadout[] types = new PCPanelReadout[] { new PCPanelReadout(1, "regular"), new PCPanelReadout(2, "outcome"),
				new PCPanelReadout(3, "score"), new PCPanelReadout(4, "ac") };
		for (PCPanelReadout type : types)
			readoutIds.put(type.getValue(), type.getId());
	}

	public static Integer getReadoutTypeId(String readoutType) {
		return readoutIds.get(readoutType);
	}

	private Integer id;

	private String value;

	public PCPanelReadout(Integer id, String value) {
		super();
		this.id = id;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
