package bard.db.registration

import bard.ReloadResultsJob
import bard.db.experiment.AsyncResultsService
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
    final static String INITIAL_STATUS = "{\"status\":\"Started...\",\"summary\":null}";

    def "test doReloadResultsAsync"() {
        AsyncResultsService service = new AsyncResultsService()  {
            String createJobKey() {
                return "KEY"
            }
        }
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
}
