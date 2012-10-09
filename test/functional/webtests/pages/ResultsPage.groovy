package webtests.pages

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/9/12
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
class ResultsPage extends HomePage {
    static at = { $("#assaysTab") }

    static content = {
        assaysTab(required: true) { $("#assaysTab") }
        compoundsTab(required: true) { $("#compoundsTab") }
        projectsTab(required: true) { $("#projectsTab") }
    }
}
