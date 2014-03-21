import bard.db.experiment.ResultsService
import bard.db.experiment.results.ImportSummary
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Created by dlahr on 3/21/14.
 */




final String firstLinePrefix = ",Experiment ID,"
final String firstLineSuffix = ",,,"
final List<String> headerLines = [",,,,,",
        "Row #,Substance,Replicate #,Parent Row #,percent viability,screening concentration (molar)"] as List<String>

final HeaderWriter headerWriter = new HeaderWriter(firstLinePrefix, firstLineSuffix, headerLines)

final File outputDir = new File("c:/Local/i_drive/projects/bard/dataMigration/NCI60/data/output")
final File sidMapFile = new File("c:/Local/i_drive/projects/bard/dataMigration/NCI60/NSC_SID_Jan14_2013.txt")
final File cellIdExperimentIdMapFile = new File("c:/Local/i_drive/projects/bard/dataMigration/NCI60/cell-serial_experiment_id_mapping.csv")

final String inputFileHeader = "NSC\tCELL\tptConc\tptResp\tptPred\tptErr"
final File inputDir = new File("c:/Local/i_drive/projects/bard/dataMigration/NCI60/data/input")
List<File> dataFileList =
        inputDir.listFiles().collect({return it}) as List<File>
//        [new File("userScripts/loadNci60/testData/head_002.txt")] as List<File>
//File dataFile = dataFileList.get(0)

//testDataReader()
DataReader dataReader = new DataReader(dataFileList)

SpringSecurityUtils.reauthenticate("dlahr", null)

Map<Integer, Integer> nscSidMap = createNscSidMap(sidMapFile)

Map<Integer, Integer> cellIdExperimentIdMap = createCellIdExperimentIdMap(cellIdExperimentIdMapFile)

Map<Integer, Integer> cellIdCountMap = new HashMap<>()

int rowNumber = 1
BufferedWriter writer = null
Integer previousCellId = null
String line
while ((line = dataReader.read()) != null) {
    if (! line.equalsIgnoreCase(inputFileHeader)) {
        String[] split = line.split("\t")

        Integer cellId = Integer.valueOf(split[1])
        Integer nscId = Integer.valueOf(split[0])  //their substance ID
        Double concMillimolar = Double.valueOf(split[2])
        Double concMicromolar = concMillimolar * 1000
        String resp = split[3] //response

        Integer sid = nscSidMap.get(nscId)

        if (cellId != previousCellId) {
            if (previousCellId) {
                cellIdCountMap.put(previousCellId, rowNumber)
                println("finished cellId:$cellId count:$rowNumber")
            }

            println("processing cellId:  $cellId")

            //if we have already encountered this cellId, append data to the file
            boolean append = cellIdCountMap.containsKey(cellId)
            if (append) {
                System.err.println("WARNING:  found a non-contiguous cellId: ${cellId} ${dataReader.currentFile}")
            }

            Integer eid = cellIdExperimentIdMap.get(cellId)

            if (writer) {
                writer.close()
            }


            File outputFile = new File(outputDir, "eid-${eid}.csv".toString())
            writer = new BufferedWriter(new FileWriter(outputFile))

            headerWriter.write(writer, eid)

            rowNumber = 1
            previousCellId = cellId
        }

        //1 is the replicate number
        String outputLine = "$rowNumber,$sid,1,,$resp,$concMicromolar".toString()
        writer.write(outputLine)
        writer.newLine()

        rowNumber++

    }
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

Map<Integer, Integer> createNscSidMap(File sidMapFile) {
    Map<Integer, Integer> result = new HashMap<>()

    BufferedReader reader = new BufferedReader(new FileReader(sidMapFile))
    reader.readLine()

    String line
    while ((line = reader.readLine()) != null) {
        String[] split = line.split("\t")

        result.put(Integer.valueOf(split[0]), Integer.valueOf(split[1]))
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


Map <Integer, Integer> createCellIdExperimentIdMap(File cellIdExperimentIdMapFile) {
    Map<Integer, Integer> result = new HashMap<>()

    BufferedReader reader = new BufferedReader(new FileReader(cellIdExperimentIdMapFile))
    reader.readLine()
    reader.readLine()
    reader.readLine()

    String line
    while ((line = reader.readLine()) != null) {
        String[] split = line.split(",")

        Integer cellId = Integer.valueOf(split[0])
        Integer eid = Integer.valueOf(split[4])

        result.put(cellId, eid)
    }

    reader.close()

    return result
}
