package bard.db.experiment

import bard.db.registration.Assay
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ProjectAssay implements Serializable {
	
		Stage stage
		Integer sequenceNo
		Float promotionThreshold
		String promotionCriteria
		Date dateCreated
		Date lastUpdated
		String modifiedBy
		Project project
		Assay assay
	
		int hashCode() {
			def builder = new HashCodeBuilder()
			builder.append assay
			builder.append project
			builder.toHashCode()
		}
	
		boolean equals(other) {
			if (other == null) return false
			def builder = new EqualsBuilder()
			builder.append assay, other.assay
			builder.append project, other.project
			builder.isEquals()
		}
	
		static belongsTo = [Assay, Project]
	
		static mapping = {
			id composite: ["assay", "project"]
			stage column: "stage"
		}
	
		static constraints = {
			stage maxSize: 20
			sequenceNo nullable: true
			promotionThreshold nullable: true
			promotionCriteria nullable: true, maxSize: 1000
			dateCreated maxSize: 19
			lastUpdated nullable: true, maxSize: 19
			modifiedBy nullable: true, maxSize: 40
		}
	}
	

/*
class ProjectAssay {

	Stage stage
	Integer sequenceNo
	Float promotionThreshold
	String promotionCriteria
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	Project project
	Assay assay

	static belongsTo = [Assay, Project]

	static mapping = {
        stage column: "stage"
	}

	static constraints = {
		sequenceNo nullable: true
		promotionThreshold nullable: true
		promotionCriteria nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
        project nullable: false
        assay nullable: false
	}
}
*/