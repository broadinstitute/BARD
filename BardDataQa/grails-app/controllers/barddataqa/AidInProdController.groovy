package barddataqa

class AidInProdController {

    FindAidInProdService findAidInProdService

    def index() {
        return [rowList: findAidInProdService.findAllAidsForExperimentsInProduction(), headerList: FindAidInProdService.headerArray]
    }
}
