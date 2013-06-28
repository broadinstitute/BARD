package bard.db.experiment

class ExperimentFile {
    private static final int FILENAME_MAX_SIZE = 1000

    Experiment experiment
    Long submissionVersion
    String originalFile;
    String exportFile;
    Long substanceCount;

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static constraints = {
        submissionVersion(nullable: false)
        originalFile(blank: false, maxSize:FILENAME_MAX_SIZE)
        exportFile(maxSize: FILENAME_MAX_SIZE)
        modifiedBy nullable: true, maxSize: 40
    }

    static mapping = {
        id(column: "EXPERIMENT_FILE_ID", generator: "sequence", params: [sequence: 'EXPERIMENT_FILE_ID_SEQ'])
    }

    static belongsTo = [experiment: Experiment]
}
