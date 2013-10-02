package bard.db.registration

import bard.ReloadResultsJob
import bard.db.experiment.AsyncResultsService
import bard.db.experiment.results.JobStatus
import grails.plugin.jesque.JesqueService
import grails.plugin.redis.RedisService
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

        service.jesqueService = jesqueService
        service.redisService = redisService

        when:
        String key = service.doReloadResultsAsync(1L)

        then:
        key == "KEY"
        1 * jedis.setex("job:KEY", _, INITIAL_STATUS)
        1 * jesqueService.enqueue("backgroundQueue", ReloadResultsJob.simpleName, ["KEY", 1]);
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
        1 * jedis.get("job:KEY") >> INITIAL_STATUS
        status.status == "Started..."
    }
}
