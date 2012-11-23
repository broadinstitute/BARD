package bard.core.rest

import bard.core.interfaces.SearchResult
import junit.framework.Assert
import spock.lang.Timeout
import spock.lang.Unroll
import bard.core.*

import bard.core.rest.helper.RESTTestHelper

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 9/5/12
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Mixin(RESTTestHelper)
@Unroll
class RESTExperimentServiceIntegrationSpec extends AbstractRESTServiceSpec {
    void "testExperiment"() {
        given:
        List<Long> assayIds = [460, 461, 197, 198, 4171, 3278, 3274, 3277, 2362, 2637]
        when:
        final Collection<Experiment> experiments = restExperimentService.get(assayIds)
        then:
        assert experiments
        assert !experiments.isEmpty()
    }

    void "testExperimentActivity"() {
        given:
        Experiment experiment = this.restExperimentService.get(197L);
        when:
        SearchResult<Value> actiter = restExperimentService.activities(experiment);
        then:
        assert actiter.searchResults
        assert !actiter.searchResults.isEmpty()
        List<Value> next = actiter.next(10);
        assert next.size() == 10, "There should be at least 10 activities for experiment 197"
    }

    public void "testExperimentActivityWithCompounds"() {
        given: "That an etag is created from a list of compound IDs"
        Object etag = restCompoundService.newETag("foo cids", [3237462L, 3240101L, 16001802L]);
        and: "That an experiment exists"
        Experiment experiment = restExperimentService.get(2273L);
        assert experiment

        when: "Experiment with the compound etags"
        SearchResult<Value> acts = restExperimentService.activities(experiment, etag);
        then:
        assert acts
        final List<Value> entities = acts.searchResults
        assert entities
        assert !acts.searchResults.isEmpty()
        for (Value value : entities) {
            assert value
        }
        assert 3 == acts.getCount();
    }




    void "test retrieving the experimental data for known compounds in an experiment #label"() {
        given:
        Object etag = restCompoundService.newETag(label, cids);

        when: "We call the getFactest matehod"
        final Experiment experiment = this.restExperimentService.get(experimentid)
        //   Collection<Compound> compounds = this.restCompoundService.get(cids)

        then: "We expect to get back a list of facets"
        SearchResult<Value> eiter = this.restExperimentService.activities(experiment, etag);
        final List<Value> entities = eiter.searchResults
        Assert.assertNotNull entities
        for (Value value : entities) {
            assert value
        }
        eiter.getCount() == cids.size() //not sure if this is 100% valid, seems like it will totally depend on the data backing the api

        where:
        label                            | experimentid   | cids
        "Search with a list of CIDs"     | new Long(883)  | [3233285, 3234360, 3235360]
        "Search with a single of CID"    | new Long(2273) | [16001802]
        "Search with another set of CID" | new Long(2273) | [3237462, 3240101, 16001802]
    }

/*
The following section of code attempts to explore some of the ways in which ETags
could be used to generate data sets that represent a meaningful intersection
between user selections of projects, assays, compounds, and experiments. The calls
that are marked with "err" fail out right, the calls marked as "works" return successfully
( though I don't test the validity of their results), and the calls marked as "ok" don't crash,
but return only a null.  It could be that there is useful functionality here that I'm failing
to utilize, but as of yet I don't know how to get anything useful out of non-compound related
eTags.

    void "testing  non-compound eTags"() {
        given: "some valid etags"
        Object compoundEtag = restCompoundService.newETag("My compound collection", [2123,4781,5342]);   // works?
        Object assayEtag = restCompoundService.newETag("My assay collection", [519]); // works?
        Object projectEtag = restCompoundService.newETag("My project collection", [805]);  // works?
        when: "We use services to build sets from eTags"
//////////// ASSAY SERVICE
//         def assay1 =  this.restCompoundService( compoundEtag )    // err
//         def assay2 =  this.restCompoundService( assayEtag )       // err
//         def assay3 =  this.restCompoundService( projectEtag )    // err

//////////// compound SERVICE
        def compound1 = this.restCompoundService.get( compoundEtag )   // ok
        def compound2 = this.restCompoundService.get( assayEtag )      // ok
        def compound3 = this.restCompoundService.get( projectEtag )    // ok

//////////// project SERVICE
// err       def project1 = this.restProjectService( projectEtag )  // err
        def project2 = this.restProjectService.get( compoundEtag )  // ok
        def project3 = this.restProjectService.get( assayEtag )     // ok

//////////// experiment SERVICE
        Experiment experiment1 = this.restExperimentService.get( compoundEtag )   // ok
        Experiment experiment2 = this.restExperimentService.get( assayEtag )      // ok
        Experiment experiment3 = this.restExperimentService.get( projectEtag )    // ok

///////////////////////////////
////////////////   get id sets from services
//////////////////////////////
        Collection<Assay> assays = this.restCompoundService.get([519])  // works
        Collection<Project> projects = this.restProjectService.get([805])  // works
        Collection<Compound> compounds = this.restCompoundService.get([2123,4781,5342]) // works
        Collection<Experiment> ESexperiments = this.restExperimentService.get([519])  // works

    }
*/









    void "test step through entire Experiment iterator #experimentid"() {

        when: "The get method is called with the given experiment ID: #experimentid"
        final Experiment experiment = this.restExperimentService.get(experimentid)
        then: "An experiment is returned with the expected information"
        assert experiment
        assert experimentid == experiment.id
        assertExperimentSearchResult(experiment)
        where:
        label                | experimentid
        "Find an experiment" | new Long(346)  // short expt (3000 values)
        //"Find an experiment"       | new Long(1326)  // longer expt (193717 values) -- runs for 8 minutes!
    }

    /**
     * Step through the experiment with the iterator
     * @param experiment
     */
    void assertExperimentSearchResult(Experiment experiment) {
        SearchResult<Value> eiter = this.restExperimentService.activities(experiment);
        final List<Value> entities = eiter.searchResults
        assert entities
        ExperimentHolder experimentHolder = new ExperimentHolder()
        for (Value v : entities) {
            assert v
            experimentHolder.appendValue(v)
        }

    }



    void "test pull other values out of an experiment"() {

        when: "The get method is called with the given experiment ID: #experimentid"
        final Experiment experiment = this.restExperimentService.get(experimentid)
        then: "An experiment is returned with the expected information"
        assert experimentid == experiment.id
        //Assay assay = experiment.getAssay()
//        assert assay!=null // presummably this shouldn't be empty, but it is
        Collection<Project> projectCollection = experiment.getProjects()
        int projectCount = experiment.getProjectCount()
        assert projectCollection.size() == projectCount
        where:
        label                | experimentid
        "Find an experiment" | new Long(346)
    }

    /**
     * Since there is no experimentAdapter I had to make a method to open up the experiment
     */
    public class ExperimentHolder {

        LinkedHashMap<String, Object> dataHolder
        Integer experimentID = 0

        public ExperimentHolder() {
            dataHolder = new LinkedHashMap<String, Object>()
        }

        public appendValue(Value value) {
            //assert value.children.size() <= 4
            assert value.children.size() >= 3
            Collection<Value> valueIterator = value.getChildren()
            String keyValue
            Long cid = null
            Long sid = null
            HillCurveValue hillCurveValue
            Object payload = null
            for (Value internalValue : valueIterator) {
                String identifier = internalValue.id
                switch (identifier) {
                    case "eid":
                        assert internalValue instanceof IntValue
                        IntValue intValue = internalValue
                        if (experimentID == 0)
                            experimentID = intValue.value
                        else
                            assert experimentID == intValue.value
                        break
                    case "cid":
                        assert internalValue instanceof LongValue
                        LongValue longValue1 = internalValue
                        cid = longValue1.value as Long
                        break
                    case "sid":
                        assert internalValue instanceof LongValue
                        LongValue longValue2 = internalValue
                        sid = longValue2.value as Long
                        break
                    case "Activity":
                        assert internalValue instanceof HillCurveValue
                        hillCurveValue = internalValue
                        payload = hillCurveValue
                        break
                    case "Activation":
                        assert internalValue instanceof HillCurveValue
                        hillCurveValue = internalValue
                        payload = hillCurveValue
                        break
                    default:
                        println "did not expect type =${identifier}"
                }
            }
            keyValue = (cid as String) + "_" + (sid as String)
            dataHolder.put(keyValue, payload)
        }

    }

    @Timeout(10)
    void "tests getExperimentActivities #label"() {
        given: "Experiment ID"
        final Experiment experiment = restExperimentService.get(experimentId)

        when: "We call the restExperimentService.activities method with the experiment"
        final SearchResult<Value> experimentSearchResult = restExperimentService.activities(experiment);

        then: "We expect activities for each of the experiments to be found"
        List<Value> activityValues = experimentSearchResult.next(10)
        assert activityValues

        where:
        label                                        | experimentId
        "For an existing experiment with activities" | new Long(346)
    }


}
