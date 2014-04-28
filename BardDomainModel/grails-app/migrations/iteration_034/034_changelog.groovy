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

package iteration_034

databaseChangeLog = {

    changeSet(author: "jasiedu", id: "iteration-034/01-create-downtimescheduler", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_034/01-create-downtimescheduler.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-034/02-add-owner-role-to-entities", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_034/02-add-owner-role-to-entities.sql", stripComments: true)
    }
    changeSet(author: "pmontgom", id: "iteration-034/03-add-resultmap-table", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_034/03-add-resultmap-table.sql", stripComments: true, endDelimiter: "/")
    }
    changeSet(author: "ycruz", id: "iteration-034/04-modify-element-abbreviation-column", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('ycruz');
                               END;
                               """)
            }
        }
        sqlFile(path: "iteration_034/04-modify-element-abbreviation-column.sql", stripComments: true, endDelimiter: ";")
    }
}
