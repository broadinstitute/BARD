package bard.db.registration

class AssayJSonController {

    def index() {
		redirect(action: "getNames", params: params)
	}
	
	def getNames() {
		def results
		if(params?.term)
			results = Assay.findAllByAssayShortNameIlike("%${params.term}%", [sort: "assayShortName", order: "asc"])
		else
			results = Assay.list()	
					
		render(contentType: "text/json") {
			if(results){
				for (a in results) {
					element a.assayShortName
				}
			}
			else
				element ""
		}
	}
}
