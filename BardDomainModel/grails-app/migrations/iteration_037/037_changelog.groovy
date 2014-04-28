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

package iteration_037

databaseChangeLog = {

    changeSet(author: "sbrudz", id: "iteration-037/01-remove-vestigial-columns-from-query-cart", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("alter table sc_shoppable drop column NUM_ASSAY_ACTIVE")
                sql.execute("alter table sc_shoppable drop column NUM_ASSAY_TESTED")
            }
        }
    }

    changeSet(author: "gwalzer", id: "iteration-037/create-bard-news-table", context: "standard") {
        createTable(tableName: "BARD_NEWS") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "BARD_NEWS_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "entry_id", type: "VARCHAR2(255)") {
                constraints(nullable: "false")
            }

            column(name: "Date_Published", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "Entry_Date_Updated", type: "TIMESTAMP(6)") {
                constraints(nullable: "true")
            }

            column(name: "title", type: "VARCHAR2(1023)") {
                constraints(nullable: "false")
            }

            column(name: "content", type: "CLOB") {
                constraints(nullable: "true")
            }

            column(name: "link", type: "VARCHAR2(255)") {
                constraints(nullable: "false")
            }

            column(name: "author_name", type: "VARCHAR2(127)") {
                constraints(nullable: "true")
            }

            column(name: "author_email", type: "VARCHAR2(127)") {
                constraints(nullable: "true")
            }

            column(name: "author_uri", type: "VARCHAR2(255)") {
                constraints(nullable: "true")
            }

            column(name: "Date_Created", type: "TIMESTAMP(6)", defaultValueComputed: "SYSDATE") {
                constraints(nullable: "false")
            }

            column(name: "Last_Updated", type: "TIMESTAMP(6)") {
                constraints(nullable: "true")
            }

            column(name: "MODIFIED_BY", type: "VARCHAR2(40)") {
                constraints(nullable: "true")
            }
        }
        createSequence(sequenceName: "BARD_NEWS_ID_SEQ")
    }
}
