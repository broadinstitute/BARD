package bard.core.interfaces;

import bard.core.Compound;
import bard.core.Experiment;
import bard.core.Value;

public interface ExperimentService extends EntityService<Experiment> {

    /*
     * return experimental values for an experiment
     */
    SearchResult<Value> activities(Experiment expr);

    /*
     * Return experimental values for an experiment filtered by an etag.
     * Note that an etag must reference a list of substances or compounds
     */
    SearchResult<Value> activities(Experiment expr, Object etag);
}
