package bard.util

import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import org.springframework.cache.CacheManager

import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 10/11/13
 * Time: 3:20 PM
 * There's some database tables that are build by stored procedures currently that serve as a cache of sorts for the element hierarchy.
 *
 * Additionally, we're using the grails cache plugin
 *
 * This service is to provide some methods to deal with the cache we new primary data is added that requires the caches
 * be refreshed
 */
class BardCacheUtilsService {

    final static String CONTEXT_ITEM_ATTRIBUTE_DESCRIPTORS = 'contextItemAttributeDescriptors'
    final static String CONTEXT_ITEM_VALUE_DESCRIPTORS = 'contextItemValueDescriptors'

    CacheManager grailsCacheManager
    OntologyDataAccessService ontologyDataAccessService
    ExecutorService executorService = Executors.newCachedThreadPool()

    /**
     * when a user adds a new dictionary term this method should refresh or clear all the relevant caches
     */
    public void refreshDueToNewDictionaryEntry() {
        rebuildTreeTables()
        clearGrailsCacheRegion(CONTEXT_ITEM_VALUE_DESCRIPTORS)
        executorService.execute({ ontologyDataAccessService.computeTrees(true) })

    }

    /**
     * when a user adds an element or update a part of the ontology
     *
     * the main difference here is that anything that cached the ontology structure needs refreshing
     */
    public void refreshDueToNonDictionaryEntry() {
        clearGrailsCacheRegion(CONTEXT_ITEM_ATTRIBUTE_DESCRIPTORS)
        refreshDueToNewDictionaryEntry()
    }

    /**
     * calls the make_trees stored procedure which populates the _Tree tables and is a convenient lookup for hierarchy info
     */
    public void rebuildTreeTables() {
        Element.withSession { session ->
            session.createSQLQuery("""BEGIN Manage_Ontology.make_trees(); END;""").executeUpdate()
        }
    }

    /**
     * Clear a grails cache region by name
     * @param name
     */
    public void clearGrailsCacheRegion(String name) {
        grailsCacheManager.getCache(name)?.clear()
    }

}
