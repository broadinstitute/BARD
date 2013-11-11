package bard.db.experiment

import bard.db.audit.BardContextUtils
import bard.db.people.Role
import bard.db.registration.Assay
import bardqueryapi.TableModel
import com.fasterxml.jackson.databind.ObjectMapper
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import org.junit.Before
import spock.lang.Unroll

import java.util.zip.GZIPOutputStream

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ExperimentServiceIntegrationSpec extends IntegrationSpec {

    ExperimentService experimentService
    SessionFactory sessionFactory
    ObjectMapper mapper = new ObjectMapper()

    String RESULTS_JSON_1 = '''
{"sid":49828991,"rootElem":[{"resultTypeId":896,"resultType":"PubChem outcome","valueDisplay":"Inactive","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":0.0,"valueDisplay":"0.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":896,"resultType":"PubChem outcome","valueDisplay":"Inactive","relationship":"calculated from","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":0.0,"valueDisplay":"0.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]},{"resultTypeId":896,"resultType":"PubChem outcome","valueNum":1.0,"replicateNumber":1,"valueDisplay":"1.0","qualifier":"=","relationship":"calculated from","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":0.0,"replicateNumber":1,"valueDisplay":"0.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]}],"contextItems":[]},{"resultTypeId":961,"resultType":"EC50","valueNum":133.3,"replicateNumber":1,"valueDisplay":">133.3 uM","qualifier":">","related":[{"resultTypeId":986,"resultType":"percent activity","valueNum":-3.502,"replicateNumber":1,"valueDisplay":"-3.502 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.104,"valueDisplay":"0.104 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":0.298,"replicateNumber":1,"valueDisplay":"0.298 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.208,"valueDisplay":"0.208 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-2.036,"replicateNumber":1,"valueDisplay":"-2.036 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.417,"valueDisplay":"0.417 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-2.916,"replicateNumber":1,"valueDisplay":"-2.916 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.833,"valueDisplay":"0.833 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":2.371,"replicateNumber":1,"valueDisplay":"2.371 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":1.667,"valueDisplay":"1.667 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":6.435,"replicateNumber":1,"valueDisplay":"6.435 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":3.333,"valueDisplay":"3.333 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":7.969,"replicateNumber":1,"valueDisplay":"7.969 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":6.667,"valueDisplay":"6.667 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":8.125,"replicateNumber":1,"valueDisplay":"8.125 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":13.333,"valueDisplay":"13.333 uM"}]},{"resultTypeId":986,"statsModifierId":1572,"resultType":"percent activity (maximum)","valueNum":8.125,"replicateNumber":1,"valueDisplay":"8.125 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[{"attribute":"number of points","attributeId":1397,"qualifier":"=","valueNum":24.0,"valueDisplay":"24.0"}]}]}
'''
    String RESULTS_JSON_2 = '''
{"sid":4240489,"rootElem":[{"resultTypeId":896,"resultType":"PubChem outcome","valueDisplay":"Active","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":70.0,"valueDisplay":"70.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":896,"resultType":"PubChem outcome","valueDisplay":"Active","relationship":"calculated from","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":70.0,"valueDisplay":"70.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]},{"resultTypeId":896,"resultType":"PubChem outcome","valueNum":2.0,"replicateNumber":1,"valueDisplay":"2.0","qualifier":"=","relationship":"calculated from","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":70.0,"replicateNumber":1,"valueDisplay":"70.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]}],"contextItems":[]},{"resultTypeId":961,"resultType":"EC50","valueNum":0.104,"replicateNumber":1,"valueDisplay":"<0.104 uM","qualifier":"<","related":[{"resultTypeId":921,"resultType":"Hill sinf","valueNum":-73.13,"replicateNumber":1,"valueDisplay":"-73.13","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-36.37,"replicateNumber":1,"valueDisplay":"-36.37 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.104,"valueDisplay":"0.104 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-59.52,"replicateNumber":1,"valueDisplay":"-59.52 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.208,"valueDisplay":"0.208 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-63.94,"replicateNumber":1,"valueDisplay":"-63.94 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.417,"valueDisplay":"0.417 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-70.83,"replicateNumber":1,"valueDisplay":"-70.83 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.833,"valueDisplay":"0.833 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-68.82,"replicateNumber":1,"valueDisplay":"-68.82 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":1.667,"valueDisplay":"1.667 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-72.01,"replicateNumber":1,"valueDisplay":"-72.01 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":3.333,"valueDisplay":"3.333 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-78.93,"replicateNumber":1,"valueDisplay":"-78.93 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":6.667,"valueDisplay":"6.667 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-75.14,"replicateNumber":1,"valueDisplay":"-75.14 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":13.333,"valueDisplay":"13.333 uM"}]},{"resultTypeId":962,"resultType":"log EC50","valueNum":-6.991,"replicateNumber":1,"valueDisplay":"-6.991 uM","qualifier":"=","relationship":"supported by","related":[{"resultTypeId":1335,"resultType":"standard error","valueNum":0.03111,"replicateNumber":1,"valueDisplay":"0.03111","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]},{"resultTypeId":986,"statsModifierId":1572,"resultType":"percent activity (maximum)","valueNum":-78.93,"replicateNumber":1,"valueDisplay":"-78.93 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":920,"resultType":"Hill s0","valueNum":0.0,"replicateNumber":1,"valueDisplay":"0.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":919,"resultType":"Hill coefficient","valueNum":1.668,"replicateNumber":1,"valueDisplay":"1.668","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[{"attribute":"number of points","attributeId":1397,"qualifier":"=","valueNum":24.0,"valueDisplay":"24.0"}]}]}
'''
    String RESULTS_JSON_3 = '''
{"sid":22404661,"rootElem":[{"resultTypeId":896,"resultType":"PubChem outcome","valueDisplay":"Active","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":50.0,"valueDisplay":"50.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":896,"resultType":"PubChem outcome","valueDisplay":"Active","relationship":"calculated from","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":50.0,"valueDisplay":"50.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]},{"resultTypeId":896,"resultType":"PubChem outcome","valueNum":2.0,"replicateNumber":1,"valueDisplay":"2.0","qualifier":"=","relationship":"calculated from","related":[{"resultTypeId":898,"resultType":"PubChem activity score","valueNum":50.0,"replicateNumber":1,"valueDisplay":"50.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]}],"contextItems":[]},{"resultTypeId":961,"resultType":"EC50","valueNum":10.5,"replicateNumber":1,"valueDisplay":"10.5 uM","qualifier":"=","related":[{"resultTypeId":921,"resultType":"Hill sinf","valueNum":-100.0,"replicateNumber":1,"valueDisplay":"-100.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-4.64,"replicateNumber":1,"valueDisplay":"-4.64 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.104,"valueDisplay":"0.104 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-1.171,"replicateNumber":1,"valueDisplay":"-1.171 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.208,"valueDisplay":"0.208 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-0.2344,"replicateNumber":1,"valueDisplay":"-0.2344 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.417,"valueDisplay":"0.417 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-3.492,"replicateNumber":1,"valueDisplay":"-3.492 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":0.833,"valueDisplay":"0.833 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":0.6566,"replicateNumber":1,"valueDisplay":"0.6566 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":1.667,"valueDisplay":"1.667 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-2.261,"replicateNumber":1,"valueDisplay":"-2.261 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":3.333,"valueDisplay":"3.333 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-50.7,"replicateNumber":1,"valueDisplay":"-50.7 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":6.667,"valueDisplay":"6.667 uM"}]},{"resultTypeId":986,"resultType":"percent activity","valueNum":-50.76,"replicateNumber":1,"valueDisplay":"-50.76 %","qualifier":"=","relationship":"calculated from","related":[],"contextItems":[{"attribute":"screening concentration (molar)","attributeId":971,"qualifier":"=","valueNum":13.333,"valueDisplay":"13.333 uM"}]},{"resultTypeId":962,"resultType":"log EC50","valueNum":-4.98,"replicateNumber":1,"valueDisplay":"-4.98 uM","qualifier":"=","relationship":"supported by","related":[{"resultTypeId":1335,"resultType":"standard error","valueNum":0.04596,"replicateNumber":1,"valueDisplay":"0.04596","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[]},{"resultTypeId":986,"statsModifierId":1572,"resultType":"percent activity (maximum)","valueNum":-50.76,"replicateNumber":1,"valueDisplay":"-50.76 %","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":920,"resultType":"Hill s0","valueNum":0.0,"replicateNumber":1,"valueDisplay":"0.0","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]},{"resultTypeId":919,"resultType":"Hill coefficient","valueNum":1.597,"replicateNumber":1,"valueDisplay":"1.597","qualifier":"=","relationship":"supported by","related":[],"contextItems":[]}],"contextItems":[{"attribute":"number of points","attributeId":1397,"qualifier":"=","valueNum":24.0,"valueDisplay":"24.0"}]}]}
'''
   String originalPath
    @Before
    void setup() {
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'test')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        originalPath = this.experimentService.resultsExportService.archivePathService.grailsApplication.config.bard.services.resultService.archivePath

    }
    def cleanup() {
        this.experimentService.resultsExportService.archivePathService.grailsApplication.config.bard.services.resultService.archivePath=originalPath
    }

    void "test preview Experiments"() {
        given:
        this.experimentService.resultsExportService.archivePathService.grailsApplication.config.bard.services.resultService.archivePath = "out/archivePathServiceIntegrationTest"
        Experiment experiment = Experiment.build()

        // create file
        String filename = experimentService.resultsExportService.archivePathService.constructExportResultPath(experiment)
        writeFile(filename)
        ExperimentFile experimentFile = ExperimentFile.build(experiment: experiment, exportFile: filename, submissionVersion: 1)
        experiment.experimentFiles.add(experimentFile)

        when:
        final TableModel tableModel = experimentService.previewResults(experiment.id)
        then:
        assert tableModel
        assert tableModel.additionalProperties.size() == 8
        assert tableModel.columnHeaders.size() == 8
        assert tableModel.data.size() == 3
    }

    void writeFile(String filename) {
        File exportFile = experimentService.resultsExportService.archivePathService.prepareForWriting(filename)
        Writer writer = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(exportFile)));

        JsonSubstanceResults substanceResults = mapper.readValue(RESULTS_JSON_1, JsonSubstanceResults)
        writer.write(mapper.writeValueAsString(substanceResults));
        writer.write("\n\n");

        substanceResults = mapper.readValue(RESULTS_JSON_2, JsonSubstanceResults)
        writer.write(mapper.writeValueAsString(substanceResults));
        writer.write("\n\n");


        substanceResults = mapper.readValue(RESULTS_JSON_3, JsonSubstanceResults)
        writer.write(mapper.writeValueAsString(substanceResults));
        writer.write("\n\n");

        writer.flush()
        writer.close()

    }

    void "test splitExperimentsFromAssay"() {
        given:
        Role role = Role.build()
        final Assay assay = Assay.build(assayName: 'assayName3', ownerRole:role)
        final Experiment experiment = Experiment.build(experimentName: "experimentsAlias", assay: assay, ownerRole: role).save(flush: true)

        when:
        final Assay newAssay = experimentService.splitExperimentsFromAssay(assay.id, [experiment])
        then:
        assert !assay.experiments
        assert newAssay.experiments
        assert newAssay.experiments.first().id == experiment.id
    }


}