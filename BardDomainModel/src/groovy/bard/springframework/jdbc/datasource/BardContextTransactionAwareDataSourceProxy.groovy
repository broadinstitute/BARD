package bard.springframework.jdbc.datasource

import bard.db.audit.BardContextUtils
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy

import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/20/13
 * Time: 11:51 PM
 * A Hook to execute a stored procedure prior to any connection from the dataSource being used that sets the username
 * of the user that's been authenticated with SpringSecurity.
 *
 * A username is required for auditing that occurs on the database for some tables
 *
 */
class BardContextTransactionAwareDataSourceProxy extends TransactionAwareDataSourceProxy {

    private static Logger LOG = Logger.getLogger(this.getClass())

    private GrailsApplication grailsApplication

    BardContextTransactionAwareDataSourceProxy() {
        throw new UnsupportedOperationException("use constructor with DataSource and grailsApplication")
    }

    /**
     * Without a SpringSecurityService the username will always be set to null for all connections
     * @param targetDataSource
     */
    BardContextTransactionAwareDataSourceProxy(DataSource targetDataSource) {
        throw new UnsupportedOperationException("use constructor with DataSource and grailsApplication")
    }

    /**
     *
     * @param targetDataSource
     * @param grailsApplication
     */
    BardContextTransactionAwareDataSourceProxy(DataSource targetDataSource, GrailsApplication grailsApplication) {
        super(targetDataSource)
        this.grailsApplication = grailsApplication
    }

    @Override
    Connection getConnection(String username, String password) throws SQLException {
        return setOrClearUsername(super.getConnection(username, password))
    }

    @Override
    Connection getConnection() throws SQLException {
        setOrClearUsername(super.getConnection())
    }

    /**
     * Tries to get the user form the springSecurityService
     *
     * if either the springSecurityService or  there isn't anyone authenticated, then the username is set to null
     * and null is set as the username in the bard_context
     *
     * Note this doesn't require an app use or have a compile time dependency on the grails-spring-security-core plugin
     * but will use it if it's present
     *
     * @param connection
     * @return returns the connection passed in so it can be chained
     */
    private Connection setOrClearUsername(Connection connection) {
        def userDetails = grailsApplication?.mainContext?.springSecurityService?.getPrincipal();
        String username = userDetails?.getUsername()
        BardContextUtils.setBardContextUsername(connection, username)
    }


}
