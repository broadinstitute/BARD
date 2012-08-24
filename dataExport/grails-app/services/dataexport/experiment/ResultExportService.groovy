package dataexport.experiment

//import bard.db.experiment.Experiment


import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.Result
import bard.db.experiment.ResultContextItem
import bard.db.experiment.ResultHierarchy
import dataexport.registration.BardHttpResponse
import dataexport.registration.MediaTypesDTO
import exceptions.NotFoundException
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import groovy.xml.StaxBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.sql.DataSource
import dataexport.util.UtilityService
import bard.db.experiment.RunContextItem

class ResultExportService {
    LinkGenerator grailsLinkGenerator
    MediaTypesDTO mediaTypes
    int maxResultsRecordsPerPage
    DataSource dataSource
    UtilityService utilityService
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
        return utilityService.update(result,id,clientVersion,latestStatus,"Result")
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
            attributes.put('readyForExtraction', result.readyForExtraction)
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
            final Element resultType = currentResult.resultType
            if (resultType) { //this is the currentResult type
                resultTypeRef(label: resultType.label) {
                    final String href = grailsLinkGenerator.link(mapping: 'resultType', absolute: true, params: [id: resultType.id]).toString()
                    link(rel: 'related', href: "${href}", type: "${this.mediaTypes.resultTypeMediaType}")
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

        int end = this.maxResultsRecordsPerPage + 1  //A trick to know if there are more records
        boolean hasMoreResults = false //This is used for paging, if there are more results than the threshold, add next link and return true

        List<Result> results = Result.findAllByExperimentAndReadyForExtraction(experiment, 'Ready', [sort: "id", order: "asc", offset: offset, max: end])
        final int numberOfResults = results.size()
        if (numberOfResults > this.maxResultsRecordsPerPage) {
            hasMoreResults = true
            results = results.subList(0, this.maxResultsRecordsPerPage) //we will leave one record behind but that is OK, since now we know there are more records
        }
        offset = this.maxResultsRecordsPerPage  //reset this to the max number of records
        markupBuilder.results(count: results.size()) {
            for (Result result : results) {
                final String resultHref = grailsLinkGenerator.link(mapping: 'result', absolute: true, params: [id: result.id]).toString()
                link(rel: 'related', type: "${this.mediaTypes.resultMediaType}", href: "${resultHref}")
            }
            generateResultsLinks(markupBuilder, experiment.id, hasMoreResults, offset)
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
                generateResultContextItem(markupBuilder, resultContextItem)
            }
        }
    }
    /**
     *
     * @param runContextItem
     * @return
     */
    protected Map<String, String> generateAttributesForRunContextItem(final RunContextItem runContextItem) {
        Map<String, String> attributes = [:]
        attributes.put(runContextItem.getClass().getName() + "Id", runContextItem.id?.toString())
        if (runContextItem.groupResultContext && runContextItem.groupResultContext.id.toString().isInteger()) {
            attributes.put('parentGroup', runContextItem.groupResultContext.id.toString())
        }

        if (runContextItem.qualifier) {
            attributes.put('qualifier', runContextItem.qualifier)
        }
        if (runContextItem.valueDisplay) {
            attributes.put('valueDisplay', runContextItem.valueDisplay)
        }
        if (runContextItem.valueNum || runContextItem.valueNum.toString().isInteger()) {
            attributes.put('valueNum', runContextItem.valueNum.toString())
        }
        if (runContextItem.valueMin || runContextItem.valueMin.toString().isInteger()) {
            attributes.put('valueMin', runContextItem.valueMin.toString())
        }
        if (runContextItem.valueMax || runContextItem.valueMax.toString().isInteger()) {
            attributes.put('valueMax', runContextItem.valueMax.toString())
        }
        return attributes
    }
    /**
     *
     * @param markupBuilder
     * @param resultContextItem
     */
    protected void generateRunContextItem(def markupBuilder, final RunContextItem runContextItem) {

        final Map<String, String> attributes = generateAttributesForRunContextItem(runContextItem)

        markupBuilder.resultContextItem(attributes) {
            if (runContextItem.attributeElement) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: "${runContextItem.attributeElement.id}"]).toString()
                attribute(label: runContextItem.attributeElement.label) {
                    link(rel: 'related', href: "${attributeHref}", type: "${this.mediaTypes.elementMediaType}")
                }
            }
            if (runContextItem.valueElement) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: "${runContextItem.valueElement.id}"]).toString()

                valueControlled(label: runContextItem.valueElement.label) {
                    link(rel: 'related', href: "${attributeHref}", type: "${this.mediaTypes.elementMediaType}")
                }
            }
            if (runContextItem.extValueId) {
                extValueId(runContextItem.extValueId)
            }

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