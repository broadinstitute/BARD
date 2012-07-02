package bard.db.registration

class AssayJSonController {

    def index() {
		redirect(action: "getNames", params: params)
	}
	
	def getNames() {
		def results
		if(params?.term)
			results = Assay.findAllByAssayNameIlike("%${params.term}%")
		else
			results = Assay.list()	
					
		render(contentType: "text/json") {
			if(results){
				for (a in results) {
					element a.assayName
				}
			}
			else
				element ""
		}
	}
}
