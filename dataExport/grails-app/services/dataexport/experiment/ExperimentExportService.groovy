package dataexport.experiment

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.sql.DataSource
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

class ExperimentExportService {
    DataSource dataSource
    LinkGenerator grailsLinkGenerator
    GrailsApplication grailsApplication
    ResultExportService resultExportService

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
            sql.eachRow("SELECT * FROM RESULT_CONTEXT_ITEM WHERE EXPERIMENT_ID=" + experimentId + " AND RESULT_ID IS NULL") { resultContextItemRow ->
                resultExportService.generateResultContextItem(
                        xml,
                        new ResultContextItemDTO(
                                resultContextItemRow.RESULT_CONTEXT_ITEM_ID,
                                resultContextItemRow.GROUP_RESULT_CONTEXT_ID,
                                resultContextItemRow.VALUE_NUM,
                                resultContextItemRow.VALUE_MAX,
                                resultContextItemRow.VALUE_MIN,
                                resultContextItemRow.ATTRIBUTE_ID,
                                resultContextItemRow.VALUE_ID,
                                resultContextItemRow.QUALIFIER,
                                resultContextItemRow.VALUE_DISPLAY
                        )
                )
            }
        }
    }

    protected void generateExperimentLinks(final MarkupBuilder xml, final BigDecimal projectId, final BigDecimal assayId, final BigDecimal experimentId) {

        final String PROJECT_MEDIA_TYPE = grailsApplication.config.bard.data.export.cap.project.xml
        final String ASSAY_MEDIA_TYPE = grailsApplication.config.bard.data.export.cap.assay.xml
        final String RESULTS_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.results.xml
        final String EXPERIMENT_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.experiment.xml
        final String EXPERIMENTS_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.experiments.xml


        final String projectHref = grailsLinkGenerator.link(mapping: 'project', absolute: true, params: [id: projectId]).toString()

        final String assayHref = grailsLinkGenerator.link(mapping: 'assay', absolute: true, params: [id: assayId]).toString()

        final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: experimentId]).toString()

        final String experimentsHref = grailsLinkGenerator.link(mapping: 'experiments', absolute: true).toString()

        final String resultsHref = grailsLinkGenerator.link(mapping: 'results', absolute: true, params: [id: experimentId]).toString()

        xml.link(rel: 'related', title: 'Link to Project', type: "${PROJECT_MEDIA_TYPE}", href: projectHref)
        xml.link(rel: 'related', title: 'Link to Assay', type: "${ASSAY_MEDIA_TYPE}", href: assayHref)
        xml.link(rel: 'related', title: 'List Related Results', type: "${RESULTS_MEDIA_TYPE}", href: "${resultsHref}")
        xml.link(rel: 'edit', title: 'Use link to edit Experiment', type: "${EXPERIMENT_MEDIA_TYPE}", href: experimentHref)
        xml.link(rel: 'up', title: 'List Experiments', type: "${EXPERIMENTS_MEDIA_TYPE}", href: "${experimentsHref}")
    }

    public void generateExperiment(final MarkupBuilder xml, final BigDecimal experimentId) {
        final Sql sql = new Sql(dataSource)
        sql.eachRow('SELECT * FROM EXPERIMENT WHERE EXPERIMENT_ID=' + experimentId) { experimentRow ->

            final ExperimentDTO experimentDTO =
                new ExperimentDTO(
                        experimentRow.EXPERIMENT_ID,
                        experimentRow.EXPERIMENT_NAME,
                        experimentRow.EXPERIMENT_STATUS,
                        experimentRow.HOLD_UNTIL_DATE,
                        experimentRow.RUN_DATE_FROM,
                        experimentRow.RUN_DATE_TO,
                        experimentRow.DESCRIPTION,
                        experimentRow.PROJECT_ID,
                        experimentRow.ASSAY_ID,
                        experimentRow.READY_FOR_EXTRACTION
                )
            generateExperiment(sql, xml, experimentDTO)
        }
    }

    protected void generateExperiment(final Sql sql, final MarkupBuilder xml, final ExperimentDTO dto) {
        def resultsCount = null
        sql.eachRow('SELECT count(*) AS RESULTSCOUNT FROM RESULT WHERE EXPERIMENT_ID=' + dto.experimentId) { resultsCountRow ->
            resultsCount = resultsCountRow.RESULTSCOUNT
        }

        def attributes = [:]
        attributes.put('numberOfResults', resultsCount)
        attributes.put('experimentName', dto.experimentName)
        attributes.put("readyForExtraction", dto.readyForExtraction)


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


            experimentStatus() {
                status(dto.experimentStatus)
            }
            if (dto.description) {
                description(dto.description)
            }
            generateResultContextItems(sql, xml, dto.experimentId)
            generateExperimentLinks(xml, dto.projectId, dto.assayId, dto.experimentId)
        }
    }

    public void generateExperiments(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)
        generateExperiments(sql, xml)
    }

    protected void generateExperiments(final Sql sql, final MarkupBuilder xml) {
        final int MAX_EXPERIMENTS_RECORDS_PER_PAGE = grailsApplication.config.bard.experiments.max.per.page
        def count = null
        sql.eachRow("select count(*)  as experimentsCount FROM EXPERIMENT WHERE READY_FOR_EXTRACTION='Ready'") { countRow ->
            count = countRow.experimentsCount
        }
        if (count == 0) {
            return
        }
        int counter = 0
        xml.experiments(count: count) {
            sql.eachRow("SELECT * FROM EXPERIMENT WHERE READY_FOR_EXTRACTION='Ready'") { experimentRow ->
                if (counter <= MAX_EXPERIMENTS_RECORDS_PER_PAGE) { //probably use rowNum instead

                    final ExperimentDTO experimentDTO =
                        new ExperimentDTO(
                                experimentRow.EXPERIMENT_ID,
                                experimentRow.EXPERIMENT_NAME,
                                experimentRow.EXPERIMENT_STATUS,
                                experimentRow.HOLD_UNTIL_DATE,
                                experimentRow.RUN_DATE_FROM,
                                experimentRow.RUN_DATE_TO,
                                experimentRow.DESCRIPTION,
                                experimentRow.PROJECT_ID,
                                experimentRow.ASSAY_ID,
                                experimentRow.READY_FOR_EXTRACTION
                        )

                    generateExperiment(sql, xml, experimentDTO)
                    counter++
                }
            }
            if (count > MAX_EXPERIMENTS_RECORDS_PER_PAGE) {
                def experimentsHref = grailsLinkGenerator.link(mapping: 'experiments', absolute: true).toString()
                final String EXPERIMENTS_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.experiments.xml
                link(rel: 'next', title: 'List Experiments', type: "${EXPERIMENTS_MEDIA_TYPE}", href: "${experimentsHref}")
            }
        }
    }


}
class ExperimentDTO {
    final BigDecimal experimentId
    final String experimentName
    final String experimentStatus
    final Date holdUntilDate
    final Date runFromDate
    final Date runToDate
    final String description
    final BigDecimal projectId
    final BigDecimal assayId
    final String readyForExtraction

    ExperimentDTO(final BigDecimal experimentId,
                  final String experimentName,
                  final String experimentStatus,
                  final Date holdUntilDate,
                  final Date runFromDate,
                  final Date runToDate,
                  final String description,
                  final BigDecimal projectId,
                  final BigDecimal assayId,
                  final String readyForExtraction) {
        this.experimentId = experimentId
        this.holdUntilDate = holdUntilDate
        this.experimentName = experimentName
        this.experimentStatus = experimentStatus
        this.runFromDate = runFromDate
        this.runToDate = runToDate
        this.description = description
        this.projectId = projectId
        this.assayId = assayId
        this.readyForExtraction = readyForExtraction
    }

}




