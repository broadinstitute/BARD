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

package iteration_039

databaseChangeLog = {
    changeSet(author: "jasiedu", id: "iteration-039/01-modify-authority-constraint-on-role", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_039/01-modify-authority-constraint-on-role.sql", stripComments: true, endDelimiter: ";")
    }

    changeSet(author: "pmontgom", id: "iteration-039/02-schema-cleanup", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_039/02-schema-cleanup.sql", stripComments: true)

        grailsChange {
            change {
                sql.execute("""DECLARE
                       l_sql  VARCHAR2(1000);
                    BEGIN
                      SELECT constraint_name
                      INTO l_sql
                      FROM user_constraints
                      WHERE table_name = 'PANEL_EXPERIMENT'
                        AND constraint_type = 'P';

                      l_sql := 'ALTER TABLE PANEL_EXPERIMENT RENAME CONSTRAINT '|| l_sql ||' TO PK_PANEL_EXPERIMENT';
                      EXECUTE IMMEDIATE l_sql;

                    END;""")
            }
        }
    }

    changeSet(author: "pmontgom", id: "iteration-039/03-drop-unused-tables", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_039/03-drop-unused-tables.sql", stripComments: true)
    }
}
