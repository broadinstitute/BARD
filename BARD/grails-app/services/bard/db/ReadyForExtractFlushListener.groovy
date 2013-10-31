package bard.db

import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy
import bard.db.dictionary.Ontology
import bard.db.dictionary.OntologyItem
import bard.db.enums.AssayStatus as AS
import bard.db.enums.ExperimentStatus as ES
import bard.db.enums.ProjectStatus as PS
import bard.db.enums.ReadyForExtraction
import bard.db.experiment.*
import bard.db.project.*
import bard.db.registration.*
import org.hibernate.HibernateException
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.event.*

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 6/11/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
class ReadyForExtractFlushListener implements FlushEventListener, PostInsertEventListener, PostUpdateEventListener, PostCollectionRecreateEventListener, PostCollectionUpdateEventListener {
    private WeakHashMap<EventSource, Collection> dirtyObjectsPerSession = new WeakHashMap()
    SessionFactory sessionFactory;
    private ThreadLocal<Boolean> isInExtraFlush = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return Boolean.FALSE
        }
    }

    /*
        Called after flush completes and may in turn generate an extra flush after changing the ready for extraction flag
     */

    void onFlush(FlushEvent flushEvent) throws HibernateException {
        assert isInExtraFlush != null

        Session source = flushEvent.session

        Collection dirty
        synchronized (this) {
            dirty = dirtyObjectsPerSession.remove(source)
            if (dirty == null) {
                dirty = []
            }
        }

        boolean needExtraFlush = false;
        Collection dirtyExtractables = dirty.collectMany(new HashSet()) { getObjectsImpactedByChange(it) }
        for (entity in dirtyExtractables) {
            if ((entity instanceof Project && entity.projectStatus?.getId() != PS.DRAFT_ID) ||
                    (entity instanceof Assay && entity.assayStatus?.getId() != AS.DRAFT_ID) ||
                    (entity instanceof Experiment && entity.experimentStatus?.getId() != ES.DRAFT_ID) ||
                    (entity instanceof Element)) {
                if (entity.readyForExtraction != ReadyForExtraction.READY) {
                    if (!entity.disableUpdateReadyForExtraction) {
                        entity.readyForExtraction = ReadyForExtraction.READY
                        needExtraFlush = true
                    }
                }
            }
        }

        if (needExtraFlush) {
            if (isInExtraFlush.get()) {
                throw new RuntimeException("Internal error: flushing appears to be stuck in a cycle");
            }
            isInExtraFlush.set(Boolean.TRUE)
            source.flush()
            isInExtraFlush.set(Boolean.FALSE)
        }
    }

    void markOwnerDirty(EventSource session, Object entity) {
        Collection dirty
        synchronized (this) {
            dirty = dirtyObjectsPerSession.get(session)
            if (dirty == null) {
                dirty = []
                dirtyObjectsPerSession[session] = dirty
            }
        }
        dirty.add(entity)
    }

    void onPostRecreateCollection(PostCollectionRecreateEvent postCollectionRecreateEvent) {
        if (postCollectionRecreateEvent.affectedOwnerOrNull != null)
            markOwnerDirty(postCollectionRecreateEvent.session, postCollectionRecreateEvent.affectedOwnerOrNull)
    }

    void onPostUpdateCollection(PostCollectionUpdateEvent postCollectionUpdateEvent) {
        if (postCollectionUpdateEvent.affectedOwnerOrNull != null)
            markOwnerDirty(postCollectionUpdateEvent.session, postCollectionUpdateEvent.affectedOwnerOrNull)
    }

    void onPostInsert(PostInsertEvent postInsertEvent) {
        markOwnerDirty(postInsertEvent.session, postInsertEvent.entity)
    }

    void onPostUpdate(PostUpdateEvent postUpdateEvent) {
        markOwnerDirty(postUpdateEvent.session, postUpdateEvent.entity)
    }

    public static Collection getObjectsImpactedByChange(Object entity) {
        if (entity instanceof Element) {
            return [(Element) entity]
        } else if (entity instanceof ElementHierarchy) {
            ElementHierarchy relationship = ((ElementHierarchy) entity);
            return [relationship.parentElement, relationship.childElement]
        } else if (entity instanceof OntologyItem) {
            return [((OntologyItem) entity).element]
        } else if (entity instanceof Ontology) {
            return new ArrayList(((Ontology) entity).ontologyItems.collect { it.element })
        } else {
            Object owningEntity = getOwningObject(entity)
            if (owningEntity != null)
                return [owningEntity]
            else
                return []
        }
    }

    static protected Collection handleDirtyExternalReference(ExternalReference reference) {
        if (reference.project != null)
            return [(reference.project)]

        if (reference.experiment != null)
            return [(reference.experiment)]

        return []
    }


    public static Object getOwningObject(Object entity) {
        // these classes update assay's status
        if (entity instanceof Assay) {
            return (Assay) entity
        } else if (entity instanceof AssayContext) {
            return ((AssayContext) entity).assay
        } else if (entity instanceof AssayContextItem) {
            return (((AssayContextItem) entity).assayContext.assay)
        } else if (entity instanceof AssayDocument) {
            return (((AssayDocument) entity).assay)
        }
        //these update Panel status
        else if (entity instanceof Panel) {
            return (Panel) entity
        } else if (entity instanceof PanelAssay) {
            return ((PanelAssay) entity).panel
        }
        // these classes update experiment's status
        else if (entity instanceof Experiment) {
            return ((Experiment) entity)
        } else if (entity instanceof ExperimentContext) {
            return (((ExperimentContext) entity).experiment)
        } else if (entity instanceof ExperimentContextItem) {
            return (((ExperimentContextItem) entity).context.experiment)
        } else if (entity instanceof ExperimentMeasure) {
            return (((ExperimentMeasure) entity).experiment)
        } else if (entity instanceof ExperimentFile) {
            return (((ExperimentFile) entity).experiment)
        }
        // these classes update project's status
        else if (entity instanceof Project) {
            return ((Project) entity)
        } else if (entity instanceof ProjectContext) {
            return (((ProjectContext) entity).project)
        } else if (entity instanceof ProjectContextItem) {
            return (((ProjectContextItem) entity).context.project)
        } else if (entity instanceof ProjectDocument) {
            return (((ProjectDocument) entity).project)
        } else if (entity instanceof ExternalReference) {
            return handleDirtyExternalReference(((ExternalReference) entity)).first()
        } else if (entity instanceof ExternalSystem) {
            return expectUniqueValue(((ExternalSystem) entity).externalReferences.collectMany { handleDirtyExternalReference(it) })
        } else if (entity instanceof ProjectExperiment) {
            return (((ProjectExperiment) entity).project)
        } else if (entity instanceof ProjectExperimentContext) {
            return (((ProjectExperimentContext) entity).projectExperiment.project)
        } else if (entity instanceof ProjectExperimentContextItem) {
            return (((ProjectExperimentContextItem) entity).context.projectExperiment.project)
        } else if (entity instanceof ProjectStep) {
            ProjectStep projectStep = ((ProjectStep) entity)
            return expectUniqueValue([(projectStep.nextProjectExperiment.project),
                    (projectStep.previousProjectExperiment.project)])
        }

        return null
    }

    private static Object expectUniqueValue(Collection values) {
        Set valueSet = new HashSet(values)
        assert (valueSet.size() == 1)
        return valueSet.first()
    }
}
