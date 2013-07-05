package bard.db.registration

import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import grails.plugin.remotecontrol.RemoteControl
import groovy.sql.Sql
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import wslite.http.auth.HTTPBasicAuthorization
import wslite.json.JSONArray
import wslite.rest.RESTClient
import wslite.rest.Response

import javax.servlet.http.HttpServletResponse

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
    //TODO:Following should come from an external properties file
    static RemoteControl remote = new RemoteControl()

    //TODO:Following should come from an external properties file


    static final String TEAM_A_1_USERNAME = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_1.username }
    static final String TEAM_A_1_EMAIL = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_1.email }
    static final String TEAM_A_1_ROLE = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_1.roles.get(0) }
    static final String TEAM_A_1_PASSWORD = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_1.password }

    static final String TEAM_A_2_EMAIL = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_2.email }
    static final String TEAM_A_2_ROLE = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_2.roles.get(0) }
    static final String TEAM_A_2_USERNAME = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_2.username }
    static final String TEAM_A_2_PASSWORD = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamA_2.password }

    static final String TEAM_B_1_USERNAME = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamB_1.username }
    static final String TEAM_B_1_PASSWORD = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamB_1.password }
    static final String TEAM_B_1_EMAIL = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamB_1.email }
    static final String TEAM_B_1_ROLE = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.teamB_1.roles.get(0) }


    static final String ADMIN_USERNAME = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.integrationTestUser.username }
    static final String ADMIN_PASSWORD = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.integrationTestUser.password }
    static final String ADMIN_EMAIL = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.integrationTestUser.email }
    static final String ADMIN_ROLE = 'ROLE_BARD_ADMINISTRATOR'


    static final String CURATOR_USERNAME = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.curator.username }
    static final String CURATOR_PASSWORD = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.curator.password }
    static final String CURATOR_EMAIL = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.curator.email }
    static final String CURATOR_ROLE = 'ROLE_CURATOR'



    static final String dburl = remote { ctx.grailsApplication.config.dataSource.url }


    static final String driverClassName = remote { ctx.grailsApplication.config.dataSource.driverClassName }

    static final String dbusername = remote { ctx.grailsApplication.config.dataSource.username }

    static final String dbpassword = remote { ctx.grailsApplication.config.dataSource.password }



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
    static void createTeamsInDatabase(String teamuserName, String teamEmail, String teamRole, String reAuthenticateWith) {
        remote.exec({
            SpringSecurityUtils.reauthenticate(reAuthenticateWith, null)
            Person person = Person.findByUserName(teamuserName)
            Role role = Role.findByAuthority(teamRole)
            if (!role) {
                role = Role.build(authority: teamRole).save(flush: true)
            }
            if (!person) {
                person = Person.build(userName: teamuserName, emailAddress: teamEmail,
                        dateCreated: new Date(), newObjectRole: role).save(flush: true)
            }
            PersonRole personRole = PersonRole.findByPersonAndRole(person, role)
            if (!personRole) {
                PersonRole.build(role: role, person: person).save(flush: true)
            }
            return true
        })

    }
}
