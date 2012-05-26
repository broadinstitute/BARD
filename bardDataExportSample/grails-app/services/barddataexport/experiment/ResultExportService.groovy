package barddataexport.experiment

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.sql.DataSource

class ResultExportService {
    DataSource dataSource
    GrailsApplication grailsApplication
    LinkGenerator grailsLinkGenerator

    /**
     * Generate the result element
     *
     * @param sql
     * @param xml
     * @param experimentId
     */
    protected void generateResult(final Sql sql, final MarkupBuilder xml, final ResultDTO resultDTO, final String experimentHref) {

        final BigDecimal resultId = resultDTO.resultId
        final String resultHref = grailsLinkGenerator.link(mapping: 'result', absolute: true, params: [id: resultId]).toString()

        def attributes = [:]

        if (resultDTO.valueDisplay) {
            attributes.put('valueDisplay', resultDTO.valueDisplay)
        }
        if (resultDTO.valueNum || resultDTO.valueNum.toString().isInteger()) {
            attributes.put('valueNum', resultDTO.valueNum)
        }
        if (resultDTO.valueMin || resultDTO.valueMin.toString().isInteger()) {
            attributes.put('valueMin', resultDTO.valueMin)
        }
        if (resultDTO.valueMax || resultDTO.valueMax.toString().isInteger()) {
            attributes.put('valueMax', resultDTO.valueMax)
        }
        if (resultDTO.qualifier) {
            attributes.put('qualifier', resultDTO.qualifier)
        }
        xml.result(
                attributes
        ) {
            generateResultStatus(sql, xml, resultDTO.statusId)

            if (resultDTO.substanceId || resultDTO.substanceId.toString().isInteger()) {
                final String PUBCHEM_SID_URL = grailsApplication.config.bard.pubchem.sid.url.prefix
                substance(url: "${PUBCHEM_SID_URL}${resultDTO.substanceId}")
            }

            generateResultContextItem(sql, xml, resultId)

            generateResultHierarchy(sql, xml, resultId)

            generateResultLinks(xml, resultId, resultDTO.resultTypeId, experimentHref, resultHref)
        }

    }

    public void generateResult(final MarkupBuilder xml, final BigDecimal resultId) {
        final Sql sql = new Sql(dataSource)

        sql.eachRow("SELECT * FROM RESULT WHERE RESULT_ID=" + resultId) { resultRow ->
            final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: resultRow.EXPERIMENT_ID]).toString()
            ResultDTO resultDTO =
                new ResultDTO(
                        resultRow.RESULT_ID,
                        resultRow.RESULT_STATUS_ID,
                        resultRow.SUBSTANCE_ID,
                        resultRow.RESULT_TYPE_ID,
                        resultRow.VALUE_NUM,
                        resultRow.VALUE_MIN,
                        resultRow.VALUE_MAX,
                        resultRow.VALUE_DISPLAY,
                        resultRow.QUALIFIER
                )
            generateResult(sql, xml, resultDTO, experimentHref)
        }
    }
    /**
     * Generate the results for a given experiment
     * @param experimentId
     * @param numberOfResults
     * @return
     */
    protected void generateResults(final Sql sql, final MarkupBuilder xml, final BigDecimal experimentId, final int numberOfResults) {
        final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: experimentId]).toString()
        final int MAX_RESULTS_RECORDS_PER_PAGE = grailsApplication.config.bard.results.max.per.page

        xml.results(count: numberOfResults) {
            int counter = 0
            sql.eachRow('SELECT * FROM RESULT WHERE EXPERIMENT_ID=' + experimentId) { resultRow ->
                if (counter <= MAX_RESULTS_RECORDS_PER_PAGE) {  //should probably use rowNum instead, but issues with MySQL?
                    ResultDTO resultDTO =
                        new ResultDTO(
                                resultRow.RESULT_ID,
                                resultRow.RESULT_STATUS_ID,
                                resultRow.SUBSTANCE_ID,
                                resultRow.RESULT_TYPE_ID,
                                resultRow.VALUE_NUM,
                                resultRow.VALUE_MIN,
                                resultRow.VALUE_MAX,
                                resultRow.VALUE_DISPLAY,
                                resultRow.QUALIFIER
                        )
                    generateResult(sql, xml, resultDTO, experimentHref)
                    counter++
                }
            }
            generateResultsLinks(xml, experimentHref, numberOfResults)
        }
    }

    /**
     * Generate the results for a given experiment
     * @param experimentId
     * @param numberOfResults
     * @return
     */
    public void generateResults(final MarkupBuilder xml, final BigDecimal experimentId) {
        final Sql sql = new Sql(dataSource)
        int resultsCount = 0
        sql.eachRow('select count(*)  as resultsCount from RESULT WHERE EXPERIMENT_ID=' + experimentId) { resultsCountRow ->
            resultsCount = resultsCountRow.resultsCount
        }

        generateResults(sql, xml, experimentId, resultsCount)
    }
    /**
     * Generate the result status element
     * @param sql
     * @param xm
     * @param statusId
     */
    protected void generateResultStatus(final Sql sql, final MarkupBuilder xml, BigDecimal statusId) {
        String statusValue = null
        if (statusId) {
            sql.eachRow('select STATUS from RESULT_STATUS where RESULT_STATUS_ID=' + statusId) {  statusRow ->
                statusValue = statusRow.STATUS
            }
        }
        if (statusValue) {
            xml.status(statusValue)
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
    protected void generateResultContextItem(final Sql sql, final MarkupBuilder xml, final BigDecimal resultId) {
        xml.resultContextItems() {
            sql.eachRow('SELECT * FROM RESULT_CONTEXT_ITEM WHERE RESULT_ID=' + resultId) { resultContextItemRow ->
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
                    final String ELEMENT_MEDIA_TYPE = grailsApplication.config.bard.data.export.dictionary.element.xml

                    if (resultContextItemRow.ATTRIBUTE_ID || resultContextItemRow.ATTRIBUTE_ID.toString().isInteger()) {
                        final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: "${resultContextItemRow.ATTRIBUTE_ID}"]).toString()

                        attributeId() {
                            link(rel: 'related', href: "${attributeHref}", type: "${ELEMENT_MEDIA_TYPE}")
                        }
                    }
                    if (resultContextItemRow.VALUE_ID || resultContextItemRow.VALUE_ID.toString().isInteger()) {
                        final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: "${resultContextItemRow.VALUE_ID}"]).toString()

                        valueId() {
                            link(rel: 'related', href: "${attributeHref}", type: "${ELEMENT_MEDIA_TYPE}")
                        }
                    }

                }
            }
        }
    }
    /**
     * Generate the RESULT_HIERARCHY element
     *
     * @param sql
     * @param xml
     * @param resultId
     */
    protected void generateResultHierarchy(final Sql sql, final MarkupBuilder xml, final BigDecimal resultId) {
        sql.eachRow('SELECT PARENT_RESULT_ID,HIERARCHY_TYPE FROM RESULT_HIERARCHY WHERE RESULT_ID=' + resultId) { resultHierarchyRow ->
            def attributes = [:]
            if (resultHierarchyRow.PARENT_RESULT_ID || resultHierarchyRow.PARENT_RESULT_ID.toString().isInteger()) {
                attributes.put('parentResultId', resultHierarchyRow.PARENT_RESULT_ID)
            }
            if (resultHierarchyRow.HIERARCHY_TYPE) {
                attributes.put('hierarchyType', resultHierarchyRow.HIERARCHY_TYPE)
            }

            xml.resultHierarchy(
                    attributes) {
            }
        }
    }
    /**
     * Generate the links needed for an individual result element
     * @param sql
     * @param xml
     * @param resultId
     * @param resultTypeId
     * @param experimentHref
     * @param resultHref
     */
    protected void generateResultLinks(final MarkupBuilder xml, final BigDecimal resultId, final BigDecimal resultTypeId, final String experimentHref, final String resultHref) {
        final String EXPERIMENT_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.experiment.xml

        xml.link(rel: 'up', title: 'Experiment', type: "${EXPERIMENT_MEDIA_TYPE}",
                href: experimentHref) {
        }

        final String resultTypeHref = grailsLinkGenerator.link(mapping: 'resultType', absolute: true, params: [id: resultTypeId]).toString()
        final String RESULT_TYPE_MEDIA_TYPE = grailsApplication.config.bard.data.export.dictionary.resultType.xml
        final String RESULT_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.result.xml
        xml.link(rel: 'related', title: 'Result Type', type: "${RESULT_TYPE_MEDIA_TYPE}",
                href: resultTypeHref) {
        }
        xml.link(rel: 'edit', title: 'Edit Link', type: "${RESULT_MEDIA_TYPE}",
                href: resultHref) {
        }
    }
    /**
     * Generate the links needed for the results element
     * @param xml
     * @param experimentHref
     * @param numberOfResults
     */
    protected void generateResultsLinks(final MarkupBuilder xml, final String experimentHref, final int numberOfResults) {
        final String EXPERIMENT_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.experiment.xml

        xml.link(rel: 'collection', title: 'Experiment', type: "${EXPERIMENT_MEDIA_TYPE}",
                href: experimentHref) {
        }
        final int MAX_RESULTS_RECORDS_PER_PAGE = grailsApplication.config.bard.results.max.per.page

        if (numberOfResults > MAX_RESULTS_RECORDS_PER_PAGE) {
            final String RESULTS_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.results.xml
            final String resultsHref = grailsLinkGenerator.link(mapping: 'results', absolute: true, params: [id: experimentId]).toString()
            xml.link(rel: 'next', title: 'Results', type: "${RESULTS_MEDIA_TYPE}",
                    href: resultsHref) {
            }
        }
    }
}
class ResultDTO {
    final String qualifier
    final String valueDisplay
    final BigDecimal valueNum
    final BigDecimal valueMin
    final BigDecimal valueMax
    final BigDecimal resultId
    final BigDecimal statusId
    final BigDecimal substanceId
    final BigDecimal resultTypeId

    ResultDTO(
            final BigDecimal resultId,
            final BigDecimal statusId,
            final BigDecimal substanceId,
            final BigDecimal resultTypeId,
            final BigDecimal valueNum,
            final BigDecimal valueMin,
            final BigDecimal valueMax,
            final String valueDisplay,
            final String qualifier) {
        this.qualifier = qualifier
        this.valueDisplay = valueDisplay
        this.valueMax = valueMax
        this.valueMin = valueMin
        this.valueNum = valueNum
        this.resultId = resultId
        this.statusId = statusId
        this.substanceId = substanceId
        this.resultTypeId = resultTypeId
    }
}
