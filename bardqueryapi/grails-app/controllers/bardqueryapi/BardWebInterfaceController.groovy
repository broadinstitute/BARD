package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
class BardWebInterfaceController {

    QueryAssayApiService queryAssayApiService

    def index() {
        render(view: "index.gsp")
    }

    def findCompoundsForAssay() {
        /**TODO
         * We would like to use the following NCGC parametrized url to hopefully pagination for free from their server:
         * http://assay.nih.gov/bard/rest/v1/assays/757/compounds?offset=100&max=100
         * NGCG limits the number of compounds result to 1000.
         */
        final AssayDisplayType assayDisplayType = AssayDisplayType.Compounds
        if (params.assay) {
            final String assayUrl = "/bard/rest/v1/assays/" + params.assay
            render queryAssayApiService.findCompoundsByAssay(assayUrl, null)
            return
        }
        else {
            render "Assay parameter required"
        }
    }
}
