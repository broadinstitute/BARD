package adf.imp

import au.com.bytecode.opencsv.CSVReader
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import org.apache.commons.collections.iterators.IteratorChain
import org.apache.commons.io.LineIterator
import org.mapdb.Pump
import org.mapdb.Serializer

/**
 * Used to sort data files
 *
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/10/14
 * Time: 12:07 PM
 */
class DatasetImportTransform {
    static final int SORT_BATCH_SIZE = 100000;

    static final Comparator<Comparable> comparableComparator = new Comparator<Comparable>() {
        int compare(Comparable t, Comparable t1) {
            return t.compareTo(t1)
        }
    }

    static class DerivedCSVReader extends CSVReader {
        Reader originalReader;

        DerivedCSVReader(Reader reader, Reader originalReader) {
            super(reader)
            this.originalReader = originalReader;
        }

        @Override
        void close() throws IOException {
            super.close()
            this.originalReader.close();
        }
    }

    public static CSVReader sortCsvFile(String filename) {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String header = bufferedReader.readLine();
        Iterator<String> lineIterator = new LineIterator(bufferedReader);
        Iterator<String> sortedLineIterator = Pump.sort(lineIterator, false, SORT_BATCH_SIZE, comparableComparator, Serializer.STRING);
        Reader vReader = new ReaderFromLineIterator(new IteratorChain([header].iterator(), sortedLineIterator), "\n");
        return new DerivedCSVReader(vReader, bufferedReader);
    }
}
