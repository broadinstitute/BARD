package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.project.Project
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

import static bard.db.registration.ExternalReference.EXT_ASSAY_REF_MAX_SIZE
import static bard.db.registration.ExternalReference.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/13/12
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ExternalReferenceConstraintIntegrationSpec extends IntegrationSpec {

    def domainInstance

        @Before
        void doSetup() {
            domainInstance = ExternalReference.buildWithoutSave(experiment: Experiment.build())
        }

        @After
        void doAfter() {
            if (domainInstance.validate()) {
                domainInstance.save(flush: true)
            }
        }

        void "test externalSystem constraints #desc externalSystem: #valueUnderTest"() {

            final String field = 'externalSystem'

            when:
            domainInstance[(field)] = valueUnderTest.call()
            domainInstance.validate()

            then:
            assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

            where:
            desc                   | valueUnderTest           | valid | errorCode
            'null not valid'       | {null}                   | false | 'nullable'
            'valid externalSystem' | {ExternalSystem.build()} | true  | null
        }

        void "test experiment OR project constraint #desc project: #valueUnderTest"() {
            given:
            domainInstance.experiment = null
            domainInstance.project = null

            when:
            domainInstance.experiment = experiment.call()
            domainInstance.project = project.call()
            domainInstance.validate()

            then:
            assertFieldValidationExpectations(domainInstance, 'experiment', valid, errorCode)
            assertFieldValidationExpectations(domainInstance, 'project', valid, errorCode)

            where:
            desc                      | experiment           | project           | valid | errorCode
            'both null, not valid'    | {null}               | {null}            | false | 'validator.invalid'
            'both non-null not valid' | {Experiment.build()} | {Project.build()} | false | 'validator.invalid'

            'only experiment, valid'  | {Experiment.build()} | {null}            | true  | null
            'only project, valid'     | {null}               | {Project.build()} | true  | null
        }

        void "test extAssayRef constraints #desc extAssayRef: #valueUnderTest"() {

            final String field = 'extAssayRef'

            when:
            domainInstance[(field)] = valueUnderTest
            domainInstance.validate()

            then:
            assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

            where:
            desc               | valueUnderTest                           | valid | errorCode
            'null not valid'   | null                                     | false | 'nullable'
            'blank not valid'  | ''                                       | false | 'blank'
            'blank not valid'  | '   '                                    | false | 'blank'

            'too long'         | createString(EXT_ASSAY_REF_MAX_SIZE + 1) | false | 'maxSize.exceeded'
            'exactly at limit' | createString(EXT_ASSAY_REF_MAX_SIZE)     | true  | null
        }

        void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

            final String field = 'modifiedBy'

            when:
            domainInstance[(field)] = valueUnderTest
            domainInstance.validate()

            then:
            assertFieldValidationExpectations(domainInstance, field, valid, errorCode)



            where:
            desc               | valueUnderTest                         | valid | errorCode
            'too long'         | createString(MODIFIED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
            'blank valid'      | ''                                     | false | 'blank'
            'blank valid'      | '  '                                   | false | 'blank'

            'exactly at limit' | createString(MODIFIED_BY_MAX_SIZE)     | true  | null
            'null valid'       | null                                   | true  | null
        }

        void "test dateCreated constraints #desc dateCreated: '#valueUnderTest'"() {
            final String field = 'dateCreated'

            when:
            domainInstance[(field)] = valueUnderTest
            domainInstance.validate()

            then:
            assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

            where:
            desc             | valueUnderTest | valid | errorCode
            'null not valid' | null           | false | 'nullable'
            'date valid'     | new Date()     | true  | null
        }

        void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
            final String field = 'lastUpdated'

            when:
            domainInstance[(field)] = valueUnderTest
            domainInstance.validate()

            then:
            assertFieldValidationExpectations(domainInstance, field, valid, errorCode)


            where:
            desc         | valueUnderTest | valid | errorCode
            'null valid' | null           | true  | null
            'date valid' | new Date()     | true  | null
        }
}
