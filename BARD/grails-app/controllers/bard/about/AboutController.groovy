package bard.about

class AboutController {

    def index() {
        redirect(controller: "bardWebInterface", action: "redirectToIndex")
    }
    def termsOfUse(){}
    def privacyPolicy(){}
}
