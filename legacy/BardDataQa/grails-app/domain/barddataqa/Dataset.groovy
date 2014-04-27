package barddataqa

class Dataset {
    String name
    String description
    Integer loadOrder

    static constraints = {
    }

    static mapping = {
        table "bard_data_qa_dashboard.dataset"
        loadOrder column:  'load_order'
    }
}
