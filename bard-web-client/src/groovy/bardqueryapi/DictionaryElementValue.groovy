package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
class DictionaryElementValue implements WebQueryValue {

    public String toString(){
        return this.value
    }
    String value //displayName
    Long dictionaryElementId
}
