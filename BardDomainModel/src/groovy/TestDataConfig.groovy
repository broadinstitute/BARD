/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import bard.db.dictionary.Element
import bard.db.enums.ExpectedValueType
import bard.db.enums.ValueType

///**
// * see https://bitbucket.org/tednaleid/grails-test-data/wiki/Features
// * see https://bitbucket.org/tednaleid/grails-test-data/wiki/TestDataConfig
// *
// * NOTE: Some constraints are not automatically supported
// *
// *    scale
// *    validator - custom validators can have all kinds of business logic in them and there isn't a programmatic way to interrogate this. If the test value that's tried doesn't pass your logic, you'll need to provide a value, or mock out the calls that the validator makes so that it passes.
// *    unique - not directly supported but it's possible to specify your own unique value in a config file as demonstrated on the SampleCode page.
// *    notEqual - unlikely that our test data would match this, could be supported in the future
// *
// */
testDataConfig {
    sampleData {
        'bard.db.dictionary.BardDictionaryDescriptor' {
            def i = -1
            label = {-> "label${i}" }
            id = {-> i-- }
        }
        'bard.db.dictionary.BardDescriptor' {
            def i = -1
            id = {-> i-- }
        }
        'bard.db.dictionary.AssayDescriptor' {
            def i = -1
            id = {-> i-- }
        }
        'bard.db.dictionary.BiologyDescriptor' {
            def i = -1
            id = {-> i-- }
        }
        'bard.db.dictionary.InstanceDescriptor' {
            def i = -1
            id = {-> i-- }
        }
        'bard.db.dictionary.Element' {
            def i = 1
            label = {-> "label${i++}" }
        }
        'bard.db.people.Role' {
            def i = 1
            authority = {-> "ROLE_TEAM_${i++}" }
        }
        'bard.db.dictionary.UnitTree' {
            def i = 1
            id = {-> i++ }
        }
        'bard.db.dictionary.LaboratoryTree' {
            def i = 1
            id = {-> i++ }
        }
        'bard.db.dictionary.ResultTypeTree' {
            def i = 1
            id = {-> i++ }
        }
        'bard.db.dictionary.StageTree' {
            def i = 1
            id = {-> i++ }
        }
        'bard.db.experiment.Substance' {
            def i = 1
            id = {-> i++ }
        }
        'bard.db.project.ProjectContextItem' {
            valueType = ValueType.FREE_TEXT
            valueDisplay = "valueDisplay"
            attributeElement = {->Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)}
        }
        'bard.db.registration.AssayContextItem' {
            valueType = ValueType.FREE_TEXT
            valueDisplay = "valueDisplay"
            attributeElement = {->Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)}
        }
        'bard.db.experiment.ExperimentContextItem' {
            valueType = ValueType.FREE_TEXT
            valueDisplay = "valueDisplay"
            attributeElement = {-> Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)}
        }
        'bard.db.project.ProjectExperimentContextItem' {
            valueType = ValueType.FREE_TEXT
            valueDisplay = "valueDisplay"
            attributeElement = {-> Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)}
        }
        'bard.db.project.StepContextItem' {
            valueType = ValueType.FREE_TEXT
            valueDisplay = "valueDisplay"
            attributeElement = {-> Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)}
        }
        'bard.db.project.ResultContextItem' {
            valueType = ValueType.FREE_TEXT
            valueDisplay = "valueDisplay"
            attributeElement = {-> Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)}
        }

    }
}

/*
// sample for creating a single static value for the com.foo.Book's title property:
// title for all Books that we "build()" will be "The Shining", unless explicitly set

testDataConfig {
    sampleData {
        'com.foo.Book' {
            title = "The Shining"
        }
    }
}
*/

/*
// sample for creating a dynamic title for com.foo.Book, useful for unique properties:
// just specify a closure that gets called

testDataConfig {
    sampleData {
        'com.foo.Book' {
            def i = 1
            title = {-> "title${i++}" }   // creates "title1", "title2", ...."titleN"
        }
    }
}
*/

/*
// When using a closure, if your tests expect a particular value, you'll likely want to reset
// the build-test-data config in the setUp of your test, or in the test itself.  Otherwise if
// your tests get run in a different order you'll get different values

// (in test/integration/FooTests.groovy)

void setUp() {
    grails.buildtestdata.TestDataConfigurationHolder.reset()
}
*/

/*
// if you'd like to disable the build-test-data plugin in an environment, just set
// the "enabled" property to false

environments {
    production {
        testDataConfig {
            enabled = false
        }
    }
}
*/
