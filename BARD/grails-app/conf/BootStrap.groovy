import bard.db.dictionary.ElementStatus
import bard.db.dictionary.ResultType
import bard.db.experiment.GroupType
import bard.db.experiment.Project
import bard.db.experiment.ProjectAssay
import bard.db.experiment.Stage
import bard.db.util.Unit
import grails.converters.JSON
import groovy.sql.Sql
import org.codehaus.groovy.grails.commons.ApplicationAttributes
import org.codehaus.groovy.grails.commons.ApplicationHolder

import javax.servlet.ServletContext
import javax.sql.DataSource

import bard.db.registration.*

class BootStrap {

    def init = { ServletContext ctx ->

        JSON.registerObjectMarshaller(Assay) { Assay it ->
            def returnArray = [:]
            returnArray['id'] = it.id
            returnArray['assayName'] = it.assayName
            returnArray['assayStatus'] = it.assayStatus
            returnArray['assayVersion'] = it.assayVersion
            returnArray['description'] = it.description
            returnArray['measures'] = it.measures
            returnArray['projectAssays'] = it.projectAssays
            returnArray['protocols'] = it.protocols
            returnArray['experiments'] = it.experiments
            returnArray['designedBy'] = it.designedBy
            return returnArray
        }

        JSON.registerObjectMarshaller(Measure) { Measure it ->
            def returnArray = [:]
            returnArray['id'] = it.id
            returnArray['measureContext'] = it.measureContext
            returnArray['entryUnit'] = it.entryUnit
            returnArray['assayId'] = it.assayId
            returnArray['resultTypeId'] = it.resultTypeId
            returnArray['resultTypeName'] = it.resultType?.resultTypeName
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
            returnArray['groupNo'] = it.groupNo
            returnArray['assayId'] = it.assayId
            return returnArray
        }

        JSON.registerObjectMarshaller(AssayStatus) { AssayStatus it ->
            def returnArray = [:]
            returnArray['id'] = it.id
            returnArray['status'] = it.status
            return returnArray
        }

        JSON.registerObjectMarshaller(ProjectAssay) { ProjectAssay it ->
            def returnArray = [:]
            returnArray['id'] = it.id
            returnArray['stage'] = it.stage
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

                // TODO guard against accidentally creating duplicates by using an if block
                def defaultStatus = new AssayStatus(status:"Pending").save();
                new AssayStatus(status:"Active").save();
                new AssayStatus(status:"Superceded").save();
                new AssayStatus(status:"Retired").save();

                new ElementStatus(elementStatus: 'Pending', capability: 'Element is new, not yet approved but can be used for assasy definition and data entry subject to future curation and approval').save()
                new ElementStatus(elementStatus: 'Published', capability: 'Element can be used for any assay definiton or data upload').save()
                new ElementStatus(elementStatus: 'Deprecated', capability: 'Element has been replaced by a another one.  It should not be used in new assasy definitions, but can be used when uploading new experiments.  It is subject to future retirement').save()
                new ElementStatus(elementStatus: 'Retired', capability: 'Element has been retired and must not be used for new assay definitions.  It can be used for uploading experiment data').save()

                def unit = new Unit(unit:"uM", description: "micromolar").save()
                new Unit(unit:"mL", description: "milliLiters").save()

                def stage = new Stage(stage:"Primary", description: "a Primary HTS assay").save()

                def project = new Project(projectName: "Test", groupType: GroupType.PROJECT, description: "Just a test").save()
                assert project.validate()

                def appCtx = ctx.getAttribute(ApplicationAttributes.APPLICATION_CONTEXT)
                DataSource dataSource = appCtx.getBean("dataSource")
                Sql sql = new Sql(dataSource)
                def sqlFilePath = "resources/element_data_load.sql"
                def sqlString = ApplicationHolder.application.parentContext.getResource("classpath:$sqlFilePath").inputStream.text
                sqlString.eachLine {
                    sql.execute(it)
                }

                def assay = new Assay(assayName: "Test from BootStrap", assayVersion: 1, assayStatus: defaultStatus, description: "Test").save();
                assert assay.validate()

                assert new ProjectAssay(assay: assay, project: project, stage: stage).save()

                assert new Protocol(assay: assay, protocolName: "Test Protocol").save()

                def measureContext = new MeasureContext(contextName: "PI Context").save()

                assert new Measure(assay: assay, resultType: ResultType.get(339), entryUnit: unit, measureContext: measureContext).save()
            }
        }
    }
    def destroy = {
    }
}
