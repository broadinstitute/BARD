package iteration_038

databaseChangeLog = {

    changeSet(author: "gwalzer", id: "iteration-038/remove-content-column-from-bard-news-table", context: "standard") {
        dropColumn(columnName: "content", tableName: "bard_news") {}
    }
}
