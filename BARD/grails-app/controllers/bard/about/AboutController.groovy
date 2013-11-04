package bard.about

class AboutController {

    def index() {
        redirect(controller: "bardWebInterface", action: "redirectToIndex")
    }
    def termsOfUse(){}
    def privacyPolicy(){}
    def aboutBard(){}

    // the "how to" pages
    def howToSearch () {
        render (view:'howToSearch')
    }
    def howToFilterResults() {
        render (view:'howToFilterResults')
    }
    def howToUseSecurely() {
        render (view:'howToUseSecurely')
    }
    def howToReadResults() {
        render (view:'howToReadResults')
    }
    def howToUsePlugins () {
        render (view:'howToUsePlugins')
    }
    def howToReportABug() {
        render (view:'howToReportABug')
    }
    def howToContactUs() {
        render (view:'howToContactUs')
    }

    // some pages accessed through the footer
    def bardHistory() {
        render (view:'footerHistory')
    }
    def bardDevelopmentTeam() {
        render (view:'footerDevelopmentTeam')
    }
    def bardArchitecture() {
        render (view:'footerArchitecture')
    }
    def bardOrganizingPrinciples() {
        render (view:'footerOrganizingPrinciples')
    }

}
