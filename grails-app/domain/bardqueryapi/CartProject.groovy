package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class CartProject extends Shoppable {
    Boolean projectNameWasTruncated = false
    String projectName = 'no project name yet specified'
    Long projectId = 0L
    static int MAXIMUM_PROJECT_NAME_FIELD_LENGTH = 4000


    static constraints = {
        projectName(blank: false, maxSize: MAXIMUM_PROJECT_NAME_FIELD_LENGTH)
    }
    CartProject(){

    }
    CartProject(String projectName, String projectIdStr) {
        this.projectName = projectName
        this.projectId = Long.parseLong(projectIdStr)
    }

    CartProject(String projectName, Long projectIdStr) {
        this.projectName = projectName
        this.projectId = projectIdStr
    }

    @Override
    String toString() {
        String trimmedName = projectName?.trim()
        if (StringUtils.isBlank(projectName) || ("null" == trimmedName)) {
            return ""
        }
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder << trimmedName

        // if name was truncated, then add in ellipses
        if (projectNameWasTruncated) {
            stringBuilder <<= "..."
        }
        stringBuilder.toString()
    }



    void setProjectName(String projectName) {
        if (StringUtils.isNotBlank(projectName)) {
            Integer lengthOfName = projectName.length()
            if (lengthOfName > MAXIMUM_PROJECT_NAME_FIELD_LENGTH) {
                lengthOfName = MAXIMUM_PROJECT_NAME_FIELD_LENGTH
                projectNameWasTruncated = true
            }
            this.projectName = projectName.substring(0, lengthOfName)
        }
        else{
            this.projectName = projectName
        }
    }
    /**
     *  equals
     * @param o
     * @return
     */
    boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        CartProject that = (CartProject) obj;
        return new EqualsBuilder().
                append(this.projectId, that.projectId).
                append(this.projectName, that.projectName).
                isEquals();
    }

    /**
     *  hashCode
     * @return
     */
    int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(this.projectId).
                append(this.projectName).
                toHashCode();
    }

}
