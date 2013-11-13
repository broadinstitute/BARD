package bardqueryapi

import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
class StringValue implements WebQueryValue, Comparable<StringValue> {

    public String toString(){
        return this.value
    }
    String value =""

    int compareTo(StringValue that) {
        return this.value.compareTo(that.value)
    }
    boolean equals(Object other) {
        if (other instanceof StringValue) {
            StringValue that = (StringValue) other
            return that?.value == this?.value
        }
        return false
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        return builder.append(this.value).toHashCode()
    }
}
