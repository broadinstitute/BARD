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

import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

databaseChangeLog = {
    String BACKSLASH_ONLY_OPTIONAL_WHITESPACE = /(?m)^\s*\/\s*$/

    changeSet(author: 'pmontgom', id: 'invalid-context', dbms: 'oracle', context: 'invalidContext', runAlways: true) {
        grailsChange {
            change {
                throw new RuntimeException("Woah there cowboy!  Always run dbm-update with --contexts=...")
            }
        }
    }

    changeSet(author: 'ddurkin', id: 'rename-changelog-filenames.sql', dbms: 'oracle', context: 'rename-changelog') {
        sqlFile(path: "sql/rename-changelog-filenames.sql", stripComments: true)
    }

    /* this needs to be created early in the process because some of the changelogs manually call set_context */
    changeSet(author: 'ddurkin', id: 'create-bard-context.sql', dbms: 'oracle', context: 'standard, auditing', runOnChange: 'true') {
        grailsChange {
            final List<String> sqlBlocks = []
            String text = resourceAccessor.getResourceAsStream('sql-auditing/create-bard-context.sql').text
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

    changeSet(author: 'ddurkin', id: 'baseline-structure-ddl.sql', dbms: 'oracle', context: 'standard') {
        sqlFile(path: "sql/baseline-structure-ddl.sql", stripComments: true)
    }

    include(file: "iteration_006/006_changelog.groovy")
    include(file: "iteration_007/007_changelog.groovy")
    include(file: "iteration_012/012_changelog.groovy")
    include(file: "iteration_014/014_changelog.groovy")
    include(file: "iteration_017/017_changelog.groovy")
    include(file: "iteration_018/018_changelog.groovy")
    include(file: "iteration_019/019_changelog.groovy")
    include(file: "iteration_020/020_changelog.groovy")
    include(file: "iteration_021/021_changelog.groovy")
    include(file: "iteration_023/023_changelog.groovy")
    include(file: "iteration_024/024_changelog.groovy")
    include(file: "iteration_025/025_changelog.groovy")
    include(file: "iteration_026/026_changelog.groovy")
    include(file: "iteration_027/027_changelog.groovy")
    include(file: "iteration_028/028_changelog.groovy")
    include(file: "iteration_030/030_changelog.groovy")
    include(file: "iteration_032/032_changelog.groovy")
    include(file: "iteration_033/033_changelog.groovy")
    include(file: "iteration_034/034_changelog.groovy")
    include(file: "iteration_035/035_changelog.groovy")
    include(file: "iteration_036/036_changelog.groovy")
    include(file: "iteration_037/037_changelog.groovy")
    include(file: "iteration_038/038_changelog.groovy")
    include(file: "iteration_039/039_changelog.groovy")
    include(file: "iteration_041/041_changelog.groovy")
    include(file: "iteration_042/042_changelog.groovy")
    include(file: "iteration_043/043_changelog.groovy")
    include(file: "iteration_044/044_changelog.groovy")
    include(file: "iteration_046/046_changelog.groovy")

    // views
    changeSet(author: 'ddurkin', id: 'create-or-replace-dictionary-views.sql', dbms: 'oracle', context: 'standard', runAlways: 'true') {
        sqlFile(path: "sql/create-or-replace-dictionary-views.sql", stripComments: true)
    }

    // do last

    include file: 'load-reference-data.groovy'
    include file: 'manage-audit-procedures.groovy'
    include file: 'manage-stored-procedures.groovy'
    include file: 'manage-names-pkg.groovy'
    include file: 'drop-retired-tables.groovy'
    include file: 'reset-sequences.groovy'
    include file: 'grant-selects.groovy'
}



