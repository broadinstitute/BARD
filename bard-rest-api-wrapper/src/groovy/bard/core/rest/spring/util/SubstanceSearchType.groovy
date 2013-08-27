package bard.core.rest.spring.util
enum SubstanceSearchType {
    MLSMR("filter=MLSMR"),
    MLSMR_DEP_REGID("filter=MLSMR[dep_regid]"),
    MLSMR_SOURCE_NAME("filter=MLSMR[source_name]")
    private String filter

    SubstanceSearchType(String filter) {
        this.filter = filter
    }

    String getFilter() {
        return this.filter
    }
}
