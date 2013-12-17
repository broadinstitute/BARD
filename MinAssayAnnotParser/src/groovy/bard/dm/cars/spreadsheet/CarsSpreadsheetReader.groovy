package bard.dm.cars.spreadsheet

import com.csvreader.CsvReader
import bard.dm.Log

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 7:45 AM
 * To change this template use File | Settings | File Templates.
 */
class CarsSpreadsheetReader {

    List<CarsProject>  readProjectsFromFile(String inputFilePath, String headerMappingsConfigPath) {
        Log.logger.info("read data from file " + inputFilePath)

        HeaderNamesReader headerNamesReader = new HeaderNamesReader()
        HeaderNames headerNames = headerNamesReader.readHeaderNames(headerMappingsConfigPath)

        File inputFile = new File(inputFilePath)
        if ((!inputFile.exists()) || (!inputFile.canRead())) {
            println("File doesn't exist or cannot be read")
            return
        }

        CsvReader reader = new CsvReader(new FileReader(inputFile))
        if (!reader.readHeaders()) {
            println("unable to read 1st line of file which should contain headers:  " + inputFile.getAbsolutePath())
            return
        }


        Map<Integer, CarsProject>  projectUidProjectMap = new HashMap<Integer, CarsProject>()
        while (reader.readRecord()) {

            String projectUidString = reader.get(headerNames.projectUid).trim()
            if (projectUidString) {
                try {
                    Integer projectUid = Integer.valueOf(projectUidString)

                    CarsProject project = projectUidProjectMap.get(projectUid)
                    if (null == project) {
                        project = new CarsProject(projectUid: projectUid)
                        projectUidProjectMap.put(projectUid, project)
                    }

                    CarsExperiment experiment = new CarsExperiment()
                    experiment.spreadsheetLineNumber = reader.getCurrentRecord()
                    experiment.aid = Integer.valueOf(reader.get(headerNames.aidNumber).trim())
                    experiment.grantNumber = reader.get(headerNames.grantNumber).trim()
                    experiment.assayTarget = reader.get(headerNames.assayTarget).trim()
                    experiment.assaySubtype = reader.get(headerNames.assaySubtype).trim()
                    experiment.assayCenter = reader.get(headerNames.assayCenter).trim()
                    experiment.assayProvider = reader.get(headerNames.assayProvider).trim()
                    experiment.assayName = reader.get(headerNames.assayName).trim()
                    experiment.grantTitle = reader.get(headerNames.grantTitle).trim()

                    project.carsExperimentList.add(experiment)

                } catch (NumberFormatException e) {
                    println("unable to parse number on line " + reader.getCurrentRecord())
                }
            }
        }
        Log.logger.info("number of data lines read:  " + (reader.currentRecord - 1))
        reader.close()

        List<CarsProject> projectList = new ArrayList<CarsProject>(projectUidProjectMap.values())
        Collections.sort(projectList, new Comparator<CarsProject>() {
            int compare(CarsProject o1, CarsProject o2) {
                return o1.projectUid - o2.projectUid
            }
        })

        return projectList
    }
}
