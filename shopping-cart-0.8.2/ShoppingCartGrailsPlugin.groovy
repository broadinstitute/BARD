import com.metasieve.shoppingcart.ShoppableMixin

class ShoppingCartGrailsPlugin {
    def version = "0.8.2"
	def grailsVersion = "1.1 > *"
    def dependsOn = [:]
	def pluginExcludes = [
		"grails-app/views/error.gsp"
    ]

    def author = "Bjoern Wilmsmann"
    def authorEmail = "bjoern@metasieve.com"
    def title = "Shopping cart plugin"
    def description = '''\
Shopping cart plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/shopping-cart"

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }
   
    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)		
    }

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional)
    }
	                                      
    def doWithDynamicMethods = { ctx ->
		ShoppableMixin.mixin()
    }
	
    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
