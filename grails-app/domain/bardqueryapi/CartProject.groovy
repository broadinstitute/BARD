package bardqueryapi

import com.metasieve.shoppingcart.Shoppable

class CartProject extends Shoppable {

    String projectName = "no project name yet specified"
    Long projectId = 0L

    static constraints = {
        projectName    blank: false;
    }

    CartProject (String projectName, String projectIdStr)   {
        this.projectName =  projectName
        this.projectId =  Long.parseLong(projectIdStr)
    }

    CartProject (String projectName, Long projectIdStr)   {
        this.projectName =  projectName
        this.projectId =  projectIdStr
    }



    /**
     *
     * @param o
     * @return
     */
    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof CartProject)) return false

        CartProject that = (CartProject) o

        if (projectName != that.projectName) return false

        return true
    }

    /**
     *
     * @return
     */
    int hashCode() {
        return (projectName != null ? projectName.hashCode() : 0)
    }


    @Override
    String toString() {
        projectName
    }


 }
