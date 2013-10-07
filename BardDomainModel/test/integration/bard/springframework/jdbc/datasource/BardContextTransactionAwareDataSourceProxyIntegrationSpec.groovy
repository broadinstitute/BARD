package bard.springframework.jdbc.datasource

import bard.db.audit.BardContextUtils
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Unroll

import javax.sql.DataSource
import java.sql.Connection

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/25/13
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class BardContextTransactionAwareDataSourceProxyIntegrationSpec extends IntegrationSpec {

    DataSource dataSource

    void "test BardContextTransactionAwareDataSourceProxy wired in plugin config"() {
        expect:
        dataSource instanceof BardContextTransactionAwareDataSourceProxy
    }

    void "getConnection when #desc"() {
        SecurityContextHolder.clearContext() // ensure SecurityContextHolder is clean, another test could have left a user authenticated

        given:
        springSecurityContextConfig.call()

        when:
        Connection connection = dataSource.getConnection()

        then:
        BardContextUtils.getCurrentUsername(connection) == expectedUsername

        where:
        desc                                       | springSecurityContextConfig                                         | expectedUsername
        'no username set in SpringSecurityContext' | {}                                                                  | null
        'username set in SpringSecurityContext'    | { SpringSecurityUtils.reauthenticate('integrationTestUser', null) } | 'integrationtestuser'
    }

}
