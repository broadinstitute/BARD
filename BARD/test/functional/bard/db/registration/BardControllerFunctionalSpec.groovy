package bard.db.registration

import grails.plugin.remotecontrol.RemoteControl
import grails.util.BuildSettingsHolder
import org.apache.commons.lang.StringUtils
import spock.lang.Specification
import spock.lang.Unroll
import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 7/2/13
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 *
 *  TEAM_A_1 and TEAM_A_2 belong to the same group
 *
 *  TEAM_B_1 belong to a different group
 *
 *  TEAM_A_1 also has ROLE_CURATOR
 *
 *
 *
 */
@Unroll
abstract class BardControllerFunctionalSpec extends Specification {


    static RemoteControl remote = new RemoteControl()

    static final String TEAM_A_1_USERNAME = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_1.username }
    static final String TEAM_A_1_PASSWORD = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_1.password }

    static final String TEAM_A_2_USERNAME = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_2.username }
    static final String TEAM_A_2_PASSWORD = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_2.password }

    static final String TEAM_B_1_USERNAME = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamB_1.username }
    static final String TEAM_B_1_PASSWORD = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamB_1.password }


    static final String ADMIN_USERNAME = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.integrationTestUser.username }
    static final String ADMIN_PASSWORD = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.integrationTestUser.password }
    static final String ADMIN_EMAIL = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.integrationTestUser.email }
    static final String ADMIN_ROLE = 'ROLE_BARD_ADMINISTRATOR'


    static final String CURATOR_USERNAME = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.curator.username }
    static final String CURATOR_PASSWORD = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.curator.password }


    static final String dburl = remote { ctx.grailsApplication.config.dataSource.url }


    static final String driverClassName = remote { ctx.grailsApplication.config.dataSource.driverClassName }

    static final String dbusername = remote { ctx.grailsApplication.config.dataSource.username }

    static final String dbpassword = remote { ctx.grailsApplication.config.dataSource.password }

    static String getBaseUrl() {
        return BuildSettingsHolder.settings?.functionalTestBaseUrl
    }

    static RESTClient getRestClient(String baseUrl, String action, String team, String teamPassword) {

        String url = baseUrl + action
        if (!action) {
            url = StringUtils.removeEnd(baseUrl, "/")
        }
        RESTClient client = new RESTClient(url)
        client.authorization = new HTTPBasicAuthorization(team, teamPassword)
        client.httpClient.sslTrustAllCerts = true
        client.httpClient.followRedirects = false
        return client
    }
}
