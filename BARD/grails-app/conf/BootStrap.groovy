import bard.db.dictionary.AssayDescriptor
import bard.db.experiment.GroupType
import bard.db.experiment.Project
import bard.db.experiment.ProjectAssay
import bard.db.dictionary.Stage
import grails.converters.JSON
import groovy.sql.Sql
import org.codehaus.groovy.grails.commons.ApplicationHolder

import javax.servlet.ServletContext
import javax.sql.DataSource

import bard.db.registration.*
import bard.db.dictionary.ResultType
import bard.db.dictionary.BiologyDescriptor
import bard.db.dictionary.InstanceDescriptor

class BootStrap {
	
	def init = { servletContext ->
	}
	def destroy = {
	}
	
	/*

	DataSource dataSource

	def init = { ServletContext ctx ->

		JSON.registerObjectMarshaller(Assay) { Assay it ->
			def returnArray = [:]
			returnArray['id'] = it.id
			returnArray['assayName'] = it.assayName
			returnArray['assayStatus'] = it.assayStatus
			returnArray['assayVersion'] = it.assayVersion
			returnArray['description'] = it.description
			returnArray['measureContextItems'] = it.measureContextItems
			returnArray['projectAssays'] = it.projectAssays
			returnArray['protocols'] = it.protocols
			returnArray['experiments'] = []
			returnArray['designedBy'] = it.designedBy
			return returnArray
		}

		JSON.registerObjectMarshaller(AssayDescriptor) { AssayDescriptor it ->
			def returnArray = [:]
			returnArray['elementId'] = it.elementId
			returnArray['elementStatus'] = it.elementStatus
			returnArray['label'] = it.label
			returnArray['description'] = it.description
			returnArray['abbreviation'] = it.abbreviation
			returnArray['acronym'] = it.acronym
			returnArray['synonyms'] = it.synonyms
			returnArray['externalURL'] = it.externalURL
			returnArray['unit'] = it.unit?.unit
			return returnArray
		}

		JSON.registerObjectMarshaller(MeasureContext) { MeasureContext it ->
			def returnArray = [:]
			returnArray['id'] = it.id
			returnArray['contextName'] = it.contextName
			returnArray['measureContextItems'] = it.measureContextItems
			return returnArray
		}

		JSON.registerObjectMarshaller(MeasureContextItem) { MeasureContextItem it ->
			def returnArray = [:]
			returnArray['id'] = it.id
			returnArray['measureContextId'] = it.measureContextId
			returnArray['attribute'] = it.attributeElement.label
			returnArray['value'] = it.valueDisplay
			return returnArray
		}

		JSON.registerObjectMarshaller(AssayStatus) { AssayStatus it ->
			def returnArray = [:]
			returnArray['id'] = it.id
			returnArray['status'] = it.status
			return returnArray
		}

		JSON.registerObjectMarshaller(Stage) { Stage it ->
			def returnArray = [:]
			returnArray['elementId'] = it.elementId
			returnArray['stage'] = it.stage
			returnArray['description'] = it.description
			returnArray['children'] = it.children
			return returnArray
		}

		JSON.registerObjectMarshaller(ProjectAssay) { ProjectAssay it ->
			def returnArray = [:]
			returnArray['id'] = it.id
			returnArray['assayId'] = it.assayId
			returnArray['assay'] = it.assay
			returnArray['project'] = it.project
			returnArray['projectId'] = it.projectId
			returnArray['sequenceNo'] = it.sequenceNo
			return returnArray
		}

		JSON.registerObjectMarshaller(Project) { Project it ->
			def returnArray = [:]
			returnArray['id'] = it.id
			returnArray['description'] = it.description
			returnArray['groupType'] = it.groupType
			returnArray['projectName'] = it.projectName
			return returnArray
		}

		environments {
			development {
				createTestData()
			}

			test {
				createTestData()
			}
		}
	}
	def destroy = {
	}

	void createTestData() {
		// TODO guard against accidentally creating duplicates by using an if block
		def defaultStatus = new AssayStatus(status:"Pending").save();
		new AssayStatus(status:"Active").save();
		new AssayStatus(status:"Superceded").save();
		new AssayStatus(status:"Retired").save();

		new ElementStatus(elementStatus: 'Pending', capability: 'Element is new, not yet approved but can be used for assasy definition and data entry subject to future curation and approval').save()
		new ElementStatus(elementStatus: 'Published', capability: 'Element can be used for any assay definiton or data upload').save()
		new ElementStatus(elementStatus: 'Deprecated', capability: 'Element has been replaced by a another one.  It should not be used in new assasy definitions, but can be used when uploading new experiments.  It is subject to future retirement').save()
		new ElementStatus(elementStatus: 'Retired', capability: 'Element has been retired and must not be used for new assay definitions.  It can be used for uploading experiment data').save()

//        def stage = new Stage(id: 50, stage:"Primary", description: "a Primary HTS assay").save()
//        assert stage.validate()

		def project = new Project(projectName: "Test", groupType: GroupType.Project, description: "Just a test").save()
		assert project.validate()

		Sql sql = new Sql(dataSource)
		def sqlFilePaths = ["resources/element_network_load2.sql", "resources/element_hierarchy_load.sql",
				"resources/biology_descriptor_load.sql", "resources/assay_descriptor_load.sql",
				"resources/instance_descriptor_load.sql", "resources/result_type_load.sql", "resources/stage_load.sql"]
		sqlFilePaths.each { String sqlFilePath ->
			def sqlString = ApplicationHolder.application.parentContext.getResource("classpath:$sqlFilePath").inputStream.text
			sqlString.eachLine {
				sql.execute(it)
			}
		}

		def assay = new Assay(assayName: "Test from BootStrap", assayVersion: 1, assayStatus: defaultStatus, description: "Test").save();
		assert assay.validate()

//        assert new ProjectAssay(assay: assay, project: project, stage: stage).save()

		assert new Protocol(assay: assay, protocolName: "Test Protocol").save()

		AssayDescriptor assayDescriptor = AssayDescriptor.findByLabel('nucleotide')
		AssayDescriptor assayDescriptor2 = AssayDescriptor.findByLabel('assay kit')
		BiologyDescriptor biologyDescriptor = BiologyDescriptor.findByLabel('molecular target')
		InstanceDescriptor instanceDescriptor = InstanceDescriptor.findByLabel('small-molecule perturbagen')
		ResultType resultType = ResultType.findByResultTypeName('IC50')

		MeasureContextItem assayItem = new MeasureContextItem(attributeElement: assayDescriptor.element, attributeType: AttributeType.Number)
		MeasureContextItem assayKitItem = new MeasureContextItem(attributeElement: assayDescriptor2.element, attributeType: AttributeType.Number)
		MeasureContextItem biologyItem = new MeasureContextItem(attributeElement: biologyDescriptor.element, attributeType: AttributeType.Number)
		MeasureContextItem instanceItem = new MeasureContextItem(attributeElement: instanceDescriptor.element, attributeType: AttributeType.Number)

		assay.addToMeasureContextItems(assayItem)
		assay.addToMeasureContextItems(assayKitItem)
		assay.addToMeasureContextItems(biologyItem)
		assay.addToMeasureContextItems(instanceItem)

		assert assayItem.validate()
		assert assayKitItem.validate()
		assert biologyItem.validate()
		assert instanceItem.validate()

		String contextName = "Context for IC50"
		MeasureContext context = new MeasureContext(contextName: contextName)
		assay.addToMeasureContexts(context)
		assert context.validate()

		Measure measure = new Measure(resultType: resultType)
		context.addToMeasures(measure)
		assert measure.validate()

	}
	
	*/
}
