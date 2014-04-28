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
     * the (?m) is a flag to treat the input as multiline
     * the rest is lookin for lines with only a / on it , it can have whitespace on either side
     */
    String BACKSLASH_ONLY_OPTIONAL_WHITESPACE = /(?m)^\s*\/\s*$/
    /**
     * Had some difficulty get the stored procedures to pass thru liquid base so parsing on / and using groovy sql
     *
     * NOTE: for this should notice changes to the store procedures and run the change when anything is updated, so
     *       just modify the create-ontology-procedures.sql file directly, this way vcs and easily see diffs over time.
     */

    changeSet(author: 'ddurkin', id: 'pkg_auditing_setup.sql', dbms: 'oracle', context: 'standard, auditing', runOnChange: 'true') {
        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('sql-auditing/pkg_auditing_setup.sql').text
            for (String sqlBlock in text.split(BACKSLASH_ONLY_OPTIONAL_WHITESPACE)) {
                sqlBlocks.add(sqlBlock)
            }
            change {
                for (String sqlBlock in sqlBlocks) {
                    sql.call(sqlBlock)
                }
            }
            checkSum(text)
        }
    }
    changeSet(author: 'ddurkin', id: 'pkg_auditing_init.sql', dbms: 'oracle', context: 'standard, auditing', runOnChange: 'true') {
        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('sql-auditing/pkg_auditing_init.sql').text
            for (String sqlBlock in text.split(BACKSLASH_ONLY_OPTIONAL_WHITESPACE)) {
                sqlBlocks.add(sqlBlock)
            }
            change {
                for (String sqlBlock in sqlBlocks) {
                    sql.call(sqlBlock)
                }
            }
            checkSum(text)
        }
    }
    changeSet(author: 'ddurkin', id: 'pkg_auditing.sql', dbms: 'oracle', context: 'standard, auditing', runOnChange: 'true') {
        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('sql-auditing/pkg_auditing.sql').text
            for (String sqlBlock in text.split(BACKSLASH_ONLY_OPTIONAL_WHITESPACE)) {
                sqlBlocks.add(sqlBlock)
            }
            change {
                for (String sqlBlock in sqlBlocks) {
                    sql.call(sqlBlock)
                }
            }
            checkSum(text)
        }
    }

    changeSet(author: "ddurkin", id: "call auditing_setup.setup_tables();", context: 'standard, auditing', dbms: 'oracle') {
        grailsChange {
            change {
                sql.call('call auditing_setup.setup_tables()')
            }
        }
    }

    changeSet(author: "ddurkin", id: "refresh audit_settings and triggers", context: 'standard, auditing', dbms: 'oracle', runAlways: 'true') {
        grailsChange {
            change {
                sql.call('''call auditing_setup.update_settings('', 'Refresh')''')
                sql.call('''call auditing_init.make_triggers()''')
            }
        }
    }

    /**
     *  here as a developer utility
     */
    changeSet(author: "ddurkin", id: "drop audit triggers audit_settings and triggers", context: 'drop-auditing-triggers', dbms: 'oracle', runAlways: 'true') {
        grailsChange {
            change {
                String dropQueryTrigger = """SELECT 'drop trigger ' || trigger_name as dropSql FROM user_triggers WHERE TRIGGER_NAME LIKE 'ADT_%' """
                sql.eachRow(dropQueryTrigger){row ->
                    println(row.dropSql)
                    sql.executeUpdate(row.dropSql)
                }
            }
        }
    }
}
