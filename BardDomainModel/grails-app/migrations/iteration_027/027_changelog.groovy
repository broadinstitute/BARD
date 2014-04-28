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

package iteration_027

databaseChangeLog = {

    changeSet(author: "jasiedu", id: "iteration-027/01-create-acl-tables", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_027/01-create-acl-tables.sql", stripComments: true)
    }
	
	changeSet(author: "jasiedu", id: "iteration-027/02-create-acl-seq-tables", dbms: "oracle", context: "standard") {
		sqlFile(path: "iteration_027/02-create-acl-seq-tables.sql", stripComments: true)
	}
	
	changeSet(author: "ycruz", id: "iteration-027/03-add-new-object-role-column-to-Person", dbms: "oracle", context: "standard") {
		sqlFile(path: "iteration_027/03-add-new-object-role-column-to-Person.sql", stripComments: true)
	}

    changeSet(author: "jasiedu", id: "iteration-027/04-make-last-updated-field-non-nullable", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.set_username('jasiedu');
                               END;
                               """)
            }
        }
        sqlFile(path: "iteration_027/04-make-last-updated-field-non-nullable.sql", stripComments: true)
        grailsChange {
            change {
                sql.execute("""BEGIN
                               bard_context.clear_username();
                               END;
                               """)
            }
        }
    }
}

