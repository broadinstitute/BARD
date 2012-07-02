package dataexport.experiment

//import groovy.sql.Sql

//import org.codehaus.groovy.grails.commons.GrailsApplication


import bard.db.dictionary.Element
import bard.db.dictionary.Stage
import bard.db.experiment.Experiment
import bard.db.experiment.Project
import bard.db.experiment.ProjectExperiment
import bard.db.experiment.ResultContextItem
import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import dataexport.registration.MediaTypesDTO
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

/**
 * Class that generates Experiments as XML
 *
 * We use MarkUpBuilder, but we can switch to StreamingMarkupBuilder
 * or even StAX if we run into memory issues
 * Right now, i have not seen any memory issues yet, but then again, we do not have many real experiment documents
 * I have tested with what i think should be a full experiment, using 100K records and I did not encounter any issues
 */
class ExperimentExportService {

    LinkGenerator grailsLinkGenerator
    final MediaTypesDTO mediaTypesDTO
    final int maxExperimentsRecordsPerPage
    /**
     * See resources.groovy for wiring of this method
     * @param mediaTypesDTO
     * @param maxExperimentsRecordsPerPage - The number of experiments that can fit on a page
     */
    public ExperimentExportService(final MediaTypesDTO mediaTypesDTO,
                                   final int maxExperimentsRecordsPerPage) {
        this.mediaTypesDTO = mediaTypesDTO
        this.maxExperimentsRecordsPerPage = maxExperimentsRecordsPerPage
    }

    /**
     *  start is used for paging, it tells us where we are in the paging process
     * @param markupBuilder
     * @param start
     */
    public void generateExperiments(final MarkupBuilder markupBuilder, final int start) {
        int end = this.maxExperimentsRecordsPerPage
        boolean moreExperiments = false
        List<Experiment> experiments = Experiment.findAllByReadyForExtraction('Ready')
        final int numberOfExperiments = experiments.size()
        if (numberOfExperiments > this.maxExperimentsRecordsPerPage) {
            moreExperiments = true
        } else {
            end = numberOfExperiments
        }
        experiments = experiments.subList(start, end)

        markupBuilder.experiments(count: numberOfExperiments) {
            for (Experiment experiment : experiments) {
                final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: experiment.id]).toString()
                link(rel: 'related', type: "${this.mediaTypesDTO.experimentMediaType}", href: "${experimentHref}")
            }
            //if there are more records that can fit on a page then add the next link, with the start parameter
            //being the end variable in this method
            if (moreExperiments) {
                final String experimentsHref = grailsLinkGenerator.link(mapping: 'experiments', absolute: true, params: [start: "${end}"]).toString()
                link(rel: 'next', title: 'List Experiments', type: "${this.mediaTypesDTO.experimentsMediaType}", href: "${experimentsHref}")
            }
        }
    }
    /**
     * @param markupBuilder
     * @param experimentId
     */
    public void generateExperiment(final MarkupBuilder markupBuilder, final Long experimentId) {
        final Experiment experiment = Experiment.get(experimentId)
        if (!experiment) {
            throw new NotFoundException("Could not find Experiment with Id ${experimentId}")
        }
        this.generateExperiment(markupBuilder, experiment)
    }

    protected Map<String, String> generateAttributesForExperiment(final Experiment experiment) {
        Map<String, String> attributes = [:]

        attributes.put("experimentId", experiment.id?.toString())
        attributes.put('experimentName', experiment.experimentName)
        attributes.put('status', experiment.experimentStatus)

        if (experiment.holdUntilDate) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(experiment.holdUntilDate);
            final XMLGregorianCalendar holdUntilDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            attributes.put('holdUntilDate', holdUntilDate.toString())
        }
        if (experiment.runDateFrom) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(experiment.runDateFrom);
            final XMLGregorianCalendar runDateFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            attributes.put('runDateFrom', runDateFrom.toString())
        }
        if (experiment.runDateTo) {
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
     */
    protected void generateExperiment(final MarkupBuilder markupBuilder, final Experiment experiment) {
        final Map<String, String> attributes = generateAttributesForExperiment(experiment)

        markupBuilder.experiment(attributes) {
            if (experiment.description) {
                description(experiment.description)
            }
            final Set<ResultContextItem> resultContextItems = experiment.resultContextItems
            if (resultContextItems) {
                generateResultContextItems(markupBuilder, resultContextItems)
            }
            final Set<ProjectExperiment> projectExperiments = experiment.projectExperiments
            if (projectExperiments) {
                generateProjectExperiments(markupBuilder, projectExperiments)
            }
            final Set<ExternalReference> externalReferences = experiment.externalReferences
            if (externalReferences) {
                generateExternalReferences(markupBuilder, externalReferences)
            }
            generateExperimentLinks(markupBuilder, experiment)
        }
    }

    protected void generateExternalReferences(final MarkupBuilder markupBuilder, final Set<ExternalReference> externalReferences) {
        markupBuilder.externalReferences() {
            for (ExternalReference externalReference : externalReferences) {
                generateExternalReference(markupBuilder, externalReference)
            }
        }
    }
    /**
     *
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
                link(rel: 'related', href: "${projectHref}", type: "${this.mediaTypesDTO.projectMediaType}")
            }
        }
    }
    /**
     *
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

    protected void generateProjectExperiment(final MarkupBuilder markupBuilder, ProjectExperiment projectExperiment) {
        markupBuilder.projectExperiment() {
            if (projectExperiment.description) {
                description(projectExperiment.description)
            }
            final Experiment precedingExperimentR = projectExperiment.precedingExperiment
            if (precedingExperimentR) {
                precedingExperiment(id: precedingExperimentR.id.toString()) {
                    final String precedingExperimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: "${precedingExperimentR.id}"]).toString()
                    link(rel: 'related', href: "${precedingExperimentHref}", type: "${this.mediaTypesDTO.experimentMediaType}")
                }
            }
            final Project project = projectExperiment.project
            if (project) {
                final String projectHref = grailsLinkGenerator.link(mapping: 'project', absolute: true, params: [id: "${project.id}"]).toString()
                link(rel: 'related', href: "${projectHref}", type: "${this.mediaTypesDTO.projectMediaType}")

            }
            final Stage stage = projectExperiment.stage
            if (stage) {
                final Element element = stage.element
                if (element) {
                    final String href = grailsLinkGenerator.link(mapping: 'stage', absolute: true, params: [id: element.id]).toString()
                    link(rel: 'related', href: "${href}", type: "${this.mediaTypesDTO.stageMediaType}")
                }
            }
        }
    }
    //TODO: Move to Result Service once that is ready
    protected void generateResultContextItems(final MarkupBuilder markupBuilder, final Set<ResultContextItem> resultContextItems) {
        markupBuilder.resultContextItems() {
            for (ResultContextItem resultContextItem : resultContextItems) {
                generateResultContextItem(markupBuilder, resultContextItem)
            }
        }
    }
    //TODO: Move to Result Service once that is ready
    protected Map<String, String> generateAttributesForResultContextItem(final ResultContextItem resultContextItem) {
        Map<String, String> attributes = [:]
        attributes.put('resultContextItemId', resultContextItem.id?.toString())
        if (resultContextItem.parentGroup && resultContextItem.parentGroup.id.toString().isInteger()) {
            attributes.put('parentGroup', resultContextItem.parentGroup.id.toString())
        }

        if (resultContextItem.qualifier) {
            attributes.put('qualifier', resultContextItem.qualifier)
        }
        if (resultContextItem.valueDisplay) {
            attributes.put('valueDisplay', resultContextItem.valueDisplay)
        }
        if (resultContextItem.valueNum || resultContextItem.valueNum.toString().isInteger()) {
            attributes.put('valueNum', resultContextItem.valueNum.toString())
        }
        if (resultContextItem.valueMin || resultContextItem.valueMin.toString().isInteger()) {
            attributes.put('valueMin', resultContextItem.valueMin.toString())
        }
        if (resultContextItem.valueMax || resultContextItem.valueMax.toString().isInteger()) {
            attributes.put('valueMax', resultContextItem.valueMax.toString())
        }
        return attributes
    }
    //TODO: Move to Result Service once that is ready
    protected void generateResultContextItem(final MarkupBuilder markupBuilder, final ResultContextItem resultContextItem) {

        final Map<String, String> attributes = generateAttributesForResultContextItem(resultContextItem)

        markupBuilder.resultContextItem(attributes) {

            if (resultContextItem.attribute) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: "${resultContextItem.attribute.id}"]).toString()
                attribute(label: resultContextItem.attribute.label) {
                    link(rel: 'related', href: "${attributeHref}", type: "${this.mediaTypesDTO.elementMediaType}")
                }
            }
            if (resultContextItem.valueControlled) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: "${resultContextItem.valueControlled.id}"]).toString()

                valueControlled(label: resultContextItem.valueControlled.label) {
                    link(rel: 'related', href: "${attributeHref}", type: "${this.mediaTypesDTO.elementMediaType}")
                }
            }
            if (resultContextItem.extValueId) {
                extValueId(resultContextItem.extValueId)
            }

        }
    }
    /**
     *
     * @param markupBuilder
     * @param experiment
     */
    protected void generateExperimentLinks(final MarkupBuilder markupBuilder, final Experiment experiment) {
        //TODO: Does not exist in domain
        // experiment.laboratory

        final String assayHref = grailsLinkGenerator.link(mapping: 'assay', absolute: true, params: [id: experiment.assay?.id]).toString()
        final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: experiment.id]).toString()
        final String experimentsHref = grailsLinkGenerator.link(mapping: 'experiments', absolute: true).toString()
        final String resultsHref = grailsLinkGenerator.link(mapping: 'results', absolute: true, params: [id: experiment.id]).toString()

        markupBuilder.link(rel: 'related', title: 'Link to Assay', type: "${this.mediaTypesDTO.assayMediaType}", href: assayHref)

        markupBuilder.link(rel: 'related', title: 'List Related Results', type: "${this.mediaTypesDTO.resultsMediaType}", href: "${resultsHref}")
        markupBuilder.link(rel: 'edit', title: 'Use link to edit Experiment', type: "${this.mediaTypesDTO.experimentMediaType}", href: experimentHref)
        markupBuilder.link(rel: 'up', title: 'List Experiments', type: "${this.mediaTypesDTO.experimentsMediaType}", href: "${experimentsHref}")
    }
}



