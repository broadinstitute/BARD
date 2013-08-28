package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
enum GroupByTypes {
    ASSAY("Group Results By ADIDs"),
    PROJECT("Group Results By PIDs"),
    COMPOUND("Group Results By CIDs"),
    EXPERIMENT("Group Results By EIDs");
    final String description

    GroupByTypes(String description){
       this.description = description
    }
    String getDescription(){
        return this.description
    }

}
