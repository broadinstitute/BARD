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

import maas.MustLoadAid
import org.springframework.transaction.support.DefaultTransactionStatus
import maas.ExperimentHandlerService
import bard.db.experiment.Experiment
import org.hibernate.Session
import org.hibernate.jdbc.Work
import java.sql.Connection
import org.apache.commons.lang3.StringUtils
/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/8/13
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */


String dir =  System.getProperty("inputdir")
String dataset_id = System.getProperty("datasetid")
String willCommit = System.getProperty("willcommit")
if (!dir) {
    println("Please provide inputdir: -Dinputdir=yourdir")
    return
}
if (!dataset_id && !StringUtils.isNumeric(dataset_id)) {
    println("Please provide a valid dataset id: -Ddatasetid=validid")
    return
}
if (!willCommit) {
    println("This will be a dry run to check if there is any errors need to be fixed in spreadsheet.")
    println("If you are sure you need to commit: -Dwillcommit=true|false")
}


final String runBy = "xx"
final List<String> inputDirs = [dir]
final String outputDir = "${dir}/output/"

def mustLoadedAids
Experiment.withSession { session ->
    mustLoadedAids = session.createSQLQuery("""
      select distinct aid from bard_data_qa_dashboard.vw_dataset_aid where dataset_id=${dataset_id} order by aid
  """).setCacheable(false).list().collect {it.longValue()}
}


//String aidsFile = "aids.csv"
//def mustLoadedAids = MustLoadAid.mustLoadedAids("${dir}/${aidsFile}")
println(mustLoadedAids)
if (!mustLoadedAids) {
    println("Nothing to be loaded")
    return
}


//String aidsFile = "aids.csv"
//final String runBy = "xx"
//final List<String> inputDirs = [dir]
//def mustLoadedAids = MustLoadAid.mustLoadedAids("${dir}${aidsFile}")

ExperimentHandlerService experimentHandlerService = new ExperimentHandlerService()
if (!StringUtils.equals("true", willCommit)) {
    println "willCommit is not true"
    Experiment.withTransaction{status ->
        experimentHandlerService.handle(runBy, inputDirs, mustLoadedAids)
        status.setRollbackOnly()
    }
}
else {
    println("willCommit is true")
    experimentHandlerService.handle(runBy, inputDirs, mustLoadedAids)
}

// renumber the display order
Experiment.withSession {Session session->
    session.doWork(new Work(){
        void execute(Connection connection) {
            connection.createStatement().executeUpdate("""
declare
  new_display_index number;
begin
  for rec in (select experiment_id from experiment) loop
    new_display_index := 0;
    for irec in (select exprmt_context_id from exprmt_context where experiment_id = rec.experiment_id order by display_order ) loop
      update exprmt_context set display_order = new_display_index where exprmt_context_id = irec.exprmt_context_id;
      new_display_index := new_display_index + 1;
    end loop;
  end loop;
end;
            """)
        }
    })
}
