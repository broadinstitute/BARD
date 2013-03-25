package bard.springframework.jdbc.datasource

import bard.db.audit.BardContextUtils
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.security.core.userdetails.UserDetails

import javax.naming.OperationNotSupportedException
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

    private SpringSecurityService springSecurityService

    BardContextTransactionAwareDataSourceProxy() {
        throw new OperationNotSupportedException("use constructor with DataSource and SpringSecurity args")
    }

    /**
     * Without a SpringSecurityService the username will always be set to null for all connections
     * @param targetDataSource
     */
    BardContextTransactionAwareDataSourceProxy(DataSource targetDataSource) {
        super(targetDataSource)
    }

    /**
     *
     * @param targetDataSource
     * @param springSecurityService
     */
    BardContextTransactionAwareDataSourceProxy(DataSource targetDataSource, SpringSecurityService springSecurityService) {
        super(targetDataSource)
        this.springSecurityService = springSecurityService
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
     * @param connection
     * @return returns the connection passed in so it can be chained
     */
    private Connection setOrClearUsername(Connection connection) {
        UserDetails userDetails = (UserDetails) springSecurityService?.getPrincipal();
        String username = userDetails?.getUsername()
        BardContextUtils.setBardContextUsername(connection, username)
    }


}
