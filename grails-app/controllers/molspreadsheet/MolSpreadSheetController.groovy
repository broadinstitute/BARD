package molspreadsheet

import grails.plugins.springsecurity.Secured
import querycart.CartCompound
import querycart.CartProject
import javax.servlet.http.HttpServletResponse

@Secured(['isFullyAuthenticated()'])
class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService

    def index() {
        render(view: 'molecularSpreadSheet')
    }

    def showExperimentDetails(Long pid, Long cid) {
        //1. Clear the current cart.
        //2. Add the project and compound to the empty cart.
        //3. Call molecularSpreadSheet

        molecularSpreadSheetService.queryCartService.emptyShoppingCart()

        CartCompound cartCompound = new CartCompound(cid)
        CartProject cartProject = new CartProject(pid)
        if (!(cartCompound && cartProject)) {
            return response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED)
        }

        molecularSpreadSheetService.queryCartService.addToShoppingCart(cartCompound)
        molecularSpreadSheetService.queryCartService.addToShoppingCart(cartProject)

        render(view: 'molecularSpreadSheet')
    }

    def molecularSpreadSheet() {
        if (molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()) {
            MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()
            render(template: 'spreadSheet', model: [molSpreadSheetData: molSpreadSheetData])
        } else {
            render(template: 'spreadSheet', model: [molSpreadSheetData: new MolSpreadSheetData()])
        }

    }

    def list = {
        // eventually we will perform a sort here and then return something useful, but for now we redirect
        redirect(action: 'index')
    }
}