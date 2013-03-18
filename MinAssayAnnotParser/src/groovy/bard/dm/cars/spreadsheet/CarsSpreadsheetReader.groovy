package bard.dm.cars.spreadsheet

import com.csvreader.CsvReader
import bard.dm.Log
import bard.dm.cars.spreadsheet.exceptions.CouldNotReadHeadersException

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 7:45 AM
 * To change this template use File | Settings | File Templates.
 */
class CarsSpreadsheetReader {

    ProjectIdsToLoad projectIdsToLoadSet

    public CarsSpreadsheetReader(ProjectIdsToLoad projectIdsToLoadSet) {
        this.projectIdsToLoadSet = projectIdsToLoadSet
    }

    List<CarsProject>  readProjectsFromFile(String inputFilePath, String headerMappingsConfigPath) throws FileNotFoundException, Exception {
        Log.logger.info("read data from file " + inputFilePath)

        HeaderNamesReader headerNamesReader = new HeaderNamesReader()
        HeaderNames headerNames = headerNamesReader.readHeaderNames(headerMappingsConfigPath)

        File inputFile = new File(inputFilePath)
        if ((!inputFile.exists()) || (!inputFile.canRead())) {
            throw new FileNotFoundException("CarsSpreadsheetReader readProjectsFromFile could not find file ${inputFile.absolutePath}")
        }

        CsvReader reader = new CsvReader(new FileReader(inputFile))
        if (!reader.readHeaders()) {
            throw new CouldNotReadHeadersException("CarsSpreadsheetReader readProjectsFromFile could not read headers in file ${inputFile.absolutePath}")
        }

        Set<Integer> projectAidSet = new HashSet<Integer>()
        Map<Integer, CarsProject>  projectUidProjectMap = new HashMap<Integer, CarsProject>()
        while (reader.readRecord()) {

            String projectUidString = reader.get(headerNames.projectUid).trim()
            if (projectUidString) {
                try {
                    Integer projectUid = Integer.valueOf(projectUidString)

                    if (projectIdsToLoadSet.contains(projectUid)) {
                        CarsProject project = projectUidProjectMap.get(projectUid)
                        if (null == project) {
                            project = new CarsProject(projectUid: projectUid)
                            projectUidProjectMap.put(projectUid, project)

                            projectAidSet.clear()
                        }

                        String summaryAidString = reader.get(headerNames.summaryAid)
                        if (summaryAidString) {
                            try {
                                Integer summaryAid = Integer.valueOf(summaryAidString.trim())

                                if (null == project.summaryAid) {
                                    project.summaryAid = summaryAid
                                } else if (summaryAid != project.summaryAid) {
                                    Log.logger.warn("CarsSpreadsheetReader readProjectFromFile multiple summary AID's found for project_UID ${project.projectUid}")
                                }
                            } catch (NumberFormatException e) {
                                println("unable to parse summary AID on line " + reader.getCurrentRecord())
                            }
                        }

                        Integer aid = Integer.valueOf(reader.get(headerNames.aidNumber).trim())
                        if (projectAidSet.add(aid)) {
                            CarsExperiment experiment = new CarsExperiment()
                            experiment.spreadsheetLineNumber = reader.getCurrentRecord()
                            experiment.aid = aid
                            experiment.grantNumber = reader.get(headerNames.grantNumber).trim()
                            experiment.assayTarget = reader.get(headerNames.assayTarget).trim()
                            experiment.assaySubtype = reader.get(headerNames.assaySubtype).trim()
                            experiment.assayCenter = reader.get(headerNames.assayCenter).trim()
                            experiment.assayProvider = reader.get(headerNames.assayProvider).trim()
                            experiment.assayName = reader.get(headerNames.assayName).trim()
                            experiment.grantTitle = reader.get(headerNames.grantTitle).trim()

                            project.carsExperimentList.add(experiment)
                        }
                        else {
                            Log.logger.warn("CarsSpreadsheetReader readProjectsFromFile AID $aid found more than once in project UID $projectUid")
                        }
                    }
                } catch (NumberFormatException e) {
                    println("unable to parse project UID number on line " + reader.getCurrentRecord())
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
