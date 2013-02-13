package dataexport.experiment

//import bard.db.experiment.Experiment


import bard.db.dictionary.Element
import bard.db.enums.ReadyForExtraction
import dataexport.registration.BardHttpResponse
import dataexport.registration.MediaTypesDTO
import dataexport.util.UtilityService
import exceptions.NotFoundException
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import groovy.xml.StaxBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.sql.DataSource

import bard.db.experiment.*
import org.apache.commons.lang.time.StopWatch
import org.hibernate.SessionFactory
import org.hibernate.Session
import bard.db.model.AbstractContextItem

class ResultExportService {
    LinkGenerator grailsLinkGenerator
    MediaTypesDTO mediaTypes
    int maxResultsRecordsPerPage
    DataSource dataSource
    UtilityService utilityService
    SessionFactory sessionFactory
    /**
     * Set the ReadyForExtraction value on the element to 'Complete'
     *
     * Return a 409, conflict, if the version supplied by client is less than the version in the database
     *
     * Return a 412, precondition failed, if the version supplied by client is not equal to the version in the database
     *
     * Return a 404 , if the element cannot be found
     *
     * @param id
     * @param version
     * Returns the HTTPStatus Code
     */
    public BardHttpResponse update(final Long id, final Long clientVersion, final String latestStatus) {
        final Result result = Result.findById(id)
        return utilityService.update(result,id,clientVersion,ReadyForExtraction.byId(latestStatus),"Result")
    }
    /**
     * Generate the results for a given experiment
     * @param markupBuilder - The markup builder to write
     * @param experimentId - The id of the experiment
     * @param offset - For paging
     * @return true if there are more results than can fit on a page
     */
    public boolean generateResults(final StaxBuilder markupBuilder, final Long experimentId, final int offset) {
        Experiment experiment = Experiment.get(experimentId)
        if (!experiment) {
            throw new NotFoundException("Could not find Experiment with Id ${experimentId}")
        }
        return generateResults(markupBuilder, experiment, offset)
    }
    /**
     * Given a resultId, find the currentResult and generate an XML object
     * @param markupBuilder
     * @param resultId
     */
    public Long generateResult(final MarkupBuilder markupBuilder, final Long resultId) {

        final Result result = Result.get(resultId)
        if (!result) {
            throw new NotFoundException("Could not find Result with Id ${resultId}")
        }
        generateResult(markupBuilder, result)
        return result.version

    }

    protected Map<String, String> generateAttributesForResult(final Result result) {
        Map<String, String> attributes = [:]

        if (result.readyForExtraction) {
            attributes.put('readyForExtraction', result.readyForExtraction.toString())
        }
        if (result.valueDisplay) {
            attributes.put('valueDisplay', result.valueDisplay)
        }
        if (result.valueNum || result.valueNum.toString().isInteger()) {
            attributes.put('valueNum', result.valueNum.toString())
        }
        if (result.valueMin || result.valueMin.toString().isInteger()) {
            attributes.put('valueMin', result.valueMin.toString())
        }
        if (result.valueMax || result.valueMax.toString().isInteger()) {
            attributes.put('valueMax', result.valueMax.toString())
        }
        if (result.qualifier) {
            attributes.put('qualifier', result.qualifier)
        }
        if (result.resultStatus) {
            attributes.put('status', result.resultStatus)
        }
        return attributes
    }
    /**
     * Generate the currentResult element
     * @param markupBuilder
     * @param currentResult
     *
     */
    protected void generateResult(final MarkupBuilder markupBuilder, final Result currentResult) {

        final Map<String, String> attributes = generateAttributesForResult(currentResult)

        markupBuilder.result(attributes) {
            final Element resultTypeElement = currentResult.resultType
            if (resultTypeElement) { //this is the currentResult type
                resultType(label: resultTypeElement.label) {
                    final String href = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: resultTypeElement.id]).toString()
                    link(rel: 'related', href: "${href}", type: "${this.mediaTypes.elementMediaType}")
                }
            }
            //TODO: The substance table, does not exist yet. We will need to fix the domain model plugin, but only after
            //we decide on whether to cache all substances or validate on demand. So we will make a ronw SQL query for the SID
            final Sql sql = new Sql(dataSource)
            sql.eachRow('SELECT SUBSTANCE_ID FROM RESULT WHERE RESULT_ID=' + currentResult.id) { row ->
                def substances = row.SUBSTANCE_ID
                if (substances) {
                    substance(sid: substances.toString()) {
                    }
                }
            }


            final Set<ResultContextItem> resultContextItems = currentResult.resultContextItems
            if (resultContextItems) {
                generateResultContextItems(markupBuilder, resultContextItems)
            }

            final Set<ResultHierarchy> hierarchies = [] as Set<ResultHierarchy>
            if (currentResult.resultHierarchiesForParentResult) {
                hierarchies.addAll(currentResult.resultHierarchiesForParentResult)
            }
            if (currentResult.resultHierarchiesForResult) {
                hierarchies.addAll(currentResult.resultHierarchiesForResult)
            }
            if (hierarchies) {
                generateResultHierarchies(markupBuilder, hierarchies)
            }
            generateResultLinks(markupBuilder, currentResult)
        }

    }
    /**
     * Generate the links needed for an individual currentResult element
     * @param markupBuilder
     * @param result
     */
    protected void generateResultLinks(def markupBuilder, final Result result) {

        final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: result.experiment.id]).toString()

        markupBuilder.link(rel: 'up', title: 'Experiment', type: "${this.mediaTypes.experimentMediaType}",
                href: experimentHref) {
        }
        final String resultHref = grailsLinkGenerator.link(mapping: 'result', absolute: true, params: [id: result.id]).toString()
        markupBuilder.link(rel: 'edit', type: "${this.mediaTypes.resultMediaType}",
                href: resultHref) {
        }
    }
    /**
     * Generate the results for a given experiment
     * @param experimentId
     * @param numberOfResults
     * @param offset - Used for paging, marks the position of the current currentResult element within the experiment
     * @return true if there are more results than can fit on a page
     */
    protected boolean generateResults(final StaxBuilder markupBuilder, final Experiment experiment, int offset) {
        int max = offset + this.maxResultsRecordsPerPage + 1  //A trick to know if there are more records

        boolean hasMoreResults = false //This is used for paging, if there are more results than the threshold, add next link and return true

        String RESULTS_BY_ID_QUERY = """
          SELECT RESULT_ID
        FROM (SELECT RESULT_ID, ROWNUM AS RESULT_ROW_NUM
                FROM ( SELECT RESULT_ID
                        FROM RESULT
                        WHERE RESULT.EXPERIMENT_ID = ${experiment.id}
                        AND READY_FOR_EXTRACTION='Ready'
                        ORDER BY RESULT_ID ASC
                )
        )
        WHERE RESULT_ROW_NUM BETWEEN ${offset} AND ${max}
        """

        final Session currentSession = sessionFactory.currentSession
        StopWatch stopWatch = new StopWatch()
        stopWatch.start()
        List<Long> resultIds = currentSession.createSQLQuery(RESULTS_BY_ID_QUERY).list()
        stopWatch.stop()
        log.info("Query ${RESULTS_BY_ID_QUERY} took ${stopWatch.toString()}")
        final int numberOfResults = resultIds.size()
        if (numberOfResults > this.maxResultsRecordsPerPage) {
            hasMoreResults = true
            resultIds = resultIds.subList(0, this.maxResultsRecordsPerPage) //we will leave one record behind but that is OK, since now we know there are more records
        }
        int currentoffSet = offset + this.maxResultsRecordsPerPage  //reset this to the max number of records
        markupBuilder.results(count: resultIds.size()) {
            for (Long resultId : resultIds) {
                final String resultHref = grailsLinkGenerator.link(mapping: 'result', absolute: true, params: [id: resultId]).toString()
                link(rel: 'related', type: "${this.mediaTypes.resultMediaType}", href: "${resultHref}")
            }
            generateResultsLinks(markupBuilder, experiment.id, hasMoreResults, currentoffSet)
        }
        return hasMoreResults
    }
    /**
     * Generate the links needed for the results element
     *
     * @param markupBuilder
     * @param experimentId
     * @param hasMoreResults
     * @param offset - Used for paging results, where to start the next time ou try to fetch results
     */
    protected void generateResultsLinks(final StaxBuilder markupBuilder, final Long experimentId, final boolean hasMoreResults, final int offset) {
        //if there are more records that can fit on a page then add the next link, with the offset parameter
        //being the end variable in this method
        if (hasMoreResults) {
            final String resultsHref = grailsLinkGenerator.link(mapping: 'results', absolute: true, params: [id: experimentId, offset: offset]).toString()
            markupBuilder.link(rel: 'next', type: "${this.mediaTypes.resultsMediaType}", href: "${resultsHref}")
        }
        final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: experimentId]).toString()
        markupBuilder.link(rel: 'up', type: "${this.mediaTypes.experimentMediaType}", href: experimentHref)
    }
    /**
     *
     * @param markupBuilder
     * @param resultContextItems
     */
    protected void generateResultContextItems(def markupBuilder, final Set<ResultContextItem> resultContextItems) {
        markupBuilder.resultContextItems() {
            for (ResultContextItem resultContextItem : resultContextItems) {
                generateContextItem(markupBuilder, resultContextItem)
            }
        }
    }
    /**
     *
     * @param contextItem
     * @return
     */
    protected Map<String, String> generateAttributesForContextItem(final AbstractContextItem contextItem, final String runContextItemIdLabel) {
        Map<String, String> attributes = [:]
        attributes.put(runContextItemIdLabel, contextItem.id?.toString())
        if (contextItem.qualifier) {
            attributes.put('qualifier', contextItem.qualifier)
        }
        if (contextItem.valueDisplay) {
            attributes.put('valueDisplay', contextItem.valueDisplay)
        }
        if (contextItem.valueNum || contextItem.valueNum.toString().isInteger()) {
            attributes.put('valueNum', contextItem.valueNum.toString())
        }
        if (contextItem.valueMin || contextItem.valueMin.toString().isInteger()) {
            attributes.put('valueMin', contextItem.valueMin.toString())
        }
        if (contextItem.valueMax || contextItem.valueMax.toString().isInteger()) {
            attributes.put('valueMax', contextItem.valueMax.toString())
        }
        return attributes
    }
    /**
     *
     * @param markupBuilder
     * @param resultContextItem
     */
    protected void generateContextItem(def markupBuilder, final AbstractContextItem contextItem) {

        final Map<String, String> attributes = generateAttributesForContextItem(contextItem,"resultContextItemId")

        markupBuilder.resultContextItem(attributes) {
            generateContextItemElements(markupBuilder,contextItem)
        }
    }
    /**
     *
     * @param markupBuilder
     * @param resultContextItem
     */
    protected void generateContextItemElements(def markupBuilder, final AbstractContextItem contextItem) {
            if (contextItem.attributeElement) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: "${contextItem.attributeElement.id}"]).toString()
                markupBuilder.attribute(label: contextItem.attributeElement.label) {
                    link(rel: 'related', href: "${attributeHref}", type: "${this.mediaTypes.elementMediaType}")
                }
            }
            if (contextItem.valueElement) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: "${contextItem.valueElement.id}"]).toString()

                markupBuilder.valueControlled(label: contextItem.valueElement.label) {
                    link(rel: 'related', href: "${attributeHref}", type: "${this.mediaTypes.elementMediaType}")
                }
            }
            if (contextItem.extValueId) {
                markupBuilder.extValueId(contextItem.extValueId)
            }

    }
    /**
     * Generate the RESULT_HIERARCHY element
     * @param markupBuilder
     * @param resultHierarchies
     */
    protected void generateResultHierarchies(def markupBuilder, final Set<ResultHierarchy> resultHierarchies) {
        markupBuilder.resultHierarchies() {
            for (ResultHierarchy resultHierarchy : resultHierarchies) {
                generateResultHierarchy(markupBuilder, resultHierarchy)
            }
        }
    }
    /**
     * Generate the RESULT_HIERARCHY element
     * @param markupBuilder
     * @param resultHierarchy
     */
    protected void generateResultHierarchy(def markupBuilder, final ResultHierarchy resultHierarchy) {
        Map<String, String> attributes = [:]
        if (resultHierarchy.parentResult) {
            attributes.put('parentResultId', resultHierarchy.parentResult.id.toString())
        }
        if (resultHierarchy.hierarchyType) {
            attributes.put('hierarchyType', resultHierarchy.hierarchyType.toString())
        }
        markupBuilder.resultHierarchy(attributes)
    }
}