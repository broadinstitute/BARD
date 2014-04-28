/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
