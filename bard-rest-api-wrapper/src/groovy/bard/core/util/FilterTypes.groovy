package bard.core.util

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
enum FilterTypes {
    TESTED("Tested Assays"),
    SHOW_OTHERS("Show Single Point, Primary, UnMapped Assays"),
    Y_DENORM_AXIS('DeNormalize Y-Axis'),
    SINGLE_POINT_RESULT('Single-point result type');

    String description
    FilterTypes(String description){
      this.description = description
    }
    String getDescription(){
        return this.description
    }
    //BY_DATA_QUALITY("Filter by Data quality. This should be its own enum. But not ready yet")
    //FIlTER By Annotations
}
