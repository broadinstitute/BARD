import au.com.bytecode.opencsv.CSVWriter
import spock.lang.Specification;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/7/14
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
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
        Batch batch

        when:
        DatasetImport input = new DatasetImport([file1, file2])

        then:
        input.hasNext();

        when:
        batch = input.readNext() ;

        then:
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
        Batch batch

        when:
        DatasetImport input = new DatasetImport([file1, file2])

        then:
        input.hasNext() ;

        when:
        batch = input.readNext() ;

        then:
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
