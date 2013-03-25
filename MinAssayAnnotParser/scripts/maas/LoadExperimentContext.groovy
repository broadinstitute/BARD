import maas.MustLoadAid
import org.springframework.transaction.support.DefaultTransactionStatus
import maas.ExperimentHandlerService
import bard.db.experiment.Experiment
import org.hibernate.Session
import org.hibernate.jdbc.Work
import java.sql.Connection
/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/8/13
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */

ExperimentHandlerService experimentHandlerService = new ExperimentHandlerService()
final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset1/aids_dataset_1.csv')
def dirs = ['data/maas/maasDataset1']
experimentHandlerService.handle('xiaorong', dirs, mustLoadAids)

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
