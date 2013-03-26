package bard.db.audit

import grails.plugin.spock.IntegrationSpec
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Unroll

import javax.sql.DataSource
import java.sql.Connection

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/25/13
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class BardContextUtilsIntegrationSpec extends IntegrationSpec {

    SessionFactory sessionFactory
    DataSource dataSource

    void "test setBardContextUsername with null session"() {

        when:
        BardContextUtils.setBardContextUsername(null as Session, "test")

        then:
        thrown(Error)
    }

    void "test setBardContextUsername with null connection"() {

        when:
        BardContextUtils.setBardContextUsername(null as Connection, "test")

        then:
        thrown(Error)
    }


    void "test session setBardContextUsername with #desc"() {
        Session session = sessionFactory.currentSession
        when:
        BardContextUtils.setBardContextUsername(session, username)

        then:
        BardContextUtils.getCurrentUsername(session) == expectedUsername

        when:
        BardContextUtils.clearBardContext(session)

        then:
        BardContextUtils.getCurrentUsername(session) == null

        where:
        desc                 | username | expectedUsername
        'null username '     | null     | null
        'non null username ' | 'test'   | 'test'
        'null username '     | null     | null
        'non null username ' | 'test'   | 'test'
    }

    void "test connection setBardContextUsername with #desc"() {
        Connection connection = dataSource.connection
        when:
        BardContextUtils.setBardContextUsername(connection, username)

        then:
        BardContextUtils.getCurrentUsername(connection) == expectedUsername

        when:
        BardContextUtils.clearBardContext(connection)

        then:
        BardContextUtils.getCurrentUsername(connection) == null

        where:
        desc                 | username | expectedUsername
        'null username '     | null     | null
        'non null username ' | 'test'   | 'test'
        'null username '     | null     | null
        'non null username ' | 'test'   | 'test'
    }

    void "test connection doWithContextUsername with #desc"() {
        Connection connection = dataSource.connection

        String usernameWithinClosure

        when:
        BardContextUtils.doWithContextUsername(connection, username) {->
            usernameWithinClosure = BardContextUtils.getCurrentUsername(connection)
        }

        then:
        usernameWithinClosure == expectedUsername

        where:
        desc                 | username | expectedUsername
        'null username '     | null     | null
        'non null username ' | 'test'   | 'test'
    }

    void "test session doWithContextUsername with #desc"() {
        Session session = sessionFactory.currentSession

        String usernameWithinClosure

        when:
        BardContextUtils.doWithContextUsername(session, username) {->
            usernameWithinClosure = BardContextUtils.getCurrentUsername(session)
        }

        then:
        usernameWithinClosure == expectedUsername

        where:
        desc                 | username | expectedUsername
        'null username '     | null     | null
        'non null username ' | 'test'   | 'test'
    }
}
