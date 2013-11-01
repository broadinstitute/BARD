package bard.about

class AboutController {

    def index() {
        redirect(controller: "bardWebInterface", action: "redirectToIndex")
    }
    def termsOfUse(){}
    def privacyPolicy(){}
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
