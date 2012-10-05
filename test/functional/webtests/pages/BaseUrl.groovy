package webtests.pages

/**
 * Created by IntelliJ IDEA.
 * User: balexand
 * Date: 7/13/11
 * Time: 8:32 AM
 * To change this template use File | Settings | File Templates.
 */
class BaseUrl extends ScaffoldPage {
    static url = "/projectdb/"

    static at = {
        assert title == "Welcome to Project Security"
        true
    }

    static content = {
        loginAnchor(to: LoginPage) { $("a", text: "Login") }
    }

}
