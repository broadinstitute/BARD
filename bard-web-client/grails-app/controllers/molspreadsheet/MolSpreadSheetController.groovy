package molspreadsheet

import grails.plugins.springsecurity.Secured

import javax.servlet.http.HttpServletResponse

import querycart.*

@Secured(['isFullyAuthenticated()'])
class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService
    CartCompoundService cartCompoundService
    CartProjectService cartProjectService
    def exportService
    def grailsApplication  //inject GrailsApplication

    def index() {
        render(view: 'molecularSpreadSheet')
    }

    def showExperimentDetails(Long pid, Long cid) {
        try {
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
        } catch (Exception ee) {
            log.error("Could not generate SpreadSheet for Project : ${pid} and Compound : ${cid} ", ee)
        }
    }

    def molecularSpreadSheet() {
        try {
            if (molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()) {
                MolSpreadSheetData molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalData()
                if (params?.format && params.format != "html") {
                    response.contentType = grailsApplication.config.grails.mime.types[params.format]
                    response.setHeader("Content-disposition", "attachment; filename=molecularSpreadSheet.${params.extension}")
                    LinkedHashMap<String, Object> map = molecularSpreadSheetService.prepareForExport(molSpreadSheetData)
                    exportService.export(params.format, response.outputStream, map["data"], map["fields"], map["labels"], [:], [:])
                }
                if (molSpreadSheetData.molSpreadsheetDerivedMethod == MolSpreadsheetDerivedMethod.Compounds_NoAssays_NoProjects) {
                    flash.message = message(code: 'show.only.active.compounds', default: "Please note: Only active compounds are shown in the Molecular Spreadsheet")
                }
                render(template: 'spreadSheet', model: [molSpreadSheetData: molSpreadSheetData])
            } else {
                render(template: 'spreadSheet', model: [molSpreadSheetData: new MolSpreadSheetData()])
            }
        } catch (Exception ee) {
            String errorMessage = "Could not generate SpreadSheet for current Query Cart Contents "
            flash.message = errorMessage
            log.error(errorMessage, ee)
        }

    }

    def list = {
        // eventually we will perform a sort here and then return something useful, but for now we redirect
        redirect(action: 'index')
    }
}