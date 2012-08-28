package bardqueryapi

import com.metasieve.shoppingcart.Shoppable

class CartProject extends Shoppable {

    String projectName = "no project name yet specified"

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof CartProject)) return false

        CartProject that = (CartProject) o

        if (projectName != that.projectName) return false

        return true
    }

    int hashCode() {
        return (projectName != null ? projectName.hashCode() : 0)
    }

    @Override
    String toString() {
        projectName
    }


    static constraints = {
        projectName  :  blank: false;
    }
}
