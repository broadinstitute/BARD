package bard.core.rest

import grails.plugin.spock.IntegrationSpec
import spock.lang.Shared
import bard.core.*
import bard.core.rest.*
import bard.core.interfaces.EntityServiceManager

/**
 * Abstract class to hold configuration
 */
abstract class AbstractRESTServiceSpec extends IntegrationSpec {
    @Shared def grailsApplication
    @Shared public String baseURL
    @Shared EntityServiceManager esm
    @Shared RESTExperimentService restExperimentService
    @Shared RESTAssayService restAssayService
    @Shared RESTProjectService restProjectService
    @Shared RESTCompoundService restCompoundService
    /**
     * default timeout in milliseconds
     */
    // @Rule
    // public TestRule globalTimeout = new Timeout(1000 * 1000);

    def setupSpec() {
        String baseURL = grailsApplication.config.ncgc.server.root.url
        this.esm = new RESTEntityServiceManager(baseURL);
        this.restExperimentService = esm.getService(Experiment.class);
        this.restCompoundService = esm.getService(Compound.class);
        this.restAssayService = esm.getService(Assay.class)
        this.restProjectService = esm.getService(Project.class)
    }

    void cleanupSpec() {
        this.restExperimentService.shutdown()
        this.restCompoundService.shutdown()
        this.restAssayService.shutdown()
        this.restProjectService.shutdown()
        this.esm.shutdown()
    }
}