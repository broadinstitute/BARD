import bard.db.experiment.ResultsService
import bard.db.experiment.results.ImportSummary
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Created by dlahr on 3/21/14.
 */


final boolean doWrite = true

final File outputDir =
//        new File("c:/Local/i_drive/projects/bard/dataMigration/NCI60/data/output") //for development environment
        new File("/home/unix/dlahr/projects/bard_NCI60/prodLoad/output") //for running on BigBard for loading to prod

final File unmappedNscOutputFile = new File(outputDir, "unmapped_nsc.csv")

final File sidMapFile =
//        new File("c:/Local/i_drive/projects/bard/dataMigration/NCI60/NSC_SID_Jan14_2013.txt") //for development environment
          new File("/home/unix/dlahr/projects/bard_NCI60/prodLoad/NSC_SID_Jan14_2013.txt") //for running on BigBard for loading to prod

final File cellIdExperimentIdMapFile =
//        new File("c:/Local/i_drive/projects/bard/dataMigration/NCI60/cell-serial_experiment_id_mapping.csv") //for development environment
        new File("/home/unix/dlahr/projects/bard_NCI60/prodLoad/cell-serial_experiment_id_mapping.csv") //for running on BigBard for loading to prod

final String inputFileHeader = "NSC\tCELL\tptConc\tptResp\tptPred\tptErr"

final File inputDir =
//        new File("c:/Local/i_drive/projects/bard/dataMigration/NCI60/data/input") //for development environment
        new File("/chembio/datasets/csdev/PAC/temp/NCI/pts/") //for running on BigBard for loading to prod

final String firstLinePrefix = ",Experiment ID,"
final String firstLineSuffix = ",,,"
final List<String> headerLines = [",,,,,",
        "Row #,Substance,Replicate #,Parent Row #,percent viability,screening concentration (molar)"] as List<String>

final HeaderWriter headerWriter = new HeaderWriter(firstLinePrefix, firstLineSuffix, headerLines)

final DoNothingWriter doNothingWriter = new DoNothingWriter()

List<File> dataFileList =
        inputDir.listFiles().collect({return it}) as List<File>
//        [new File("userScripts/loadNci60/testData/head_002.txt")] as List<File>
//File dataFile = dataFileList.get(0)
Collections.sort(dataFileList, new Comparator<File>() {
    @Override
    int compare(File o1, File o2) {
        return o1.name.compareTo(o2.name)
    }
})
println("dataFileList:  $dataFileList")

//testDataReader()
DataReader dataReader = new DataReader(dataFileList)

SpringSecurityUtils.reauthenticate("dlahr", null)

Map<Integer, NscInfo> nscMap = createNscSidMap(sidMapFile)

Map<Integer, CellInfo> cellMap = createCellIdExperimentIdMap(cellIdExperimentIdMapFile)


BufferedWriter writer = new BufferedWriter(doNothingWriter)
CellInfo prevCellInfo = new CellInfo(null, null)
String line
while ((line = dataReader.read()) != null) {
    if (! line.equalsIgnoreCase(inputFileHeader)) {
        String[] split = line.split("\t")

        CellInfo cellInfo = cellMap.get(Integer.valueOf(split[1]))
        //if a new cellId is encountered, switch to the file for the experiment corresponding to that new cellId
        if (cellInfo.cellId != prevCellInfo.cellId) {
            if (prevCellInfo.cellId) {
                println("finished cellId:${prevCellInfo.cellId} count:${prevCellInfo.count}")
            }

            println("processing cellId:  ${cellInfo.cellId}")

            //if we have already encountered this cellId, append data to the file
            boolean append = cellInfo.count > 0
            if (append) {
                System.err.println("WARNING:  found a non-contiguous cellId: ${cellInfo.cellId} ${dataReader.currentFile}")
            }

            writer.close()

            File outputFile = new File(outputDir, "eid-${cellInfo.experimentId}.csv".toString())

            Writer rawWriter = doWrite ? new FileWriter(outputFile, append) : doNothingWriter
            writer = new BufferedWriter(rawWriter)

            if (! append) {
                headerWriter.write(writer, cellInfo.experimentId)
            }

            prevCellInfo = cellInfo
        }
        cellInfo.count++


        Integer nscId = Integer.valueOf(split[0])  //their substance ID
        NscInfo nscInfo = nscMap.get(nscId)
        if (! nscInfo) {
            nscInfo = new NscInfo(nscId, null)
            nscMap.put(nscId, nscInfo)
        }
        nscInfo.count++
        nscInfo.cellIds.add(cellInfo.cellId)

        if (nscInfo.sid) {
            //file contains concentration in millimolar, convert to BARD screening concentration default unit of
            //micromolar
            Double concentration = Double.valueOf(split[2]) * 1000

            int replicateCount = nscInfo.incrementReplicateCount(cellInfo.cellId, concentration)

            String resp = split[3] //response

            //row number, sid, replicate number, result, concentration
            String outputLine = "${cellInfo.count},${nscInfo.sid},$replicateCount,,$resp,$concentration".toString()
            writer.write(outputLine)
            writer.newLine()
        }

    }
}

writer.close()

writer = new BufferedWriter(new FileWriter(unmappedNscOutputFile))
List<NscInfo> nscWithoutSid = nscMap.values().findAll({NscInfo it -> (null == it.sid)})
Collections.sort(nscWithoutSid, new Comparator<NscInfo>() {
    @Override
    int compare(NscInfo o1, NscInfo o2) {
        return o1.nscId - o2.nscId
    }
})
writer.write("nsc,count,cellIds")
writer.newLine()
for (NscInfo nscInfo : nscWithoutSid) {
    List<Integer> cellIds = new ArrayList<>(nscInfo.cellIds)
    Collections.sort(cellIds)
    String cellIdsString = cellIds.join(",")

    writer.write("${nscInfo.nscId},${nscInfo.count},$cellIdsString".toString())
    writer.newLine()
}

writer.close()

return


void investigateNscNumbers(File sidMapFile) {
    Set<Integer> nscSet = new HashSet<>()
    BufferedReader reader = new BufferedReader(new FileReader(sidMapFile))
    reader.readLine()
    int lineNumber = 1
    String line
    int duplicateCount = 0
    while ((line = reader.readLine()) != null) {
        lineNumber++

        String[] split = line.split("\t")

        if (! nscSet.add(Integer.valueOf(split[0]))) {
            println("found duplicate nsc number:  ${split[0]}  lineNumber:$lineNumber")
            duplicateCount++
        }

    }
    reader.close()

    Integer maxNsc = Collections.max(nscSet)

    println("size:${nscSet.size()} maxNsc:$maxNsc duplicateCount:$duplicateCount")
}

class Nci60ResultContext {
    final int cellId
    final double concentration

    Nci60ResultContext(int cellId, double concentration) {
        this.cellId = cellId
        this.concentration = concentration
    }

    @Override
    int hashCode() {
        Double
        return (37*cellId) + Double.valueOf(concentration).hashCode()
    }

    @Override
    boolean equals(Object obj) {
        if (obj instanceof Nci60ResultContext) {
            Nci60ResultContext other = (Nci60ResultContext)obj
            return (other.cellId == cellId) && (other.concentration == concentration)
        } else {
            return false
        }
    }
}

class NscInfo {
    final Integer nscId
    final Integer sid

    Map<Nci60ResultContext, Integer> replicateCountMap

    int count
    Set<Integer> cellIds

    NscInfo(Integer nscId, Integer sid) {
        this.nscId = nscId
        this.sid = sid

        replicateCountMap = new HashMap<>()

        count = 0

        cellIds = new HashSet<>()
    }

    int incrementReplicateCount(int cellId, double concentration) {
        Nci60ResultContext rc = new Nci60ResultContext(cellId, concentration)

        Integer replicateCount = replicateCountMap.get(rc)
        if (! replicateCount) {
            replicateCount = 0
        }

        replicateCount++
        replicateCountMap.put(rc, replicateCount)

        return replicateCount
    }
}

class CellInfo {
    final Integer cellId
    final Integer experimentId
    int count

    CellInfo(Integer cellId, Integer experimentId) {
        this.cellId = cellId
        this.experimentId = experimentId

        count = 0
    }
}


Map<Integer, NscInfo> createNscSidMap(File sidMapFile) {
    Map<Integer, NscInfo> result = new HashMap<>()

    BufferedReader reader = new BufferedReader(new FileReader(sidMapFile))
    reader.readLine()

    String line
    while ((line = reader.readLine()) != null) {
        String[] split = line.split("\t")

        NscInfo nscInfo = new NscInfo(Integer.valueOf(split[0]), Integer.valueOf(split[1]))

        result.put(nscInfo.nscId, nscInfo)
    }
    reader.close()

    return result
}


class HeaderWriter {
    final String firstLinePrefix
    final String firstLineSuffix
    final List<String> headerLines

    HeaderWriter(String firstLinePrefix, String firstLineSuffix, List<String> headerLines) {
        this.firstLinePrefix = firstLinePrefix
        this.firstLineSuffix = firstLineSuffix
        this.headerLines = headerLines
    }

    void write(BufferedWriter writer, Integer experimentId) {
        StringBuilder firstLine = new StringBuilder()
        firstLine.append(firstLinePrefix).append(experimentId).append(firstLineSuffix)
        writer.write(firstLine.toString())
        writer.newLine()
        for (String headerLine : headerLines) {
            writer.write(headerLine)
            writer.newLine()
        }
    }
}


class DataReader {
    List<File> inputFileList

    Iterator<File> inputFileIter

    File currentFile

    BufferedReader reader

    DataReader(List<File> inputFileList) {
        this.inputFileList = inputFileList

        inputFileIter = inputFileList.iterator()

        currentFile = inputFileIter.next()

        reader = new BufferedReader(new FileReader(currentFile))
    }

    String read() {
        String result

        while (null == (result = reader.readLine())) {
            reader.close()

            if (inputFileIter.hasNext()) {
                currentFile = inputFileIter.next()

                reader = new BufferedReader(new FileReader(currentFile))
            } else {
                return null
            }
        }

        return result
    }
}


void testDataReader() {
    File dir = new File("userScripts/loadNci60/testData/")
    List<File> fileList = [new File(dir, "1.txt"), new File(dir, "2.txt"), new File(dir, "3.txt")] as List<File>

    DataReader dataReader = new DataReader(fileList)

    String line
    while ((line = dataReader.read()) != null) {
        println(line)
    }
}


Map <Integer, CellInfo> createCellIdExperimentIdMap(File cellIdExperimentIdMapFile) {
    Map<Integer, CellInfo> result = new HashMap<>()

    BufferedReader reader = new BufferedReader(new FileReader(cellIdExperimentIdMapFile))
    reader.readLine()
    reader.readLine()
    reader.readLine()

    String line
    while ((line = reader.readLine()) != null) {
        String[] split = line.split(",")

        CellInfo cellInfo = new CellInfo(Integer.valueOf(split[0]), Integer.valueOf(split[4]))

        result.put(cellInfo.cellId, cellInfo)
    }

    reader.close()

    return result
}


class DoNothingWriter extends Writer {
    @Override
    void write(char[] cbuf, int off, int len) throws IOException {

    }

    @Override
    void flush() throws IOException {

    }

    @Override
    void close() throws IOException {

    }
}

