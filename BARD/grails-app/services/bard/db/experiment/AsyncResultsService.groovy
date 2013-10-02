package bard.db.experiment

import bard.db.experiment.results.ImportSummary
import bard.db.experiment.results.JobStatus
import com.fasterxml.jackson.databind.ObjectMapper
import grails.plugin.jesque.JesqueService
import grails.plugin.redis.RedisService
import grails.plugins.springsecurity.SpringSecurityService
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
    // TODO Ensure that authentication is cleared on worker thread when job is done:  Ask Dan.  He knows.
    // TODO Add exception handling to worker
    // TODO Add security on urls for jesque-web plugin
    // TODO figure out configuration issues
    // TODO Add total # of lines
    // TODO Add My Jobs page
    // Set up to run workers on bigbard

    RedisService redisService
    JesqueService jesqueService
    SpringSecurityService springSecurityService

    ObjectMapper mapper = new ObjectMapper()

    int timeoutInSeconds = 60 * 60 * 24
    static String jobKeyPrefix = "job:"

    public String createJobKey() {
        return UUID.randomUUID().toString()
    }

    public String createJob() {
        String jobKey = createJobKey();
        updateStatus(jobKey, "Started...")
        return jobKey
    }

    String asString(JobStatus status) {
        return mapper.writeValueAsString(status)
    }

    public void updateStatus(String jobKey, String status) {
        redisService.withRedis { Jedis jedis ->
            jedis.setex( (jobKeyPrefix+jobKey), timeoutInSeconds, asString(new JobStatus(status: status)))
        }
    }

    public void updateResult(String jobKey, ImportSummary summary) {
        redisService.withRedis { Jedis jedis ->
            jedis.setex( (jobKeyPrefix+jobKey), timeoutInSeconds, asString(new JobStatus(summary: summary)))
        }
    }

    public JobStatus getStatus(String jobKey) {
        JobStatus status;

        redisService.withRedis { Jedis jedis ->
            String json = jedis.get((jobKeyPrefix + jobKey))
            status = mapper.readValue(json, JobStatus.class)
        }

        return status;
    }

    public String doReloadResultsAsync(Long id) {
        String jobKey = createJob()

        String username = springSecurityService.getPrincipal()?.username

        jesqueService.enqueue("backgroundQueue", ReloadResultsJob.simpleName, username, jobKey, id)

        return jobKey
    }
}
