package bard.db.experiment.results

import com.fasterxml.jackson.annotation.JsonIgnore

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
    private boolean finished = false;

    // if finished is not private groovy will create both isFinished and getFinished and that causes jackson to get confused, so create these manually
    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished
    }
}