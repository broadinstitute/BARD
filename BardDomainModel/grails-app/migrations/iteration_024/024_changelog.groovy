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

package iteration_024

databaseChangeLog = {
    changeSet(author: "pmontgom", id: "iteration-024/01-add-experiment-file-pk", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_024/01-add-experiment-file-pk.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-024/01-add-parent-child-relationship-to-assay", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_024/01-add-parent-child-relationship-to-assay.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "iteration-024/03-add-display-order-unique-constraint", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_024/03-add-display-order-unique-constraint.sql", stripComments: true)
    }
    changeSet(author: "jasiedu", id: "add parent child relationship to measures", dbms: 'oracle', context:'standard') {
        grailsChange {
            change {
                sql.eachRow("SELECT DISTINCT MEASURE_ID, PARENT_CHILD_RELATIONSHIP FROM EXPRMT_MEASURE WHERE PARENT_CHILD_RELATIONSHIP IS NOT NULL ORDER BY MEASURE_ID") {row ->
                    String updateStatement = "UPDATE MEASURE SET PARENT_CHILD_RELATIONSHIP='${row.PARENT_CHILD_RELATIONSHIP}' WHERE MEASURE_ID=${row.MEASURE_ID}"
                    sql.executeUpdate(updateStatement)
                }
            }
        }
    }
    changeSet(author: 'jasiedu', id: 'update parent child for mesures', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.executeUpdate("""UPDATE MEASURE SET PARENT_CHILD_RELATIONSHIP='supported by'
                                      WHERE PARENT_CHILD_RELATIONSHIP IS NULL AND PARENT_MEASURE_ID IS NOT NULL
                                  """)
            }
        }
    }
    changeSet(author: 'jasiedu', id: 'add parent child constraint to EXPRMT_MEASURE', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.execute("""ALTER TABLE EXPRMT_MEASURE ADD CONSTRAINT CK_EXPRMT_MEASURE_PARENT
                                    CHECK ((PARENT_CHILD_RELATIONSHIP IS NOT NULL AND PARENT_EXPRMT_MEASURE_ID IS NOT NULL)
                                    OR
                                    (PARENT_CHILD_RELATIONSHIP IS NULL AND PARENT_EXPRMT_MEASURE_ID IS NULL))
                                  """)
            }
        }
    }
    changeSet(author: "jasiedu", id: "remove parent child relationship for null parent_measures", dbms: 'oracle', context:'standard') {
        grailsChange {
            change {
                String updateStatement = "UPDATE MEASURE SET PARENT_CHILD_RELATIONSHIP = NULL WHERE PARENT_MEASURE_ID IS NULL"
                sql.executeUpdate(updateStatement)
            }
        }
    }
    changeSet(author: 'jasiedu', id: 'add parent child constraint to MEASURE_PARENT', dbms: 'oracle', context: 'standard') {
        grailsChange {
            change {
                sql.execute("""ALTER TABLE MEASURE ADD CONSTRAINT CK_MEASURE_PARENT
                                CHECK ((PARENT_CHILD_RELATIONSHIP IS NOT NULL AND PARENT_MEASURE_ID IS NOT NULL)
                                OR (PARENT_CHILD_RELATIONSHIP IS NULL AND PARENT_MEASURE_ID IS NULL))
                                  """)
            }
        }
    }
    changeSet(author: "jasiedu", id: "iteration-024/04-add-comments-field-to-Element", dbms: 'oracle', context: 'standard') {
        sqlFile(path: "iteration_024/04-add-comments-field-to-Element.sql", stripComments: true)
    }

    changeSet(author: "pmontgom", id: "iteration-024/05-recreate-deferred-constraints", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_024/05-recreate-deferred-constraints.sql", stripComments: true)
    }

    changeSet(author: "pmontgom", id: "iteration-024/06-create-ext-ontology-tree", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.executeUpdate("create table ext_ontology_tree as select * from assay_descriptor_tree")
            }
        }
    }

    changeSet(author: "dlahr", id: "iteration-024/07-elmnt-hrrchy-parent-elmnt-id-not-null", dbms: "oracle", context: "standard") {
        grailsChange {
            change {
                sql.executeUpdate("alter table element_hierarchy modify parent_element_id not null")
            }
        }
    }

    changeSet(author: "pmontgom", id: "iteration-024/08-add-replaced-by-to-element", dbms: "oracle", context: "standard") {
        sqlFile(path: "iteration_024/08-add-replaced-by-to-element.sql", stripComments: true)
    }
}

