package bard.dm.cars.spreadsheet

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 8/31/12
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
class CarsProject {
    List<CarsExperiment> carsExperimentList = new LinkedList<CarsExperiment>()

    Integer projectUid
    String grantNumber
    String grantTitle
    String assayCenter
    Integer summaryAid

    @Override
    String toString() {
        StringBuilder builder = new StringBuilder()
        builder.append("project UID:").append(projectUid).append(" ")

        for (CarsExperiment experiment : carsExperimentList) {
            builder.append(experiment.toString()).append(" ")
        }

        return builder.toString()
    }
}
