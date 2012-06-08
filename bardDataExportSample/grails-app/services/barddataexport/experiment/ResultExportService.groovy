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
        if (resultDTO.readyForExtraction) {
            attributes.put('readyForExtraction', resultDTO.readyForExtraction)
        }

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
            if (resultDTO.status) {
                status(resultDTO.status)
            }
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
                        resultRow.RESULT_STATUS,
                        resultRow.SUBSTANCE_ID,
                        resultRow.RESULT_TYPE_ID,
                        resultRow.VALUE_NUM,
                        resultRow.VALUE_MIN,
                        resultRow.VALUE_MAX,
                        resultRow.VALUE_DISPLAY,
                        resultRow.QUALIFIER,
                        resultRow.READY_FOR_EXTRACTION
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

        xml.results(count: numberOfResults) {
            sql.eachRow('SELECT * FROM RESULT WHERE EXPERIMENT_ID=' + experimentId) { resultRow ->
                final String resultHref = grailsLinkGenerator.link(mapping: 'result', absolute: true, params: [id: resultRow.RESULT_ID]).toString()
                final String RESULT_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.result.xml
                xml.link(rel: 'related', title: '', type: "${RESULT_MEDIA_TYPE}",
                        href: resultHref) {
                }
            }
            generateResultsLinks(xml, experimentHref, numberOfResults, experimentId)
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
        sql.eachRow('SELECT COUNT(*) AS RESULTSCOUNT FROM RESULT WHERE EXPERIMENT_ID=' + experimentId) { resultsCountRow ->
            resultsCount = resultsCountRow.resultsCount
        }

        generateResults(sql, xml, experimentId, resultsCount)
    }

    protected void generateResultContextItem(final MarkupBuilder xml, final ResultContextItemDTO resultContextItemDTO) {
        def attributes = [:]
        attributes.put('resultContextItemId', resultContextItemDTO.resultContextItemId)
        if (resultContextItemDTO.groupResultContextItemId || resultContextItemDTO.groupResultContextItemId.toString().isInteger()) {
            attributes.put('groupResultContextItemId', resultContextItemDTO.groupResultContextItemId)
        }

        if (resultContextItemDTO.qualifier) {
            attributes.put('qualifier', resultContextItemDTO.qualifier)
        }


        if (resultContextItemDTO.valueDisplay) {
            attributes.put('valueDisplay', resultContextItemDTO.valueDisplay)
        }
        if (resultContextItemDTO.valueNum || resultContextItemDTO.valueNum.toString().isInteger()) {
            attributes.put('valueNum', resultContextItemDTO.valueNum)
        }
        if (resultContextItemDTO.valueMin || resultContextItemDTO.valueMin.toString().isInteger()) {
            attributes.put('valueMin', resultContextItemDTO.valueMin)
        }
        if (resultContextItemDTO.valueMax || resultContextItemDTO.valueMax.toString().isInteger()) {
            attributes.put('valueMax', resultContextItemDTO.valueMax)
        }


        xml.resultContextItem(attributes) {
            final String ELEMENT_MEDIA_TYPE = grailsApplication.config.bard.data.export.dictionary.element.xml

            if (resultContextItemDTO.attributeId || resultContextItemDTO.attributeId.toString().isInteger()) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: "${resultContextItemDTO.attributeId}"]).toString()

                attributeId() {
                    link(rel: 'related', href: "${attributeHref}", type: "${ELEMENT_MEDIA_TYPE}")
                }
            }
            if (resultContextItemDTO.valueId || resultContextItemDTO.valueId.toString().isInteger()) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: "${resultContextItemDTO.valueId}"]).toString()

                valueId() {
                    link(rel: 'related', href: "${attributeHref}", type: "${ELEMENT_MEDIA_TYPE}")
                }
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
    protected void generateResultContextItem(final Sql sql, final MarkupBuilder xml, final BigDecimal resultId) {
        xml.resultContextItems() {
            sql.eachRow('SELECT * FROM RESULT_CONTEXT_ITEM WHERE RESULT_ID=' + resultId) { resultContextItemRow ->
                generateResultContextItem(xml,
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
    /**
     * Generate the RESULT_HIERARCHY element
     *
     * @param sql
     * @param xml
     * @param resultId
     */
    protected void generateResultHierarchy(final Sql sql, final MarkupBuilder xml, final BigDecimal resultId) {
        sql.eachRow('SELECT PARENT_RESULT_ID,HIERARCHY_TYPE FROM RESULT_HIERARCHY WHERE RESULT_ID=' + resultId) { resultHierarchyRow ->
            ResultHierarchyDTO resultHierarchyDTO = new ResultHierarchyDTO(resultHierarchyRow.PARENT_RESULT_ID, resultHierarchyRow.HIERARCHY_TYPE)
            generateResultHierarchy(xml, resultHierarchyDTO)
        }
    }
    /**
     * Generate the RESULT_HIERARCHY element
     *
     * @param sql
     * @param xml
     * @param resultId
     */
    protected void generateResultHierarchy(final MarkupBuilder xml, ResultHierarchyDTO resultHierarchyDTO) {
        def attributes = [:]
        if (resultHierarchyDTO.parentResultId || resultHierarchyDTO.parentResultId.toString().isInteger()) {
            attributes.put('parentResultId', resultHierarchyDTO.parentResultId)
        }
        if (resultHierarchyDTO.hierarchyType) {
            attributes.put('hierarchyType', resultHierarchyDTO.hierarchyType)
        }

        xml.resultHierarchy(attributes)

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
    protected void generateResultsLinks(final MarkupBuilder xml, final String experimentHref, final int numberOfResults, final BigDecimal experimentId) {
        final String EXPERIMENT_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.experiment.xml

        xml.link(rel: 'collection', title: 'Experiment', type: "${EXPERIMENT_MEDIA_TYPE}",
                href: experimentHref) {
        }
//        final int MAX_RESULTS_RECORDS_PER_PAGE = grailsApplication.config.bard.results.max.per.page
//
//        if (numberOfResults > MAX_RESULTS_RECORDS_PER_PAGE) {
//            final String RESULTS_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.results.xml
//            final String resultsHref = grailsLinkGenerator.link(mapping: 'results', absolute: true, params: [id: experimentId]).toString()
//            xml.link(rel: 'next', title: 'Results', type: "${RESULTS_MEDIA_TYPE}",
//                    href: resultsHref) {
//            }
//        }
    }
}
class ResultContextItemDTO {
    final BigDecimal resultContextItemId
    final BigDecimal groupResultContextItemId
    final BigDecimal valueNum
    final BigDecimal valueMax
    final BigDecimal valueMin
    final BigDecimal attributeId
    final BigDecimal valueId
    final String qualifier
    final String valueDisplay

    ResultContextItemDTO(
            final BigDecimal resultContextItemId,
            final BigDecimal groupResultContextItemId,
            final BigDecimal valueNum,
            final BigDecimal valueMax,
            final BigDecimal valueMin,
            final BigDecimal attributeId,
            final BigDecimal valueId,
            final String qualifier,
            final String valueDisplay
    ) {
        this.resultContextItemId = resultContextItemId
        this.groupResultContextItemId = groupResultContextItemId
        this.valueNum = valueNum
        this.valueMax = valueMax
        this.valueMin = valueMin
        this.attributeId = attributeId
        this.valueId = valueId
        this.qualifier = qualifier
        this.valueDisplay = valueDisplay
    }

}

class ResultHierarchyDTO {
    final BigDecimal parentResultId
    final String hierarchyType

    ResultHierarchyDTO(final BigDecimal parentResultId, final String hierarchyType) {
        this.parentResultId = parentResultId
        this.hierarchyType = hierarchyType
    }

}
class ResultDTO {
    final String qualifier
    final String valueDisplay
    final BigDecimal valueNum
    final BigDecimal valueMin
    final BigDecimal valueMax
    final BigDecimal resultId
    final String status
    final BigDecimal substanceId
    final BigDecimal resultTypeId
    final String readyForExtraction

    ResultDTO(
            final BigDecimal resultId,
            final String status,
            final BigDecimal substanceId,
            final BigDecimal resultTypeId,
            final BigDecimal valueNum,
            final BigDecimal valueMin,
            final BigDecimal valueMax,
            final String valueDisplay,
            final String qualifier,
            final String readyForExtraction
    ) {
        this.qualifier = qualifier
        this.valueDisplay = valueDisplay
        this.valueMax = valueMax
        this.valueMin = valueMin
        this.valueNum = valueNum
        this.resultId = resultId
        this.status = status
        this.substanceId = substanceId
        this.resultTypeId = resultTypeId
        this.readyForExtraction = readyForExtraction
    }
}
