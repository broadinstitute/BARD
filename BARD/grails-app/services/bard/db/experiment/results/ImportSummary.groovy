package bard.db.experiment.results

import bard.db.experiment.ResultsService

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 9/30/13
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
class ImportSummary implements Serializable {
        def errors = []

        // these are just collected for purposes of reporting the import summary at the end
        int linesParsed = 0;
        int resultsCreated = 0;
        int experimentAnnotationsCreated = 0;
        Map<String, Integer> resultsPerLabel = [:]
        transient Set<Long> substanceIds = [] as Set
        int substanceCount;

        int resultsWithRelationships = 0;
        int resultAnnotations = 0;

        List<List> topLines = []

        void addError(int line, int column, String message) {
            if (!tooMany()) {
                if (line != 0) {
                    errors << "On line ${line}, column ${column + 1}: ${message}"
                } else {
                    errors << message
                }
            }
        }

        boolean hasErrors() {
            return errors.size() > 0
        }

        boolean tooMany() {
            return errors.size() > ResultsService.MAX_ERROR_COUNT;
        }
}
