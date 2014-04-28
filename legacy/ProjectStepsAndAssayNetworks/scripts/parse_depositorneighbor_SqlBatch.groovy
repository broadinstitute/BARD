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

import groovy.sql.Sql
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.time.StopWatch
import org.apache.commons.lang.StringUtils

@Grapes(
@Grab(group = 'commons-io', module = 'commons-io', version = '2.1')

)

StopWatch sw = new StopWatch()
sw.start()
def db = [url: 'jdbc:oracle:thin:@vmbarddev:1521:barddev', user: 'gwalzer_bard', password: 'ch3mb10', driver: 'oracle.jdbc.driver.OracleDriver']
def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)

String dir = 'C:/BARD_DATA_MIGRATION/SwamidassProjectFlow'
String filename = 'pcassay_pcassay_depositorneighbor.txt'



println("started reading $filename current time: ${new Date()}")

File file = new File(dir, filename)
assert file.exists()
println("file size: ${file.size()}")
file.withReader { reader ->

    //Prepared statement to be passed to the sql.withBatch method.
    String insertSql = """insert into RELATION(ID, VERSION, PARENT, PARENT_ID, CHILD, CHILD_ID) values
                            (RELATION_ID_SEQ.nextval,0, ?, ?, ?, ?)"""
//    println(insertSql)
    int count = 0
    int batch = 100000
    def batchSw = new StopWatch(); batchSw.start();
    sql.withTransaction {
        sql.withBatch(batch, insertSql) { preparedStatement ->
            reader.eachLine {String line ->
                String[] values = line.trim().split()
                String parent = values[0]
                Long parentId = new Long(parent - 'AID')
                String child = values[1]
                Long childId = new Long(child - 'AID')

                //Populate the params in the prepared-statement
                [parent, parentId, child, childId].eachWithIndex {param, i ->
                    int oneBasedIndex = i + 1
                    preparedStatement.setString(oneBasedIndex, param.toString())
                }

                preparedStatement.addBatch()
                count++

                if (count != 0 && count % batch == 0) {
                    println("added $count rows. duration: ${batchSw}; total time: ${sw}")
                    batchSw.reset(); batchSw.start();
                }
            }
        }
    }
}
sw.stop()
println("completed. duration: ${sw}; current time: ${new Date()}")
