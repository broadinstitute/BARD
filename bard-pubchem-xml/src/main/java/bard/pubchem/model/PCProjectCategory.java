package bard.pubchem.model;

import java.util.HashMap;
import java.util.Map;

public class PCProjectCategory {

	private static Map<String, Integer> projectIds = new HashMap();
	private static Map<String, String> projectNames = new HashMap();
	static {
		PCProjectCategory[] categories = new PCProjectCategory[] { new PCProjectCategory(1, "mlscn", "MLSCN"),
				new PCProjectCategory(2, "mlpcn", "MLPCN"), new PCProjectCategory(3, "mlscn-ap", "MLSCN (Assay Provider)"),
				new PCProjectCategory(4, "mlpcn-ap", "MLPCN (Assay Provider)"),
				new PCProjectCategory(7, "literature-extracted", "Literature (Extracted)"),
				new PCProjectCategory(8, "literature-author", "Literature (Author)"), new PCProjectCategory(10, "rnaigi", "RNAi Global Initiative"),
				new PCProjectCategory(6, "assay-vendor", "Assay Vendor"), new PCProjectCategory(255, "other", "Other") };
		for (PCProjectCategory category : categories)
			projectNames.put(category.getKey(), category.getName());
		for (PCProjectCategory category : categories)
			projectIds.put(category.getKey(), category.getId());
	}

	public static String getProjectCategoryFormatted(String category) {
		return projectNames.get(category);
	}
	public static Integer getProjectCategoryId(String category) {
		return projectIds.get(category);
	}

	private Integer id;

	private String key;

	private String name;

	public PCProjectCategory(Integer id, String key, String name) {
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