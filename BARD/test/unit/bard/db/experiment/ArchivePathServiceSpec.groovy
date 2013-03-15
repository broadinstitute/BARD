package bard.db.experiment

import org.codehaus.groovy.grails.commons.GrailsApplication
import spock.lang.Specification

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class ArchivePathServiceSpec extends Specification {

    File expectedFinalPath = new File("out/testArchivePathService/testArchivePathServiceSpec.txt")

    void testPathResolution() {
        setup:
        GrailsApplication grailsApplication = Mock(GrailsApplication)
        grailsApplication.config >> [bard: [services: [resultService: [archivePath: "out/testArchivePathService"]]]]

        if (expectedFinalPath.exists())
            expectedFinalPath.delete()

        ArchivePathService service = new ArchivePathService()
        service.grailsApplication = grailsApplication

        Experiment experiment = new Experiment()
        experiment.id = 2

        when:
        String exportPath = service.constructExportResultPath(experiment)

        then:
        exportPath ==~ /exported-results\/2\/2\/exp-2-\d+-\d+.json.gz/

        when:
        String uploadPath = service.constructUploadResultPath(experiment)

        then:
        uploadPath ==~ /uploaded-results\/2\/2\/exp-2-\d+-\d+.txt.gz/

        when:
        File fullPath = service.prepareForWriting("testArchivePathServiceSpec.txt")

        then:
        fullPath.parentFile.exists()
        fullPath.equals(expectedFinalPath)
    }
}
