package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
class BardWebInterfaceController {

    QueryService queryService
    def   shoppingCartService


    def index() {
        homePage()
    }

    def example() {
       println 'test'
    }

    def homePage() {
        render(view: "homePage", totalCompounds: 0, model: [assays: [], compounds: [], experiments: [], projects: []])
    }
    /**
     * @return
     */
    def search() {

        def searchString = params.searchString?.trim()
        if (searchString) {
            def result = this.queryService.search(searchString)
            shoppingCartService.emptyShoppingCart()
            def assays = result["assays"]
            for (assay in assays) {
                shoppingCartService.addToShoppingCart(new CartAssay(assayTitle:assay["assayName"]),1)
            }
            def compounds = result["compounds"]
            for (compound in compounds) {
                shoppingCartService.addToShoppingCart(new CartCompound(smiles:compound["smiles"]),1)
            }
            render(view: "homePage", model: result)
            return
        }
        shoppingCartService.addToShoppingCart(new CartAssay(),1)
        def t =  shoppingCartService.findAll()
        flash.message = 'Search String is required'
        redirect(action: "homePage")
    }


    def showCompound(Integer cid) {
        Integer compoundId = cid ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one

        if (compoundId) {
            Map compoundJson = this.queryService.showCompound(compoundId)
            render(view: "showCompound", model: [compoundJson: compoundJson, compoundId: compoundId])
        }
        else {
            render "Compound ID (CID) parameter required"
        }
    }

    def autoCompleteAssayNames() {
        final List<String> assayNames = this.queryService.autoComplete(params?.term)
        render(contentType: "text/json") {
            for (String assayName : assayNames) {
                element assayName
            }
            if (!assayNames) {
                element ""
            }
        }
    }

    /**
     * An Action to provide a search-call to NCGC REST API: find CIDs by structure (SMILES).
     * @param smiles
     * @param structureSearchType
     * @return
     */
    def structureSearch(String smiles, String structureSearchType) {
        final StructureSearchType searchType = structureSearchType as StructureSearchType
        switch (searchType) {
            case StructureSearchType.SUB_STRUCTURE:
            case StructureSearchType.SIMILARITY:
            case StructureSearchType.EXACT_MATCH:
                redirect(action: "search", params: ['searchString': searchType.description + ":" + smiles])
                break
            default:
                throw new RuntimeException("Undeifined structure-search type")
                break
        }


    }

}
