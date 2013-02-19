package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
class WebQueryValueModel {
    private final Object value;
    private final Class type;


    public WebQueryValueModel(Object value) {
        this.value = value
        this.type = value.getClass()
    }

    public Object getValue() {
        return this.value
    }


    public Class getType() {
        return this.type
    }

    public String toString(){
        return ""
    }


}
