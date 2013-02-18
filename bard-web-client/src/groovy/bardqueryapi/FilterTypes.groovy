package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
enum FilterTypes{
    ACTIVE("Only active Assays"),
    TESTED("Tested Assays"),
    SHOW_OTHERS("Show Single Point, Primary, UnMapped Assays"),
    SHOW_DOSE("Show Only Dose")
    //BY_DATA_QUALITY("Filter by Data quality. This should be its own enum. But not ready yet")
    //FIlTER By Annotations
}
