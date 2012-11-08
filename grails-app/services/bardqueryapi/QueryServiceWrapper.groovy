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
    /**
     * {@link RESTCompoundService}
     */
    final RESTCompoundService restCompoundService;
    /**
     * {@link RESTProjectService}
     */
    final RESTProjectService restProjectService;
    /**
     * {@link RESTAssayService}
     */
    final RESTAssayService restAssayService;
    /**
     * {@link RESTExperimentService}
     */
    final RESTExperimentService restExperimentService;
    final String baseURL
    final String promiscuityScoreURL
    /**
     *
     * @param baseURL
     * @param promiscuityScoreURL
     */
    public QueryServiceWrapper(final String baseURL, final String promiscuityScoreURL) {
        this.baseURL = baseURL
        this.promiscuityScoreURL = promiscuityScoreURL;
        EntityServiceManager esm = new RESTEntityServiceManager(baseURL);
        this.restCompoundService = esm.getService(Compound);
        this.restAssayService = esm.getService(Assay);
        this.restProjectService = esm.getService(Project);
        this.restExperimentService = esm.getService(Experiment);
    }

    public RESTCompoundService getRestCompoundService() {
        return this.restCompoundService;
    }

    public RESTProjectService getRestProjectService() {
        return this.restProjectService;
    }

    public RESTAssayService getRestAssayService() {
        return this.restAssayService;
    }

    public RESTExperimentService getRestExperimentService() {
        return this.restExperimentService;
    }
}
