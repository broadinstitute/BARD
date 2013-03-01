package bard.db.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import spock.lang.Specification

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class ResultsExportServiceSpec extends Specification {

    static File destination = new File("out/resultExportServiceTest.txt.gz")

    void 'test dumpFromList'() {
        setup:
        ArchivePathService archivePathService = Mock(ArchivePathService)
        archivePathService.prepareForWriting("path") >> destination

        Substance substance1 = new Substance()
        substance1.id = 1

        Substance substance2 = new Substance()
        substance2.id = 2

        ResultsExportService service = new ResultsExportService()
        service.archivePathService = archivePathService
        List results = [new Result(substance: substance1)]

        when:
        service.dumpFromList("path", results)

        then:
        destination.exists()

        when:
        // parse into json objects
        ObjectMapper mapper = new ObjectMapper()
        ObjectReader reader = mapper.reader(JsonSubstanceResults)

        BufferedReader lineReader = new BufferedReader(new FileReader(destination));

        JsonSubstanceResults substanceResults1 = reader.readValue(lineReader.readLine())

        then:
        lineReader.readLine() == ""

        when:
        JsonSubstanceResults substanceResults2 = reader.readValue(lineReader.readLine())

        then:
        lineReader.readLine() == ""
        lineReader.readLine() == null
    }
}
