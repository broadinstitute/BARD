package bard.dm.cars.spreadsheet

import bard.dm.Log

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 7:49 AM
 * To change this template use File | Settings | File Templates.
 */
class CarsSpreadsheetValidator {
    void validateProjects(List<CarsProject> projectList) {
        Log.logger.info("validate data loaded from file")

        Iterator<CarsProject> projectIter = projectList.iterator()
        while (projectIter.hasNext()) {
            CarsProject project = projectIter.next()

            Iterator<String> projectFieldIter = ["grantNumber", "assayCenter", "grantTitle"].iterator()

            boolean foundInconsistentField = false
            while ((!foundInconsistentField) && projectFieldIter.hasNext()) {
                String projectField = projectFieldIter.next()

                def first = project.carsExperimentList.get(0).getProperty(projectField)

                Iterator<CarsExperiment> experimentIter = project.carsExperimentList.iterator()
                boolean isSame = true
                while (isSame && experimentIter.hasNext()) {
                    def current = experimentIter.next().getProperty(projectField)

                    isSame = current.equals(first)
                }

                if (! isSame) {
                    println("project has inconsistent values:  " + project.projectUid + " field: " + projectField)
                    projectIter.remove()
                    foundInconsistentField = true
                } else {
                    project.putAt(projectField, first)
                }
            }
        }
    }
}
