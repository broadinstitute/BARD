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
        'bard.db.registration.Assay'{
            assayVersion = "1"
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