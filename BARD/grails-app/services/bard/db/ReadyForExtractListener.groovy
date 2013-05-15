package bard.db

import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy
import bard.db.dictionary.ElementStatus
import bard.db.dictionary.Ontology
import bard.db.dictionary.OntologyItem
import bard.db.enums.AssayStatus
import bard.db.enums.ExperimentStatus
import bard.db.enums.ProjectStatus
import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.ExperimentFile
import bard.db.experiment.ExperimentMeasure
import bard.db.project.Project
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import bard.db.project.ProjectDocument
import bard.db.project.ProjectExperiment
import bard.db.project.ProjectExperimentContext
import bard.db.project.ProjectExperimentContextItem
import bard.db.project.ProjectStep
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AssayContextMeasure
import bard.db.registration.AssayDocument
import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import org.apache.commons.logging.LogFactory
import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEventListener
import org.springframework.context.ApplicationEvent

import static org.grails.datastore.mapping.engine.event.EventType.*

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 5/14/13
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
class ReadyForExtractListener extends AbstractPersistenceEventListener {
    public ReadyForExtractListener(final Datastore datastore) {
        super(datastore)
    }

    protected void handleDirtyAssay(Assay assay ){
        if (assay.assayStatus != AssayStatus.DRAFT) {
            assay.readyForExtraction = ReadyForExtraction.READY;
        }
    }

    protected void handleDirtyProject(Project project){
        if (project.projectStatus != ProjectStatus.DRAFT) {
            project.readyForExtraction = ReadyForExtraction.READY;
        }
    }

    protected void handleDirtyExperiment(Experiment experiment) {
        if (experiment.experimentStatus != ExperimentStatus.DRAFT) {
            experiment.readyForExtraction = ReadyForExtraction.READY;
        }
    }

    protected void handleDirtyElement(Element element) {
        if (element.elementStatus != ElementStatus.Pending) {
            element.readyForExtraction = ReadyForExtraction.READY;
        }
    }

    protected void handleDirtyExternalReference(ExternalReference reference) {
        if (reference.project != null)
            handleDirtyProject(reference.project)
        if (reference.experiment != null)
            handleDirtyExperiment(reference.experiment)
    }

    public void handleDirtyObject(Object entity) {
        // these classes update assay's status
        if (entity instanceof Assay) {
            handleDirtyAssay((Assay)entity)
        } else if (entity instanceof AssayContext) {
            handleDirtyAssay(((AssayContext)entity).assay)
        } else if (entity instanceof AssayContextItem) {
            handleDirtyAssay(((AssayContextItem)entity).assayContext.assay)
        } else if (entity instanceof AssayContextMeasure) {
            handleDirtyAssay(((AssayContextMeasure)entity).assayContext.assay)
        } else if (entity instanceof AssayDocument) {
            handleDirtyAssay( ((AssayDocument)entity).assay );
        }
        // these classes update experiment's status
        else if (entity instanceof Experiment) {
            handleDirtyExperiment((Experiment)entity)
        } else if (entity instanceof ExperimentContext) {
            handleDirtyExperiment(((ExperimentContext)entity).experiment)
        } else if (entity instanceof ExperimentContextItem) {
            handleDirtyExperiment(((ExperimentContextItem)entity).context.experiment)
        } else if (entity instanceof ExperimentMeasure) {
            handleDirtyExperiment(((ExperimentMeasure)entity).experiment)
        } else if (entity instanceof ExperimentFile) {
            handleDirtyExperiment(((ExperimentFile)entity).experiment)
        }
        // these classes update project's status
        else if (entity instanceof Project) {
            handleDirtyProject((Project)entity)
        } else if (entity instanceof ProjectContext) {
            handleDirtyProject(((ProjectContext)entity).project)
        } else if (entity instanceof ProjectContextItem) {
            handleDirtyProject(((ProjectContextItem)entity).context.project)
        } else if (entity instanceof ProjectDocument) {
            handleDirtyProject(((ProjectDocument)entity).project)
        } else if (entity instanceof ExternalReference) {
            handleDirtyExternalReference((ExternalReference)entity);
        } else if (entity instanceof ExternalSystem) {
            ((ExternalSystem)entity).externalReferences.each { handleDirtyExternalReference(it) }
        } else if (entity instanceof ProjectExperiment) {
            handleDirtyProject(((ProjectExperiment)entity).project)
        } else if (entity instanceof ProjectExperimentContext) {
            handleDirtyProject(((ProjectExperimentContext)entity).projectExperiment.project)
        } else if (entity instanceof ProjectExperimentContextItem) {
            handleDirtyProject(((ProjectExperimentContextItem)entity).context.projectExperiment.project)
        } else if (entity instanceof ProjectStep) {
            ProjectStep projectStep = ((ProjectStep)entity)
            handleDirtyProject(projectStep.nextProjectExperiment.project)
            handleDirtyProject(projectStep.previousProjectExperiment.project)
        }
        // these elements
        else if (entity instanceof Element) {
            handleDirtyElement((Element)entity)
        } else if (entity instanceof ElementHierarchy) {
            ElementHierarchy relationship = ((ElementHierarchy)entity);
            handleDirtyElement(relationship.parentElement)
            handleDirtyElement(relationship.childElement)
        } else if (entity instanceof OntologyItem) {
            handleDirtyElement(((OntologyItem)entity).element)
        } else if (entity instanceof Ontology){
            ((Ontology)entity).ontologyItems.each { handleDirtyElement( it.element )}
        }
    }

    @Override
    protected void onPersistenceEvent(final AbstractPersistenceEvent event) {
        switch(event.eventType) {
            case PostInsert:
                handleDirtyObject(event.entityObject)
                break
            case PostUpdate:
                handleDirtyObject(event.entityObject)
                break;
            case PreDelete:
                handleDirtyObject(event.entityObject)
                break;
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return true
    }
}
