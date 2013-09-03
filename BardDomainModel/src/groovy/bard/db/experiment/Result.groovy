package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.enums.ReadyForExtraction

class Result {
    private static final int VALUE_DISPLAY_MAX_SIZE = 256
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int RESULT_STATUS_MAX_SIZE = 20
    private static final int READY_FOR_EXTRACTION_MAX_SIZE = 20

    String resultStatus
    ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY

    Experiment experiment
    Element resultType
    Long substanceId
    Element statsModifier
    Integer replicateNumber

    String qualifier
    Float valueNum
    Float valueMin
    Float valueMax
    String valueDisplay

    Date dateCreated
    Date lastUpdated
    String modifiedBy
    Long id;
    transient ExperimentMeasure experimentMeasure

    Set<ResultContextItem> resultContextItems = [] as Set<ResultContextItem>
    Set<ResultHierarchy> resultHierarchiesForResult = [] as Set<ResultHierarchy>
    Set<ResultHierarchy> resultHierarchiesForParentResult = [] as Set<ResultHierarchy>

    static QUALIFIER_VALUES = ['= ', '< ', '<=', '> ', '>=', '<<', '>>', '~ ']
    static hasMany = [resultHierarchiesForParentResult: ResultHierarchy,
            resultHierarchiesForResult: ResultHierarchy,
            resultContextItems: ResultContextItem]

    static belongsTo = [Experiment, ResultContextItem]

    static mappedBy = [resultHierarchiesForParentResult: "parentResult",
            resultHierarchiesForResult: "result"]

    static mapping = {
        id(column: "RESULT_ID", generator: "sequence", params: [sequence: 'RESULT_ID_SEQ'])

        resultType(column: "RESULT_TYPE_ID")
        qualifier(column: "qualifier", sqlType: "char", length: 2)
        replicateNumber(column: 'REPLICATE_NO')
    }

    static constraints = {
        resultStatus(maxSize: RESULT_STATUS_MAX_SIZE, nullable: false, inList: ["Pending", "Approved", "Rejected", "Mark for Deletion"])
        readyForExtraction(nullable: false)

        experiment()
        resultType()
        substanceId(nullable: false)
        statsModifier(nullable: true)
        replicateNumber(nullable: true)

        qualifier(nullable: true, blank: false, inList: QUALIFIER_VALUES)
        valueNum(nullable: true)
        valueMin(nullable: true)
        valueMax(nullable: true)
        valueDisplay(nullable: true, blank: false, maxSize: VALUE_DISPLAY_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true,)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    public String getDisplayLabel() {
        String label = resultType.label
        if(statsModifier != null) {
            label += " (${statsModifier.label})"
        }
        return label
    }
}
