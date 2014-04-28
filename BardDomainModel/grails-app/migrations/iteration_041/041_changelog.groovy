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

package iteration_041

databaseChangeLog = {

    changeSet(author: "ycruz", id: "iteration-041/00-add_columns-approvedby-and-date-to-entities", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                                   bard_context.set_username('ycruz');
                                   END;
                                   """)
            }
        }
        sqlFile(path: "iteration_041/00-add_columns-approvedby-and-date-to-entities.sql", stripComments: true, endDelimiter: ";")
    }

    changeSet(author: "pmontgom", id: "iteration-041/01-update-email", dbms: "oracle", context: "standard") {

        grailsChange {
            change {
                sql.execute("BEGIN bard_context.set_username('pmontgom'); end;")
                sql.execute("update person set email_address = lower(email_address) where email_address <> lower(email_address)")
            }
        }
    }
    changeSet(author: "pmontgom", id: "iteration-041/01-add-email-constraint", dbms: "oracle", context: "standard") {

        grailsChange {
            change {
                sql.execute("alter table person add constraint uk_person_email_address unique (email_address)")
            }
        }
    }
}
