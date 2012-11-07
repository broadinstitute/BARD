package molspreadsheet

import grails.plugins.springsecurity.Secured
import querycart.CartCompound
import querycart.CartProject
import javax.servlet.http.HttpServletResponse
import querycart.CartCompoundService
import querycart.CartProjectService
import querycart.QueryItem

@Secured(['isFullyAuthenticated()'])
class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService
    CartCompoundService cartCompoundService
    CartProjectService cartProjectService

    def index() {
        render(view: 'molecularSpreadSheet')
    }

    def showExperimentDetails(Long pid, Long cid) {
        //1. Clear the current cart and delete all QueryItems stored in database.
        //2. Add the project and compound to the empty cart.
        //3. Call molecularSpreadSheet

        molecularSpreadSheetService.queryCartService.emptyShoppingCart()
        //QueryItem.where {}.deleteAll() - generates a hibernate error
        QueryItem.list()*.delete()

        CartCompound cartCompound = cartCompoundService.createCartCompoundFromCID(cid)
        CartProject cartProject = cartProjectService.createCartProjectFromPID(pid)
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