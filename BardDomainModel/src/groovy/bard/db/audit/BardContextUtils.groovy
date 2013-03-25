package bard.db.audit

import org.apache.log4j.Logger
import org.hibernate.HibernateException
import org.hibernate.Query
import org.hibernate.Session

import java.sql.CallableStatement
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/22/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
class BardContextUtils {

    private static Logger LOG = Logger.getLogger(this)
    private static final String SET_USERNAME_QUERY = "{call bard_context.set_username(?)}"
    private static final String GET_USERNAME_QUERY = "select bard_context.get_username() from dual"

    private static final String SQL_EXCEPTION_MSG = "exception when trying to call bard_context.set_username"

    /**
     *
     * @param connection
     * @param username to set in the bard_context for this connection, null is fine as it will just set the username in
     * the bard_context to null
     * @return the connection passed in to allow chaining
     */
    public static Connection setBardContextUsername(Connection connection, String username) {
        CallableStatement callableStatement
        try {
            if (LOG.isDebugEnabled()) {
                logExistingUsername(connection, username)
            }
            callableStatement = connection.prepareCall(SET_USERNAME_QUERY);
            callableStatement.setString(1, username);
            callableStatement.executeUpdate()
        } catch (SQLException e) {
            LOG.error("exception when trying to call bard_context.set_username", e);
        }
        finally {
            callableStatement?.close()
        }
        connection
    }

    public static void setBardContextUsername(Session session, String username) {
        try {
            if (LOG.isDebugEnabled()) {
                logExistingUsername(session, username)
            }
            Query query = session.createSQLQuery("{call bard_context.set_username(:username)}");
            query.setString('username', username);
            query.executeUpdate()
            if (LOG.isDebugEnabled()) {
                LOG.debug("set bard_context username: $username")
            }
        } catch (HibernateException e) {
            LOG.warn(SQL_EXCEPTION_MSG, e);
            println(SQL_EXCEPTION_MSG)
        }


    }

    private static logExistingUsername(Connection connection, String username) {
        ResultSet rs
        try {
            rs = connection.prepareStatement(GET_USERNAME_QUERY).executeQuery()
            while (rs.next()) {
                LOG.debug("bard_context.get_username() was ${rs.getString(1)} being set to ${username}")
            }
        }
        catch (SQLException e) {
            LOG.error("exception when logExistingUsername to call bard_context.get_username", e);
        }
        finally {
            rs?.close()
        }
    }

    private static logExistingUsername(Session session, String username) {
        try {
            List results = session.createSQLQuery(GET_USERNAME_QUERY).list()
            for (r in results) {
                LOG.debug("bard_context.get_username() was ${r} being set to ${username}")
            }
        }
        catch (SQLException e) {
            LOG.error("exception when logExistingUsername to call bard_context.get_username", e);
        }
    }
}
