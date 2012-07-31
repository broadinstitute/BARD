package dataexport.experiment

//import groovy.sql.Sql

//import org.codehaus.groovy.grails.commons.GrailsApplication


import bard.db.dictionary.Element
import bard.db.dictionary.Stage
import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import dataexport.registration.BardHttpResponse
import dataexport.registration.MediaTypesDTO
import dataexport.registration.UpdateType
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.servlet.http.HttpServletResponse
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

import bard.db.experiment.*

/**
 * Class that generates Experiments as XML
 *
 * We use MarkUpBuilder, but we can switch to StreamingMarkupBuilder
 * or even StAX if we run into memory issues
 * Right now, i have not seen any memory issues yet, but then again, we do not have many real experiment documents
 * I have tested with what i think should be a full experiment, using 100K records and I did not encounter any issues
 */
class ExperimentExportService {

    ResultExportService resultExportService
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
    public BardHttpResponse update(final Long id, final Long clientVersion, final String latestStatus) {
        final Experiment experiment = Experiment.findById(id)
        if (!experiment) { //we could not find the element
            throw new NotFoundException("Experiment with ID: ${id}, could not be found")
        }
        //make sure there are no children with a status other than 'Complete'
        final int outStandingResults = Result.countByExperimentAndReadyForExtractionNotEqual(experiment, UpdateType.COMPLETE.description)
        if (outStandingResults > 0) {//this experiments has results that have not yet been consumed
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_NOT_ACCEPTABLE, ETag: experiment.version)
        }


        if (experiment.version > clientVersion) { //There is a conflict, supplied version is less than the current version
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_CONFLICT, ETag: experiment.version)
        }
        if (experiment.version != clientVersion) {//supplied version is not equal to the version in database
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_PRECONDITION_FAILED, ETag: experiment.version)
        }

        final String currentStatus = experiment.readyForExtraction
        if (currentStatus != latestStatus) {
            experiment.readyForExtraction = latestStatus
            experiment.save(flush: true)
        }
        //we probably should supply a new version
        return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_OK, ETag: experiment.version)
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

        List<Experiment> experiments = Experiment.findAllByReadyForExtraction('Ready', [sort: "id", order: "asc", offset: offset, max: end])
        final int numberOfExperiments = experiments.size()
        if (numberOfExperiments > this.numberRecordsPerPage) {
            hasMoreExperiments = true
            experiments = experiments.subList(0, this.numberRecordsPerPage)

        }
        offset = this.numberRecordsPerPage

        markupBuilder.experiments(count: experiments.size()) {
            for (Experiment experiment : experiments) {
                final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: experiment.id]).toString()
                link(rel: 'related', type: "${this.mediaTypeDTO.experimentMediaType}", href: "${experimentHref}")
            }
            //if there are more records that can fit on a page then add the next link, with the offset parameter
            //being the end variable in this method
            if (hasMoreExperiments) {
                final String experimentsHref = grailsLinkGenerator.link(mapping: 'experiments', absolute: true, params: [offset: offset]).toString()
                link(rel: 'next', title: 'List Experiments', type: "${this.mediaTypeDTO.experimentsMediaType}", href: "${experimentsHref}")
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
    /**
     *
     * @param experiment
     * @return a Map of key value pairs that maps to attribute name and value in the generated XML
     */
    protected Map<String, String> generateAttributesForExperiment(final Experiment experiment) {
        Map<String, String> attributes = [:]

        attributes.put("experimentId", experiment.id?.toString())
        attributes.put('experimentName', experiment.experimentName)
        attributes.put('status', experiment.experimentStatus)

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
            if (experiment.description) {
                description(experiment.description)
            }
            final Set<ResultContextItem> resultContextItems = experiment.resultContextItems
            if (resultContextItems) {
                resultExportService.generateResultContextItems(markupBuilder, resultContextItems)
            }
            final Set<ProjectExperiment> projectExperiments = experiment.projectExperiments
            if (projectExperiments) {
                generateProjectExperiments(markupBuilder, projectExperiments)
            }
            final Set<ExternalReference> externalReferences = experiment.externalReferences
            if (externalReferences) {
                generateExternalReferences(markupBuilder, externalReferences)
            }
            if (experiment.laboratory) {
                laboratory(experiment.laboratory.laboratory)
            }
            generateExperimentLinks(markupBuilder, experiment)
        }
    }
    /**
     * External References to an Experiment
     */
    protected void generateExternalReferences(final MarkupBuilder markupBuilder, final Set<ExternalReference> externalReferences) {
        markupBuilder.externalReferences() {
            for (ExternalReference externalReference : externalReferences) {
                generateExternalReference(markupBuilder, externalReference)
            }
        }
    }
    /**
     * External Reference to an Experiment
     * @param markupBuilder
     * @param externalReference
     */
    protected void generateExternalReference(final MarkupBuilder markupBuilder, final ExternalReference externalReference) {

        markupBuilder.externalReference() {
            externalAssayRef(externalReference.extAssayRef) {
            }
            final ExternalSystem externalSystemReference = externalReference.externalSystem
            if (externalSystemReference) {
                externalSystem(name: externalSystemReference.systemName, owner: externalSystemReference.owner) {
                    systemUrl(externalSystemReference.systemUrl)
                }
            }
            final Project project = externalReference.project
            if (project) {
                final String projectHref = grailsLinkGenerator.link(mapping: 'project', absolute: true, params: [id: "${project.id}"]).toString()
                link(rel: 'related', href: "${projectHref}", type: "${this.mediaTypeDTO.projectMediaType}")
            }
        }
    }
    /**
     * List of @ProjectExperiment associated to a given Experiment
     * @param markupBuilder
     * @param projectExperiments
     */
    protected void generateProjectExperiments(final MarkupBuilder markupBuilder, final Set<ProjectExperiment> projectExperiments) {
        markupBuilder.projectExperiments() {
            for (ProjectExperiment projectExperiment : projectExperiments) {
                generateProjectExperiment(markupBuilder, projectExperiment)
            }
        }
    }
    /**
     * Generate projectExperiment
     *
     * @param markupBuilder
     * @param projectExperiment
     */
    protected void generateProjectExperiment(final MarkupBuilder markupBuilder, ProjectExperiment projectExperiment) {
        markupBuilder.projectExperiment() {
            if (projectExperiment.description) {
                description(projectExperiment.description)
            }
            final Experiment precedingExperimentR = projectExperiment.precedingExperiment
            if (precedingExperimentR) {
                precedingExperiment(id: precedingExperimentR.id.toString()) {
                    final String precedingExperimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: "${precedingExperimentR.id}"]).toString()
                    link(rel: 'related', href: "${precedingExperimentHref}", type: "${this.mediaTypeDTO.experimentMediaType}")
                }
            }
            final Project project = projectExperiment.project
            if (project) {
                final String projectHref = grailsLinkGenerator.link(mapping: 'project', absolute: true, params: [id: "${project.id}"]).toString()
                link(rel: 'related', href: "${projectHref}", type: "${this.mediaTypeDTO.projectMediaType}")

            }
            final Stage stage = projectExperiment.stage
            if (stage) {
                final Element element = stage.element
                if (element) {
                    final String href = grailsLinkGenerator.link(mapping: 'stage', absolute: true, params: [id: element.id]).toString()
                    link(rel: 'related', href: "${href}", type: "${this.mediaTypeDTO.stageMediaType}")
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
        final String assayHref = grailsLinkGenerator.link(mapping: 'assay', absolute: true, params: [id: experiment.assay?.id]).toString()
        markupBuilder.link(rel: 'related', title: 'Link to Assay', type: "${this.mediaTypeDTO.assayMediaType}", href: assayHref)

        //link to list of experiments
        final String experimentsHref = grailsLinkGenerator.link(mapping: 'experiments', absolute: true).toString()
        markupBuilder.link(rel: 'up', title: 'List Experiments', type: "${this.mediaTypeDTO.experimentsMediaType}", href: "${experimentsHref}")

        //link to results associated with this experiment
        final String resultsHref = grailsLinkGenerator.link(mapping: 'results', absolute: true, params: [id: experiment.id, offset: 0]).toString()
        markupBuilder.link(rel: 'related', title: 'List Related Results', type: "${this.mediaTypeDTO.resultsMediaType}", href: "${resultsHref}")

        //link to edit this experiment. You can only change the ready_for_extraction status
        final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: experiment.id]).toString()
        markupBuilder.link(rel: 'edit', title: 'Use link to edit Experiment', type: "${this.mediaTypeDTO.experimentMediaType}", href: experimentHref)
    }
}



