package dataexport.experiment

import bard.db.enums.ExperimentStatus
import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.ExperimentMeasure
import bard.db.registration.ExternalReference
import dataexport.registration.BardHttpResponse
import dataexport.registration.MediaTypesDTO
import dataexport.util.ExportAbstractService
import dataexport.util.UtilityService
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar
import org.apache.commons.lang.StringUtils
import bard.db.enums.HierarchyType

/**
 * Class that generates Experiments as XML
 *
 * We use MarkUpBuilder, but we can switch to StreamingMarkupBuilder
 * or even StAX if we run into memory issues
 * Right now, i have not seen any memory issues yet, but then again, we do not have many real experiment documents
 * I have tested with what i think should be a full experiment, using 100K records and I did not encounter any issues
 */
class ExperimentExportService extends ExportAbstractService {

    UtilityService utilityService
    LinkGenerator grailsLinkGenerator
    MediaTypesDTO mediaTypeDTO
    int numberRecordsPerPage

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
    public BardHttpResponse update(final Long id, final Long clientVersion, final ReadyForExtraction latestStatus) {
        final Experiment experiment = Experiment.findById(id)
        return utilityService.update(experiment, id, clientVersion, latestStatus, "Experiment")
    }
    /**
     *  offset is used for paging, it tells us where we are in the paging process
     * @param markupBuilder
     * @param offset
     * @return true if there are more experiments than can fit on a page
     * this.numberRecordsPerPage is what determines how many experiments should fit on a page
     * Making markupBuilder def, so we can use any of the markup builders (Stax, markupBuilder etc) at run time
     * For instance, if we are generating just one experiment, we should just use MarkUpBuilder
     */
    public boolean generateExperiments(def markupBuilder, int offset) {
        int end = this.numberRecordsPerPage + 1  //A trick to know if there are more records
        boolean hasMoreExperiments = false //This is used for paging, if there are more experiments than the threshold, add next link and return true

        String experimentIdQuery = 'select distinct experiment.id from Experiment experiment where experiment.readyForExtraction=:ready order by experiment.id asc'
        List<Long> experimentIds = (List<Long>) Experiment.executeQuery(experimentIdQuery, [ready: ReadyForExtraction.READY, offset: offset, max: end])

        final int numberOfExperiments = experimentIds.size()
        if (numberOfExperiments > this.numberRecordsPerPage) {
            hasMoreExperiments = true
            experimentIds = experimentIds.subList(0, this.numberRecordsPerPage)
        }
        int currentoffSet = offset + this.numberRecordsPerPage  //reset this to the max number of records

        markupBuilder.experiments(count: experimentIds.size()) {
            for (Long experimentId : experimentIds) {
                generateLink(
                        [
                                mapping: 'experiment', absolute: true,
                                rel: 'related', mediaType: this.mediaTypeDTO.experimentMediaType,
                                params: [id: experimentId]
                        ],
                        markupBuilder,
                        this.grailsLinkGenerator
                )
            }
            //if there are more records that can fit on a page then add the next link, with the offset parameter
            //being the end variable in this method
            if (hasMoreExperiments) {
                generateLink(
                        [mapping: 'experiments', absolute: true,
                                rel: 'next', mediaType: this.mediaTypeDTO.experimentsMediaType,
                                params: [offset: currentoffSet]
                        ]
                        ,
                        markupBuilder,
                        this.grailsLinkGenerator
                )
            }
        }
        return hasMoreExperiments
    }
    /**
     * @param markupBuilder
     * @param experimentId
     */
    public Long generateExperiment(final MarkupBuilder markupBuilder, final Long experimentId) {
        final Experiment experiment = Experiment.get(experimentId)
        if (!experiment) {
            throw new NotFoundException("Could not find Experiment with Id ${experimentId}")
        }
        this.generateExperiment(markupBuilder, experiment)
        return experiment.version
    }

    private String convertStatusToString(ExperimentStatus status) {
        switch(status) {
            case ExperimentStatus.APPROVED:
                return "Approved";
            case ExperimentStatus.DRAFT:
                return "Pending";
            case ExperimentStatus.RETIRED:
                return "Rejected";
            default:
                throw new RuntimeException("invalid status: ${status}")
        }
    }
    /**
     *
     * @param experiment
     * @return a Map of key value pairs that maps to attribute name and value in the generated XML
     */
    protected Map<String, String> generateAttributesForExperiment(final Experiment experiment) {
        Map<String, String> attributes = [:]

        attributes.put("experimentId", experiment.id?.toString())
        attributes.put('status', convertStatusToString(experiment.experimentStatus))
        attributes.put('readyForExtraction', experiment.readyForExtraction.getId())
        attributes.put('confidenceLevel', experiment.confidenceLevel?.toString())
        if(experiment.lastUpdated){
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(experiment.lastUpdated);
            final XMLGregorianCalendar lastUpdatedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
           attributes.put('lastUpdated', lastUpdatedDate.toString())
        }
        if(StringUtils.isNotBlank(experiment.modifiedBy)){
            attributes.put('modifiedBy', experiment.modifiedBy)
        }

        if (experiment.holdUntilDate) {   //convert date to XML date
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(experiment.holdUntilDate);
            final XMLGregorianCalendar holdUntilDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            attributes.put('holdUntilDate', holdUntilDate.toString())
        }
        if (experiment.runDateFrom) {  //convert date to XML date
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(experiment.runDateFrom);
            final XMLGregorianCalendar runDateFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            attributes.put('runDateFrom', runDateFrom.toString())
        }
        if (experiment.runDateTo) {//convert date to XML date
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(experiment.runDateTo);
            final XMLGregorianCalendar runDateTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            attributes.put('runDateTo', runDateTo.toString())
        }
        return attributes
    }
    /**
     * @param markupBuilder
     * @param experiment
     *
     * Serialize Experiment to XML
     */
    protected void generateExperiment(final MarkupBuilder markupBuilder, final Experiment experiment) {

        final Map<String, String> attributes = generateAttributesForExperiment(experiment)

        markupBuilder.experiment(attributes) {
            experimentName(experiment.experimentName)
            if (experiment.description) {
                description(experiment.description)
            }
            generateExperimentContexts(markupBuilder, experiment.experimentContexts)
            generateExperimentMeasures(markupBuilder, experiment.experimentMeasures)
            generateExperimentLinks(markupBuilder, experiment)
        }
    }

    protected void generateExperimentMeasures(MarkupBuilder markupBuilder, Set<ExperimentMeasure> experimentMeasures) {
        if (experimentMeasures) {
            markupBuilder.experimentMeasures() {
                for (ExperimentMeasure experimentMeasure in experimentMeasures) {
                    generateExperimentMeasure(markupBuilder, experimentMeasure)
                }
            }
        }
    }

    protected void generateExperimentContexts(MarkupBuilder markupBuilder, List<ExperimentContext> experimentContexts) {
        if (experimentContexts) {
            markupBuilder.contexts() {
                for (ExperimentContext context in experimentContexts) {
                    generateExperimentContext(markupBuilder, context)
                }
            }
        }
    }
    /**
     *  Generate links from an experiment object
     *  - results, parent experiments,assay, and self
     * @param markupBuilder
     * @param experiment
     */
    protected void generateExperimentLinks(final MarkupBuilder markupBuilder, final Experiment experiment) {

        //link to the assay
        generateLink(
                [mapping: 'assay', absolute: true,
                        rel: 'related', mediaType: this.mediaTypeDTO.assayMediaType,
                        params: [id: experiment.assay.id]
                ]
                ,
                markupBuilder,
                this.grailsLinkGenerator
        )

        //link to list of experiments
        generateLink(
                [mapping: 'experiments', absolute: true,
                        rel: 'up', mediaType: this.mediaTypeDTO.experimentsMediaType
                ]
                ,
                markupBuilder,
                this.grailsLinkGenerator
        )

        //link to results associated with this experiment
        generateLink(
                [mapping: 'results', absolute: true,
                        rel: 'related', mediaType: this.mediaTypeDTO.resultsMediaType,
                        params: [id: experiment.id, offset: 0]
                ]
                ,
                markupBuilder,
                this.grailsLinkGenerator
        )
        generateLink(
                [mapping: 'results', absolute: true,
                        rel: 'related', mediaType: "application/json;type=results",
                        params: [id: experiment.id]
                ]
                ,
                markupBuilder,
                this.grailsLinkGenerator
        )
        //link to edit this experiment. You can only change the ready_for_extraction status
        generateLink(
                [mapping: 'experiment', absolute: true,
                        rel: 'edit', mediaType: this.mediaTypeDTO.experimentMediaType,
                        params: [id: experiment.id]
                ]
                ,
                markupBuilder,
                this.grailsLinkGenerator
        )
        final Set<ExternalReference> externalReferences = experiment.externalReferences
        generateExternalReferencesLink(markupBuilder, externalReferences as List<ExternalReference>, this.grailsLinkGenerator, this.mediaTypeDTO)
    }

    protected void generateExperimentContext(final MarkupBuilder markupBuilder, final ExperimentContext experimentContext) {
        def attributes = ['id': experimentContext.id,
                'displayOrder': experimentContext.experiment.experimentContexts.indexOf(experimentContext)]
        markupBuilder.context(attributes) {
            addContextInformation(markupBuilder, experimentContext)
            generateExperimentContextItems(markupBuilder, experimentContext.experimentContextItems)
        }

    }

    protected void generateExperimentContextItems(MarkupBuilder markupBuilder, List<ExperimentContextItem> experimentContextItems) {
        if (experimentContextItems) {
            markupBuilder.contextItems() {
                for (ExperimentContextItem experimentContextItem : experimentContextItems) {
                    generateExperimentContextItem(markupBuilder, experimentContextItem)
                }
            }
        }
    }

    protected void generateExperimentContextItem(final MarkupBuilder markupBuilder, final ExperimentContextItem experimentContextItem) {
        generateContextItem(
                markupBuilder,
                experimentContextItem,
                null,
                "contextItem",
                this.mediaTypeDTO.elementMediaType,
                grailsLinkGenerator,
                experimentContextItem.id,
                experimentContextItem.experimentContext.experimentContextItems.indexOf(experimentContextItem))
    }

    protected void generateExperimentMeasure(final MarkupBuilder markupBuilder, final ExperimentMeasure experimentMeasure) {
        Map attributes = [experimentMeasureId: experimentMeasure.id,
                measureRef: experimentMeasure.measure.id]
        ExperimentMeasure parent = experimentMeasure.parent
        if (parent) {
            attributes.put('parentExperimentMeasureRef', parent.id)
        }
       HierarchyType parentChildRelationship = experimentMeasure.parentChildRelationship
        if (parentChildRelationship) {
            attributes.put('parentChildRelationship', parentChildRelationship.getId())
        }
        markupBuilder.experimentMeasure(attributes)
    }
}