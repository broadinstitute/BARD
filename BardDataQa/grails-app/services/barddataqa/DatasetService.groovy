package barddataqa

class DatasetService {
    List<Dataset> findAllDatasetsOrderedByName() {
        return Dataset.findAll([sort: "name"])
    }
}
