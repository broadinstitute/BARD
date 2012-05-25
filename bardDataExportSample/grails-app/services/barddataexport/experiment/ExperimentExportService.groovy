package barddataexport.experiment

import groovy.sql.Sql
import groovy.xml.MarkupBuilder

import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar
import javax.sql.DataSource

class ExperimentExportService {
    DataSource dataSource

    //TODO: Read from Properties file

    final static int MAX_EXPERIMENTS_RECORDS_PER_PAGE = 1000000
    final static String EXPERIMENT_URL_PREFIX = "http://bard/experiment/"
    final static String EXPERIMENTS_URL_PREFIX = "http://bard/experiments"
    final static String PROJECT_URL_PREFIX = "http://bard/project/"
    final static String ASSAY_URL_PREFIX = "http://bard/assay/"

    final static String CAP_MEDIA_TYPE = "application/vnd.bard.cap+xml;type=cap"
    final static String RESULTS_MEDIA_TYPE = "application/vnd.bard.cap+xml;type=results"
    final static String EXPERIMENT_MEDIA_TYPE = "application/vnd.bard.cap+xml;type=experiment"
    final static String EXPERIMENTS_MEDIA_TYPE = "application/vnd.bard.cap+xml;type=experiments"
    final static String ELEMENT_BASE_URL = "http://bard/dictionary/element/"
    final static String ELEMENT_MEDIA_TYPE = "application/vnd.bard.cap+xml;type=element"


    protected void generateExperimentStatus(
            final Sql sql,
            final MarkupBuilder xml,
            final BigDecimal statusId) {

        sql.eachRow('select STATUS from EXPERIMENT_STATUS where EXPERIMENT_STATUS_ID=' + statusId) { statusRow ->
            xml.experimentStatus() {
                status(statusRow.STATUS)
            }
        }

    }
    /**
     *
     * Generate the result context element
     *
     * @param sql
     * @param xml
     * @param resultId
     */
    protected void generateResultContextItems(final Sql sql, final MarkupBuilder xml, final BigDecimal experimentId) {
        xml.resultContextItems() {
            sql.eachRow("SELECT * FROM RESULT_CONTEXT_ITEM WHERE EXPERIMENT_ID=" + experimentId + " and RESULT_ID IS NULL" ) { resultContextItemRow ->
                def attributes = [:]
                attributes.put('resultContextItemId', resultContextItemRow.RESULT_CONTEXT_ITEM_ID)
                if (resultContextItemRow.GROUP_RESULT_CONTEXT_ID || resultContextItemRow.GROUP_RESULT_CONTEXT_ID.toString().isInteger()) {
                    attributes.put('groupResultContextItemId', resultContextItemRow.GROUP_RESULT_CONTEXT_ID)
                }

                if (resultContextItemRow.QUALIFIER) {
                    attributes.put('qualifier', resultContextItemRow.QUALIFIER)
                }


                if (resultContextItemRow.VALUE_DISPLAY) {
                    attributes.put('valueDisplay', resultContextItemRow.VALUE_DISPLAY)
                }
                if (resultContextItemRow.VALUE_NUM || resultContextItemRow.VALUE_NUM.toString().isInteger()) {
                    attributes.put('valueNum', resultContextItemRow.VALUE_NUM)
                }
                if (resultContextItemRow.VALUE_MIN || resultContextItemRow.VALUE_MIN.toString().isInteger()) {
                    attributes.put('valueMin', resultContextItemRow.VALUE_MIN)
                }
                if (resultContextItemRow.VALUE_MAX || resultContextItemRow.VALUE_MAX.toString().isInteger()) {
                    attributes.put('valueMax', resultContextItemRow.VALUE_MAX)
                }


                xml.resultContextItem(attributes) {
                    if (resultContextItemRow.ATTRIBUTE_ID || resultContextItemRow.ATTRIBUTE_ID.toString().isInteger()) {
                        attributeId() {
                            link(rel: 'related', href: "${ELEMENT_BASE_URL}${resultContextItemRow.ATTRIBUTE_ID}", type: "${ELEMENT_MEDIA_TYPE}")
                        }
                    }
                    if (resultContextItemRow.VALUE_ID || resultContextItemRow.VALUE_ID.toString().isInteger()) {
                        valueId() {
                            link(rel: 'related', href: "${ELEMENT_BASE_URL}${resultContextItemRow.VALUE_ID}", type: "${ELEMENT_MEDIA_TYPE}")
                        }
                    }

                }
            }
        }
    }
    protected void generateExperimentLinks(final MarkupBuilder xml, final BigDecimal projectId, final BigDecimal assayId, final BigDecimal experimentId) {
        //links to assay and project
        def projectHref = "${PROJECT_URL_PREFIX}${projectId}"
        def assayHref = "${ASSAY_URL_PREFIX}${assayId}"
        def experimentHref = "${EXPERIMENT_URL_PREFIX}${experimentId}"
        def resultsHref = "${experimentHref}/results"
        xml.link(rel: 'related', title: 'Link to Project', type: "${CAP_MEDIA_TYPE}", href: projectHref) {
        }

        xml.link(rel: 'related', title: 'Link to Assay', type: "${CAP_MEDIA_TYPE}", href: assayHref) {
        }

        xml.link(rel: 'edit', title: 'Use link to edit Experiment', type: "${EXPERIMENT_MEDIA_TYPE}", href: experimentHref) {
        }
        xml.link(rel: 'related', title: 'List Related Results', type: "${RESULTS_MEDIA_TYPE}", href: "${resultsHref}") {
        }
        xml.link(rel: 'up', title: 'List Experiments', type: "${EXPERIMENTS_MEDIA_TYPE}", href: "${EXPERIMENTS_URL_PREFIX}") {
        }
    }

    public void generateExperiment(final MarkupBuilder xml, final BigDecimal experimentId) {
        final Sql sql = new Sql(dataSource)
        sql.eachRow('select * FROM EXPERIMENT WHERE EXPERIMENT_ID=' + experimentId) { experimentRow ->

            final ExperimentDTO experimentDTO =
                new ExperimentDTO(
                        experimentRow.EXPERIMENT_ID,
                        experimentRow.EXPERIMENT_NAME,
                        experimentRow.EXPERIMENT_STATUS_ID,
                        experimentRow.HOLD_UNTIL_DATE,
                        experimentRow.RUN_DATE_FROM,
                        experimentRow.RUN_DATE_TO,
                        experimentRow.DESCRIPTION,
                        experimentRow.PROJECT_ID,
                        experimentRow.ASSAY_ID)

            generateExperiment(sql, xml, experimentDTO)
        }
    }

    protected void generateExperiment(final Sql sql, final MarkupBuilder xml, final ExperimentDTO dto) {
        def resultsCount = null
        sql.eachRow('select count(*)  as resultsCount from RESULT WHERE EXPERIMENT_ID=' + dto.experimentId) { resultsCountRow ->
            resultsCount = resultsCountRow.resultsCount
        }

        def attributes = [:]
        attributes.put('count', resultsCount)
        attributes.put('experimentName', dto.experimentName)



        if (dto.holdUntilDate) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(dto.holdUntilDate);
            final XMLGregorianCalendar holdUntilDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            attributes.put('holdUntilDate', holdUntilDate.toString())
        }
        if (dto.runFromDate) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(dto.runFromDate);
            final XMLGregorianCalendar runFromDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            attributes.put('runDateFrom', runFromDate.toString())
        }
        if (dto.runToDate) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(dto.runToDate);
            final XMLGregorianCalendar runDateTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            attributes.put('runDateTo', runDateTo.toString())
        }

        xml.experiment(
                attributes) {

            generateExperimentStatus(sql, xml, dto.experimentStatusId)
            if (dto.description) {
                description(dto.description)
            }
            generateResultContextItems(sql,xml,dto.experimentId)
            generateExperimentLinks(xml, dto.projectId, dto.assayId, dto.experimentId)
        }
    }

    public String generateExperiments(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)
        generateExperiments(sql, xml)
    }

    protected String generateExperiments(final Sql sql, final MarkupBuilder xml) {

        def count = null
        sql.eachRow('select count(*)  as experimentsCount from EXPERIMENT') { countRow ->
            count = countRow.experimentsCount
        }
        int counter = 0
        xml.experiments(count: count) {
            sql.eachRow('select * FROM EXPERIMENT') { experimentRow ->
                if (counter <= MAX_EXPERIMENTS_RECORDS_PER_PAGE) { //probably use rowNum instead


                    final ExperimentDTO experimentDTO =
                        new ExperimentDTO(
                                experimentRow.EXPERIMENT_ID,
                                experimentRow.EXPERIMENT_NAME,
                                experimentRow.EXPERIMENT_STATUS_ID,
                                experimentRow.HOLD_UNTIL_DATE,
                                experimentRow.RUN_DATE_FROM,
                                experimentRow.RUN_DATE_TO,
                                experimentRow.DESCRIPTION,
                                experimentRow.PROJECT_ID,
                                experimentRow.ASSAY_ID)

                    generateExperiment(sql, xml, experimentDTO)
                    counter++
                }
            }
            if (count > MAX_EXPERIMENTS_RECORDS_PER_PAGE) {
                link(rel: 'next', title: 'List Experiments', type: "${EXPERIMENTS_MEDIA_TYPE}", href: "${EXPERIMENTS_URL_PREFIX}") {
                }
            }
        }
    }


}
class ExperimentDTO {
    final BigDecimal experimentId
    final String experimentName
    final BigDecimal experimentStatusId
    final Date holdUntilDate
    final Date runFromDate
    final Date runToDate
    final String description
    final BigDecimal projectId
    final BigDecimal assayId

    ExperimentDTO(final BigDecimal experimentId,
                  final String experimentName,
                  final BigDecimal experimentStatusId,
                  final Date holdUntilDate,
                  final Date runFromDate,
                  final Date runToDate,
                  final String description,
                  final BigDecimal projectId,
                  final BigDecimal assayId) {
        this.experimentId = experimentId
        this.experimentName = experimentName
        this.experimentStatusId = experimentStatusId
        this.runFromDate = runFromDate
        this.runToDate = runToDate
        this.description = description
        this.projectId = projectId
        this.assayId = assayId

    }

}




