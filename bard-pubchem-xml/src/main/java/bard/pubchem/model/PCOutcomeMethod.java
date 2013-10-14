package bard.pubchem.model;

import java.util.HashMap;
import java.util.Map;

public class PCOutcomeMethod {

	private static Map<String, Integer> outcomeIds = new HashMap();
	private static Map<String, String> outcomeNames = new HashMap();
	static {
		PCOutcomeMethod[] outcomes = new PCOutcomeMethod[] { new PCOutcomeMethod(0, "other", "Other"),
				new PCOutcomeMethod(1, "screening", "Screening"), new PCOutcomeMethod(2, "confirmatory", "Confirmatory"),
				new PCOutcomeMethod(3, "summary", "Summary"), };

		for (PCOutcomeMethod outcome : outcomes)
			outcomeNames.put(outcome.getKey(), outcome.getName());
		for (PCOutcomeMethod outcome : outcomes)
			outcomeIds.put(outcome.getKey(), outcome.getId());
	}

	public static String getActivityOutcomeMethodFormatted(String method) {
		return outcomeNames.get(method);
	}
	public static Integer getActivityOutcomeMethodId(String method) {
		return outcomeIds.get(method);
	}

	private Integer id;

	private String key;

	private String name;

	public PCOutcomeMethod(Integer id, String key, String name) {
		super();
		this.id = id;
		this.key = key;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}
}