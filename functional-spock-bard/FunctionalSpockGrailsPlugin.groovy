class FunctionalSpockGrailsPlugin {
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2 > *"
    def version = "0.7"

    def dependsOn = [:]
    def pluginExcludes = [
            "grails-app/**",
            "web-app"
    ]

    def author = "Sebastian Gozin"
    def authorEmail = "sebastian.gozin@gmail.com"
    def title = "Spock Functional Plugin - spockframework.org"
    def description = 'Write Grails functional tests with Spock'
    def documentation = 'http://grails.org/plugin/functional-spock'
}
