package bardqueryapi

import bard.core.adapter.ProjectAdapter

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectValue implements WebQueryValue {

    public String toString(){
        return this.value
    }

    ProjectAdapter value
}
