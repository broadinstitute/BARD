package modules

import geb.Module
import geb.navigator.Navigator

class SectionHeaderModule extends Module {
	def sectionName
	static content = {
		header { $("#"+sectionName+"-header") }
	}
}