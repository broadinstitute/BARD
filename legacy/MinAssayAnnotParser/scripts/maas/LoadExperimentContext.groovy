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
