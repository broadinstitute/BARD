package bardqueryapi;

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/17/12
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */

import bard.core.*
import bard.core.rest.*

public class QueryServiceWrapper {
    final RESTCompoundService restCompoundService;
    final RESTProjectService restProjectService;
    final RESTAssayService restAssayService;
    final RESTExperimentService restExperimentService;
    final String baseURL
    final String promiscuityScoreURL
    public QueryServiceWrapper(final String baseURL, final String promiscuityScoreURL) {
        this.baseURL = baseURL
        this.promiscuityScoreURL = promiscuityScoreURL;
        EntityServiceManager esm = new RESTEntityServiceManager(baseURL);
        this.restCompoundService = esm.getService(Compound.class);
        this.restAssayService = esm.getService(Assay.class);
        this.restProjectService= esm.getService(Project.class);
        this.restExperimentService = esm.getService(Experiment.class);
    }

    public RESTCompoundService getRestCompoundService(){
        return this.restCompoundService;
    }
    public RESTProjectService getRestProjectService(){
        return this.restProjectService;
    }

    public RESTAssayService getRestAssayService(){
        return this.restAssayService;
    }
    public RESTExperimentService getRestExperimentService(){
        return this.restExperimentService;
    }
}
