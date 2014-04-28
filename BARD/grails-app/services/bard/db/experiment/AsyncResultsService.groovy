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

package bard.db.experiment

import bard.db.experiment.results.ImportSummary
import bard.db.experiment.results.JobStatus
import com.fasterxml.jackson.databind.ObjectMapper
import grails.plugin.jesque.JesqueService
import grails.plugin.redis.RedisService
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import redis.clients.jedis.Jedis
import bard.ReloadResultsJob

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 9/30/13
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
class AsyncResultsService {
    // TODO Add security on urls for jesque-web plugin
    // TODO Add total # of lines
    // Set up to run workers on bigbard

    RedisService redisService
    JesqueService jesqueService
    SpringSecurityService springSecurityService

    ObjectMapper mapper = new ObjectMapper()

    // consider moving this state information into DB?  We could get rid of the timeout if we did that and
    // rely on redis less.
    int timeoutInSeconds = 60 * 60 * 24
    static String jobKeyPrefix = "result-job:"
    // each user job is a key with the job id and the value is the url to go to
    static String jobByUserKeyPrefix = "user-jobs:"

    public String createJobKey() {
        return UUID.randomUUID().toString()
    }

    public String createJob(String username, String jobKey, String link) {
        updateStatus(jobKey, "Started...")
        redisService.withRedis { Jedis jedis ->
            jedis.hset(jobByUserKeyPrefix+username, jobKeyPrefix+jobKey, link)
        }
        return jobKey
    }

    public List updateAndGetJobs(String username = null) {
        if(username == null) {
            username = springSecurityService.getPrincipal()?.username
        }

        List result = [];
        redisService.withRedis { Jedis jedis ->
            String userKey = jobByUserKeyPrefix+username
            Map<String,String> unfiltered = jedis.hgetAll(userKey)
            // some keys may have expired since this was created so make sure each key exists
            // and remove it from the hash if it doesn't exist
            List<String> expired = []
            unfiltered.each{ k, v ->
                String jobKey = k.substring(jobKeyPrefix.length())
                JobStatus status = getStatus(jobKey)
                if ( status == null ) {
                    expired << k
                }
                else {
                    result << [key: jobKey, link:v, status: status.status]
                }
            }

            if(expired.size() > 0) {
                jedis.hdel(userKey, expired.toArray(new String[0]))
            }
        }

        return result
    }

    String asString(JobStatus status) {
        return mapper.writeValueAsString(status)
    }

    JobStatus fromString(String json) {
        return mapper.readValue(json, JobStatus.class)
    }

    public void updateStatus(String jobKey, String status) {
        redisService.withRedis { Jedis jedis ->
            jedis.setex( (jobKeyPrefix+jobKey), timeoutInSeconds, asString(new JobStatus(status: status)))
        }
    }

    public void updateFinished(String jobKey, String status) {
        redisService.withRedis { Jedis jedis ->
            jedis.setex( (jobKeyPrefix+jobKey), timeoutInSeconds, asString(new JobStatus(status: status, finished: true)))
        }
    }

    public void updateResult(String jobKey, ImportSummary summary) {
        redisService.withRedis { Jedis jedis ->
            jedis.setex( (jobKeyPrefix+jobKey), timeoutInSeconds, asString(new JobStatus(summary: summary, status: "Successfully loaded ${summary.resultsCreated} results", finished: true)))
        }
    }

    public JobStatus getStatus(String jobKey) {
        JobStatus status = null;

        redisService.withRedis { Jedis jedis ->
            String json = jedis.get((jobKeyPrefix + jobKey))
            if(json != null) {
                status = fromString(json)
            }
        }

        return status;
    }

    public String doReloadResultsAsync(Long id, String jobKey, String link) {
        String username = springSecurityService.getPrincipal()?.username

        createJob(username, jobKey, link)

        jesqueService.enqueue("backgroundQueue", ReloadResultsJob.simpleName, username, jobKey, id)

        return jobKey
    }
}
