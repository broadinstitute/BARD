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
