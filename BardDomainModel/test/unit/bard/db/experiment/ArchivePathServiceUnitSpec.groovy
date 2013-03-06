package bard.db.experiment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.commons.GrailsApplication
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/4/13
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([Experiment, ExperimentFile])
@Mock([Experiment, ExperimentFile])
class ArchivePathServiceUnitSpec extends Specification {
    def 'test getEtlExport'() {
        setup:
        GrailsApplication grailsApplication = Mock(GrailsApplication)
        grailsApplication.config >> [bard: [services: [resultService: [archivePath: "out/archivePathServiceTest"]]]]

        ArchivePathService service = new ArchivePathService()
        service.grailsApplication = grailsApplication

        String expectedString = new Date().toString()

        when:
        Experiment experiment = Experiment.build()

        // create file
        String filename = service.constructExportResultPath(experiment)
        File exportFile = service.prepareForWriting(filename)
        exportFile.write(expectedString)
        ExperimentFile experimentFile = ExperimentFile.build(experiment: experiment, exportFile: filename, submissionVersion: 1)
        experiment.experimentFiles.add(experimentFile)

        // now, try to read it via getEtlExport
        InputStream input = service.getEtlExport(experiment)
        then:
        input != null

        when:
        String valueRead = IOUtils.toString(input)

        then:
        valueRead == expectedString
    }
}
