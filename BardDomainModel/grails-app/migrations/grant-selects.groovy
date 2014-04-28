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

databaseChangeLog = {

    /**
     * script to grant select priv for all tables in a schema to all the users
     */
    changeSet(author: "ddurkin", id: "grant select", dbms: 'oracle', context:'grant-selects',runAlways: 'true') {
        grailsChange {
            change {
                /* so in an exerternalized config file specify
                 * migrations.grantSelects.schemas = ['FOO','BAR']
                 * where the schemas that should be granted select privs are enumerated
                 */
                def schemaNames = application?.config?.migrations?.grantSelects?.schemas
                String currentUsername = application?.config?.dataSource?.username

                def tablenames = sql.rows('''select table_name from user_tables order by table_name''').collect{it.TABLE_NAME}

                def result = sql.withBatch(100){stmt ->
                    for(tablename in tablenames){
                        try{
                            for (String schemaName in schemaNames) {
                                // can't grant select to yourself in oracle
                                if( schemaName.toUpperCase() != currentUsername.toUpperCase() ){
                                    String grant = "GRANT SELECT ON ${tablename} TO ${schemaName}"
//                                    println(grant)
                                    stmt.addBatch(grant)
                                }
                            }
                       }
                       catch(java.sql.SQLSyntaxErrorException e){
                           println(e.message)
                       }
                    }
                }
            }
        }
    }

}



