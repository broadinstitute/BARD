package molspreadsheet

import bard.core.interfaces.ExperimentRole
import bard.core.rest.spring.ExperimentRestService
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.ExperimentSearch

class NewMolecularSpreadSheetService {
    ExperimentRestService experimentRestService

    Map findExperimentDataById(final Long experimentId, final Integer top = 10, final Integer skip = 0) {
        List<SpreadSheetActivity> spreadSheetActivities = []
        long totalNumberOfRecords = 0
        ExperimentRole role = null
        ExperimentSearch experiment = experimentRestService.getExperimentById(experimentId)
        if (experiment) {
            role = experiment.role
            final Map activityValuesMap = extractActivityValuesWithExperiment(experimentId, top, skip)
            final List<Activity> activityValues = activityValuesMap.activityValues
            totalNumberOfRecords = activityValuesMap.totalNumberOfRecords
            spreadSheetActivities = createSpreadSheetActivitiesFromActivityValues(activityValues)

        }
        return [total: totalNumberOfRecords, spreadSheetActivities: spreadSheetActivities, role: role, experiment: experiment]
    }

    protected Map extractActivityValuesWithExperiment(final Long experimentId, final Integer top = 10, final Integer skip = 0) {
        ExperimentData experimentData = experimentRestService.activities(experimentId);
        return extractActivityValuesFromExperimentValueIterator(experimentData, top, skip)
    }

    protected Map extractActivityValuesFromExperimentValueIterator(final ExperimentData experimentData, final Integer top = 10, final Integer skip = 0) {
        List<Activity> activityValues = []
        long totalNumberOfRecords = 0
        if (experimentData) {
            final List<Activity> activities = experimentData.activities
            totalNumberOfRecords = activities.size()

            if (skip >= totalNumberOfRecords) {
                activityValues = []
            }
            else if (top >= totalNumberOfRecords) {
                activityValues = activities
            }
            else if (skip + top > totalNumberOfRecords) {
                activityValues = activities.subList(skip, totalNumberOfRecords.intValue())
            }
            else {
                activityValues = activities.subList(skip, skip + top)
            }
        }
        return [totalNumberOfRecords: totalNumberOfRecords, activityValues: activityValues]
    }

    protected List<SpreadSheetActivity> createSpreadSheetActivitiesFromActivityValues(final List<Activity> activityValues) {
        List<SpreadSheetActivity> spreadSheetActivities = []
        for (Activity experimentValue : activityValues) {
            SpreadSheetActivity spreadSheetActivity = extractActivitiesFromExperiment(experimentValue)
            spreadSheetActivities.add(spreadSheetActivity)
        }
        return spreadSheetActivities
    }

    SpreadSheetActivity extractActivitiesFromExperiment(final Activity experimentValue) {
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.activityToSpreadSheetActivity(experimentValue)
        return spreadSheetActivity
    }

}