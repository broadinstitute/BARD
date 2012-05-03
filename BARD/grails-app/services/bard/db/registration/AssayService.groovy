package bard.db.registration

class AssayService {

    Map getMeasureContextItemsForAssay(Assay assay) {
        Map map = [:]
        map.measureContextItems = assay.measureContextItems
        return map
    }
}
