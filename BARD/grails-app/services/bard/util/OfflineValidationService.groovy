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

import bard.db.guidance.GuidanceAware
import bard.db.people.PersonService
import bard.db.registration.Assay
import groovy.sql.Sql
import org.apache.commons.lang.time.StopWatch
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.hibernate.SessionFactory
import org.hibernate.metadata.ClassMetadata

import javax.sql.DataSource
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class OfflineValidationService {

    DataSource dataSource
    GrailsApplication grailsApplication
    SessionFactory sessionFactory
    PersonService personService

    Map validate(List<String> domainClassFullNames) {
        final int numIdsSubmitted = 0
        AtomicInteger numIdsProcessed = new AtomicInteger(0)
        final ExecutorService pool = Executors.newFixedThreadPool(10)
        if (domainClassFullNames) {
            final StopWatch sw = new StopWatch()
            sw.start()
            final Sql sql = new Sql(dataSource)

            final List<GrailsDomainClass> domainClasses = grailsApplication.domainClasses.findAll {
                domainClassFullNames.contains(it.fullName)
            }
            for (GrailsDomainClass grailsDomainClass in domainClasses) {
                final Class actualDomainClass = grailsDomainClass.getClazz()
                final ClassMetadata classMetaData = sessionFactory.getClassMetadata(actualDomainClass)
                final String tableName = classMetaData.getTableName()
                final String idColumnName = classMetaData.getPropertyColumnNames('id')[0]
                final Long personId = personService.findCurrentPerson()?.id

                String query = "select ${idColumnName} as ID from ${tableName} order by ${idColumnName}"
                println(query)
                sql.eachRow(query) { row ->
                    final Long id = row.ID

                    pool.submit({
                        try {
                            boolean isValid = false
                            String errors = null
                            String guidance = null
                            Assay.withTransaction {
                                final def instance = actualDomainClass.findById(id)
                                isValid = instance?.validate()
                                errors = instance.errors.getGlobalError()
                                if (instance instanceof GuidanceAware) {
                                    guidance = instance.guidance*.message.findAll().sort().join('\n')
                                }
                            }
                            if (isValid==false || errors || guidance) {
                                sql.executeInsert("""insert into OFFLINE_VALIDATION (CLASS_NAME, DOMAIN_ID , ERRORS, GUIDANCE, INITIATED_BY)
                                            values (?,?,?,?,?)""".toString(), [actualDomainClass.simpleName, id,errors,guidance ,personId])
                            }
                        }
                        catch (Exception e) {
                            println(e)
                            e.printStackTrace(System.out)
                        }
                        numIdsProcessed.incrementAndGet()
                    });
                    numIdsSubmitted++
                }
            }
            while(numIdsProcessed.get()<numIdsSubmitted){
                sleep(1000) // sleep for a second
            }
            println("numIdsSubmitted: $numIdsSubmitted numIdsProcessed: ${numIdsProcessed.get()}" )
            pool.shutdown()
            println("pool shutdown")
            println("finished duration: ${sw} time: ${new Date()}")
        }
        [numIdsSubmitted:numIdsSubmitted]
    }

}
