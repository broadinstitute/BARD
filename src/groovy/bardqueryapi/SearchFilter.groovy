package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 8/31/12
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchFilter {
    final String filterName
    final String filterValue

    public SearchFilter(final String filterName, final String filterValue) {
        this.filterName = filterName
        this.filterValue = filterValue
    }
}
