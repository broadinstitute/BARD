package bard.db.experiment.results

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 9/30/13
 * Time: 5:34 PM
 * To change this template use File | Settings | File Templates.
 */
class JobStatus implements Serializable {
    String status;
    ImportSummary summary;
}