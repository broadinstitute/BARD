package bard.springframework.jdbc.datasource

import clover.org.apache.log4j.Logger
import grails.plugins.springsecurity.SpringSecurityService
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.security.core.userdetails.UserDetails

import javax.naming.OperationNotSupportedException
import javax.sql.DataSource
import java.sql.CallableStatement
import java.sql.Connection
import java.sql.ResultSet
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

    BardContextTransactionAwareDataSourceProxy(DataSource targetDataSource) {
        throw new OperationNotSupportedException("use constructor with DataSource and SpringSecurity args")
    }

    BardContextTransactionAwareDataSourceProxy(DataSource targetDataSource, SpringSecurityService springSecurityService) {
        super(targetDataSource)
        this.springSecurityService = springSecurityService
    }

    @Override
    Connection getConnection(String username, String password) throws SQLException {
        return super.getConnection(username, password)    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    Connection getConnection() throws SQLException {
        final Connection connection = super.getConnection()
        UserDetails userDetails = (UserDetails) springSecurityService?.getPrincipal();
        String username = userDetails?.getUsername() ?: 'testuser'
        if (username) {
            CallableStatement callableStatement
            try {
                ResultSet rs = connection.prepareStatement("select bard_context.get_username() from dual").executeQuery()
                while(rs.next()){
                    println(rs.getString(1))
                }
                callableStatement = connection.prepareCall("{call bard_context.set_username(?)}");
                callableStatement.setString(1, username);
                callableStatement.executeUpdate()
            } catch (SQLException e) {
                LOG.error("exception when trying to call bard_context.set_username", e);
            }
            finally {
                callableStatement?.close()
            }
        }
        connection
    }
}
