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

databaseHost = "DATABASE_HOST"
databaseSid = "DATABASE_SID"
dataSource {
    url = 'jdbc:oracle:thin:@${databaseHost}:1521:${databaseSid}'
    driverClassName = 'oracle.jdbc.driver.OracleDriver'
    dialect = 'bard.SequencePerTableOracleDialect'
    username = "USERNAME"
    password = "PASSWORD"
    dbCreate='no-val'
}

CbipCrowd {
    mockUsers.put('admin', [roles : ['ROLE_USER', 'ROLE_BARD_ADMINISTRATOR','ROLE_ADMIN'], username : 'admin', password : 'test', email:'test@nowhere.com'])
}

bard.services.resultService.archivePath = "RESULTS_PATH"
bard.hostname = "WEB_SERVER_HOSTNAME"

grails.serverURL = "http://${bard.hostname}:${server.port}/${appName}"
ncgc.server.root.url = "WAREHOUSE_API/v17.3"
promiscuity.badapple.url = "${ncgc.server.root.url}/plugins/badapple/prom/cid/"
grails.jesque.enabled = true
