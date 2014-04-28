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

package bard.db.registration

import bard.ReloadResultsJob
import bard.db.experiment.AsyncResultsService
import bard.db.experiment.results.ImportSummary
import bard.db.experiment.results.JobStatus
import grails.plugin.jesque.JesqueService
import grails.plugin.redis.RedisService
import grails.plugins.springsecurity.SpringSecurityService
import redis.clients.jedis.Jedis
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 9/30/13
 * Time: 9:38 PM
 * To change this template use File | Settings | File Templates.
 */
class AsyncResultsServiceUnitSpec extends Specification {
    final static String INITIAL_STATUS = "{\"status\":\"Started...\",\"summary\":null,\"finished\":false}";

    def "test doReloadResultsAsync"() {
        AsyncResultsService service = new AsyncResultsService();

        Jedis jedis = Mock(Jedis)
        RedisService redisService = Stub(RedisService) {
            withRedis(_) >> { fn ->
                fn[0](jedis)
            }
        }
        JesqueService jesqueService= Mock(JesqueService)
        SpringSecurityService springSecurityService = Mock (SpringSecurityService)

        service.springSecurityService = springSecurityService
        service.jesqueService = jesqueService
        service.redisService = redisService

        when:
        String key = service.doReloadResultsAsync(1L, "KEY", "url")

        then:
        key == "KEY"
        1 * jedis.setex("result-job:KEY", _, INITIAL_STATUS)
        1 * jedis.hset("user-jobs:username", "result-job:KEY", "url")
        1 * jesqueService.enqueue("backgroundQueue", ReloadResultsJob.simpleName, ["username", "KEY", 1]);
        1 * springSecurityService.getPrincipal() >> [username: "username"]
    }

    def "test getStatus"() {
        AsyncResultsService service = new AsyncResultsService()
        Jedis jedis = Mock(Jedis)
        RedisService redisService = Stub(RedisService) {
            withRedis(_) >> { fn ->
                fn[0](jedis)
            }
        }
        service.redisService = redisService

        when:
        JobStatus status = service.getStatus("KEY")

        then:
        1 * jedis.get("result-job:KEY") >> INITIAL_STATUS
        status.status == "Started..."
    }

//    List<String> errors = []
//
//    // these are just collected for purposes of reporting the import summary at the end
//    int linesParsed = 0;
//    int resultsCreated = 0;
//    int experimentAnnotationsCreated = 0;
//    Map<String, Integer> resultsPerLabel = [:]
//    int substanceCount;
//
//    int resultsWithRelationships = 0;
//    int resultAnnotations = 0;
//
//    List<List<String>> topLines = []

    def "test job status serialization"() {
        setup:
        JobStatus status = new JobStatus(status: "status",
                summary: new ImportSummary(
                        errors: ["x"],
                        linesParsed: 1,
                        resultsCreated: 2,
                        experimentAnnotationsCreated: 3,
                        resultsPerLabel: ["result":4],
                        substanceCount: 5,
                        resultsWithRelationships: 6,
                        resultAnnotations: 7,
                        topLines: [["a"],["b","c"]] ),
                finished: true)


        AsyncResultsService service = new AsyncResultsService()

        when:
        String json = service.asString(status)
        JobStatus status2 = service.fromString(json)

        then:
        status2.status == status.status
        status2.summary.errors == status.summary.errors
        status2.summary.linesParsed == status.summary.linesParsed
        status2.summary.resultsCreated == status.summary.resultsCreated
        status2.summary.experimentAnnotationsCreated == status.summary.experimentAnnotationsCreated
        status2.summary.resultsPerLabel == status.summary.resultsPerLabel
        status2.summary.substanceCount == status.summary.substanceCount
        status2.summary.resultsWithRelationships == status.summary.resultsWithRelationships
        status2.summary.resultAnnotations == status.summary.resultAnnotations
        status2.summary.topLines == status.summary.topLines
        status2.finished == status.finished
        status2.finished
    }
}
