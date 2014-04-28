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

package iteration_006

databaseChangeLog = {
    changeSet(author: 'ddurkin', id: 'set null assay_type to Regular', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.executeUpdate("""UPDATE ASSAY
                                     SET ASSAY_TYPE = 'Regular'
                                     WHERE ASSAY_TYPE IS NULL
                                  """)
            }
        }
    }

    changeSet(author: 'ddurkin', id: 'assay_type NOT NULL', dbms: 'oracle', context: 'standard') {
        addNotNullConstraint(tableName: "ASSAY", columnName: "assay_type")
    }

    changeSet(author: 'ddurkin', id: 'check constraint updates', dbms: 'oracle', context: 'standard', runOnChange: 'true') {
        // change spelling for to Superseded for CK_ASSAY_STATUS
        sql("ALTER TABLE ASSAY DROP CONSTRAINT CK_ASSAY_STATUS")
        sql("""UPDATE ASSAY SET ASSAY_STATUS = 'Superseded'
               WHERE ASSAY_STATUS = 'Superceded'
            """)
        sql("""ALTER TABLE ASSAY ADD ( CONSTRAINT CK_ASSAY_STATUS
               CHECK ( Assay_Status IN ('Pending', 'Active', 'Superseded', 'Retired') ))
            """)

        sql("""ALTER TABLE ASSAY DROP CONSTRAINT CK_ASSAY_EXTRACTION
            """)
        sql("""ALTER TABLE ASSAY ADD ( CONSTRAINT CK_ASSAY_EXTRACTION
               CHECK ( ready_for_extraction IN ('Pending', 'Ready', 'Started', 'Complete')))
            """)


    }

    changeSet(author: 'ddurkin', id: 'iteration-006/01-assay-related-refactoring.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_006/sql/01-assay-related-refactoring.sql", stripComments: true)
    }

    changeSet(author: 'ddurkin', id: 'iteration-006/02-element-related-refactoring.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_006/sql/02-element-related-refactoring.sql", stripComments: true)
    }

    changeSet(author: 'ddurkin', id: 'iteration-006/03-project-related-refactoring.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_006/sql/03-project-related-refactoring.sql", stripComments: true)
    }

    changeSet(author: 'ddurkin', id: 'iteration-006/04-result-related-refactoring.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_006/sql/04-result-related-refactoring.sql", stripComments: true)
    }

    changeSet(author: 'ddurkin', id: 'iteration-006/05-identifier_mapping-index.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_006/sql/05-identifier_mapping-index.sql", stripComments: true)
    }
    changeSet(author: 'ddurkin', id: 'iteration-006/06-project-related-refactoring.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_006/sql/06-project-related-refactoring.sql", stripComments: true)
    }
    changeSet(author: 'ddurkin', id: 'iteration-006/07-more-element-changes.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_006/sql/07-more-element-changes.sql", stripComments: true)
    }


}
