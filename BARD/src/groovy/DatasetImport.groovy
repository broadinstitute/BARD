import au.com.bytecode.opencsv.CSVReader

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/7/14
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatasetImport {
    List<FileState> openFiles;

    public DatasetImport(List<String> filenames) {
        openFiles = filenames.collect { new FileState(it) }
    }

    Batch readNext() {
        Long nextSid = openFiles.collect { it.nextSid }.min()

        Batch batch = new Batch(sid: nextSid,
                datasets: openFiles.findAll { it.nextSid == nextSid }.collect {
                    it.readNextDataset()
                })

        return batch
    }

    boolean hasNext() {
        boolean reachedEnd = true;
        openFiles.each {
            if(!it.reachedEnd) {
                reachedEnd = false;
            }
        }
        return !reachedEnd
    }
}

class FileState {
    List<String> columns;
    CSVReader reader;
    List<String> nextRow = null;
    Long nextSid;
    boolean reachedEnd = false;

    public FileState(String filename) {
        reader = new CSVReader(new FileReader(filename))
        columns = reader.readNext() as List
        populateNext();
    }

    void populateNext() {
        nextRow = reader.readNext() as List
        if (nextRow == null) {
            reachedEnd = true;
            nextRow = null;
            nextSid = null;
            reader.close();
        } else {
            nextSid = Long.parseLong(nextRow[0])
        }
    }

    Dataset readNextDataset() {
        List<List<String>> rows = [];
        Long sid = nextSid;
        while (true) {
            if (nextSid != sid) {
                if(nextSid < sid) {
                    throw new RuntimeException("Samples are out of order.");
                }
                break
            }
            rows.add(nextRow)

            populateNext();
            if (reachedEnd)
                break;
        }

        Dataset dataset = new Dataset(columns: columns, rows: rows);
        return dataset;
    }
}

class Dataset {
    List<String> columns;
    List<List<String>> rows;
}

class Batch {
    Long sid;
    List<Dataset> datasets;
}
