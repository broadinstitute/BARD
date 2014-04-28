/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
    static Connection setBardContextUsername(Connection connection, String username) {
        assert connection
        CallableStatement callableStatement
        try {
            if (LOG.isDebugEnabled()) {
                getCurrentUsername(connection)
            }
            callableStatement = connection.prepareCall(SET_USERNAME_QUERY);
            callableStatement.setString(1, username);
            callableStatement.executeUpdate()
            if (LOG.isDebugEnabled()) {
                LOG.debug("set bard_context username: $username")
            }
        } catch (SQLException e) {
            LOG.error("exception when trying to call bard_context.set_username", e);
        }
        finally {
            callableStatement?.close()
        }
        connection
    }

    static void setBardContextUsername(Session session, String username) {
        assert session
        try {
            if (LOG.isDebugEnabled()) {
                getCurrentUsername(session)
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

    static def doWithContextUsername = { def sessionOrConnection, String username, Closure c ->
        setBardContextUsername(sessionOrConnection, username)
        try {
            c.call()
        }
        finally {
            clearBardContext(sessionOrConnection)
        }
    }

    static void clearBardContext(def sessionOrConnection) {
        setBardContextUsername(sessionOrConnection, null)
    }

    static String getCurrentUsername(Connection connection) {
        ResultSet rs
        String username
        try {
            rs = connection.prepareStatement(GET_USERNAME_QUERY).executeQuery()
            while (rs.next()) {
                username = rs.getString(1)
                LOG.debug("bard_context.get_username() is ${username} for connection: ${connection}")
            }
        }
        catch (SQLException e) {
            LOG.error("exception when getCurrentUsername to call bard_context.get_username", e);
        }
        finally {
            rs?.close()
        }
        username
    }

    static String getCurrentUsername(Session session) {
        String username
        try {
            List results = session.createSQLQuery(GET_USERNAME_QUERY).list()
            for (r in results) {
                username = r
                LOG.debug("bard_context.get_username() is ${username} for session: ${session}")
            }
        }
        catch (SQLException e) {
            LOG.error("exception when getCurrentUsername to call bard_context.get_username", e);
        }
        username
    }
}
