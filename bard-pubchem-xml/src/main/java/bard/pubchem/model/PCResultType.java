package bard.pubchem.model;

import java.util.HashMap;
import java.util.Map;

public class PCResultType {

	private static Map<String, Integer> resultTypeIds = new HashMap();
	static {
		PCResultType[] resultTypes = new PCResultType[] { new PCResultType(1, "float"), new PCResultType(2, "int"), new PCResultType(3, "bool"),
				new PCResultType(4, "string") };
		for (PCResultType resultType : resultTypes)
			resultTypeIds.put(resultType.getValue(), resultType.getId());
	}

	public static Integer getResultTypeId(String resultType) {
		return resultTypeIds.get(resultType);
	}

	private Integer id;

	private String value;

	public PCResultType(Integer id, String value) {
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
