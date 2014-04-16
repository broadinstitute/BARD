package adf.imp

import adf.exp.JsonTransform
import adf.imp.Batch
import adf.imp.DatasetParser
import au.com.bytecode.opencsv.CSVWriter
import bard.db.dictionary.Element
import bard.db.experiment.JsonSubstanceResults
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/7/14
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Element])
@Mock([Element])
@TestMixin(GrailsUnitTestMixin)
public class DatasetImportUnitSpec extends Specification {

    String createFile(List<List<String>> rows) {
        File temp = File.createTempFile("test-data-imp", ".tmp");
        FileWriter out = new FileWriter(temp)
        CSVWriter writer = new CSVWriter(out)
        writer.writeNext(["sid", "a"].toArray(new String[0]))
        rows.each {
            writer.writeNext(it.collect {it.toString()} .toArray(new String[0]))
        }
        writer.close()
        return temp.absolutePath
    }

    void testSameSamples() {
        setup:
        String file1 = createFile([[1,2],[2,3]]);
        String file2 = createFile([[1,2],[2,3]]);
        Element element = Element.build(label: "a")
        Batch batch

        when:
        DatasetParser input = new DatasetParser([file1, file2], {e -> true})

        then:
        input.hasNext();

        when:
        batch = input.readNext() ;

        then:
        batch.datasets.size() == 2
        batch.sid == 1
        input.hasNext() ;

        when:
        Element.findByLabel("a") >> element
        batch = input.readNext() ;

        then:
        batch.datasets.size() == 2
        batch.sid == 2
        !input.hasNext()
    }

    void testOutOfOrderSids() {
        setup:
        String file1 = createFile([[2,3],[1,2]]);
        String file2 = createFile([[1,2],[2,3]]);
        Element element = Element.build(label: "a")
        Batch batch

        when:
        DatasetParser input = new DatasetParser([file1, file2], {e -> true})

        then:
        input.hasNext();

        when:
        batch = input.readNext() ;

        then:
        Element.findByLabel("a") >> element
        batch.datasets.size() == 2
        batch.sid == 1
        input.hasNext() ;

        when:
        batch = input.readNext() ;

        then:
        batch.datasets.size() == 2
        batch.sid == 2
        !input.hasNext()
    }

    void testDifferentSample() {
        String file1 = createFile([[2,3]])
        String file2 = createFile([[1,2]])
        Element element = Element.build(label: "a")
        Batch batch

        when:
        DatasetParser input = new DatasetParser([file1, file2], {e -> true})

        then:
        input.hasNext() ;

        when:
        batch = input.readNext() ;

        then:
        Element.findByLabel("a") >> element
        batch.datasets.size() == 1
        batch.sid == 1
        input.hasNext() ;

        when:
        batch = input.readNext() ;

        then:
        batch.datasets.size() == 1
        batch.sid == 2
        !input.hasNext()
    }
}
