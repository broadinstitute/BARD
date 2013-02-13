package bard.util.db;

public class ResultBean {
	
	public Long resultId;
	public Integer aid;
	public Integer tid;
	
	public String tidName;
	public Integer seriesNo;
	public Integer parentTid;
	public Integer getSeriesNo() {
		return seriesNo;
	}
	public void setSeriesNo(Integer seriesNo) {
		this.seriesNo = seriesNo;
	}
	public String relationship;
	public String resultType;
	public Integer contextTid;
	public String contextItem;
	public Integer qualifierTid;
	public Double concentration;
	public String concentrationUnit;
	public String stats_modifier;
	public Long resultTypeId;
	public Long elementId;
	
	public Integer getAid() {
		return aid;
	}
	public Double getConcentration() {
		return concentration;
	}
	public String getConcentrationUnit() {
		return concentrationUnit;
	}
	public String getContextItem() {
		return contextItem;
	}
	
	public Integer getContextTid() {
		return contextTid;
	}
	public Integer getParentTid() {
		return parentTid;
	}
	public Integer getQualifierTid() {
		return qualifierTid;
	}
	public String getRelationship() {
		return relationship;
	}
	public Long getResultId() {
		return resultId;
	}
	public String getResultType() {
		return resultType;
	}
	public String getStats_modifier() {
		return stats_modifier;
	}
	public Integer getTid() {
		return tid;
	}
	public String getTidName() {
		return tidName;
	}
	public void setAid(Integer aid) {
		this.aid = aid;
	}
	public void setConcentration(Double concentration) {
		this.concentration = concentration;
	}
	public void setConcentrationUnit(String concentrationUnit) {
		this.concentrationUnit = concentrationUnit;
	}
	public void setContextItem(String contextItem) {
		this.contextItem = contextItem;
	}
	public void setContextTid(Integer contextTid) {
		this.contextTid = contextTid;
	}
	public void setParentTid(Integer parentTid) {
		this.parentTid = parentTid;
	}
	public void setQualifierTid(Integer qualifierTid) {
		this.qualifierTid = qualifierTid;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	public void setResultId(Long resultId) {
		this.resultId = resultId;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public void setStats_modifier(String stats_modifier) {
		this.stats_modifier = stats_modifier;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public void setTidName(String tidName) {
		this.tidName = tidName;
	}
}