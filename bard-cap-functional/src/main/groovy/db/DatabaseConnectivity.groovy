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

package db

import groovy.sql.Sql

import javax.sql.DataSource

import com.jolbox.bonecp.BoneCPDataSource
import common.ConfigHelper

class DatabaseConnectivity {

    private static final DataSource dataSource = getDataSource()

    public static Sql getSql() {
        Sql.newInstance(dataSource)
    }

    public static def withSql(Closure closure) {
        final Sql sql = getSql()
        try {
            return closure.call(sql)
        }
        finally {
            sql.close()
        }
    }

    private static DataSource getDataSource() {
        final Map dbInfoMap = ConfigHelper.config.dbInfoMap
        final BoneCPDataSource ds = new BoneCPDataSource()
        ds.setDriverClass(dbInfoMap.driver)
        ds.setUsername(dbInfoMap.username)
        ds.setPassword(dbInfoMap.password)
        ds.setJdbcUrl(dbInfoMap.url)
        return ds
    }

}
