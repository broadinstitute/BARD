package bard.db

import bard.db.audit.BardContextUtils
import grails.plugin.spock.IntegrationSpec
import org.hibernate.SessionFactory
import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/24/13
 * Time: 12:43 PM
 *
 * With auditing enabled, the auditing triggers require as username set in the bard context.
 *
 * All classes that subclass this class will have the username set before each test
 */
abstract class BardIntegrationSpec extends IntegrationSpec{

    SessionFactory sessionFactory

    @Before
    void setUsernameInBardContext(){
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'test')
    }
}
