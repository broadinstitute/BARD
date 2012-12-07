package molspreadsheet

import de.andreasschmitt.export.ExportService
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.commons.GrailsApplication
import querycart.QueryCartService

@Secured(['isFullyAuthenticated()'])
class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService
    ExportService exportService
    QueryCartService queryCartService
    GrailsApplication grailsApplication  //inject GrailsApplication

    def index() {
        render(view: 'molecularSpreadSheet')
    }

    def showExperimentDetails(Long pid, Long cid) {
        render(view: 'molecularSpreadSheet', model: [cid: cid, pid: pid])
    }

    def molecularSpreadSheet() {
        MolSpreadSheetData molSpreadSheetData
        try {
            List<Long> cids = []
            List<Long> pids = []
            List<Long> adids = []
            if (params.cid || params.pid || params.adid) {
                cids = params.cid ? [new Long(params.cid)] : []
                pids = params.pid ? [new Long(params.pid)] : []
                adids = params.adid ? [new Long(params.adid)] : []
            }
            else if (queryCartService.weHaveEnoughDataToMakeASpreadsheet()) {
                cids = queryCartService.retrieveCartCompoundIdsFromShoppingCart()
                pids = queryCartService.retrieveCartProjectIdsFromShoppingCart()
                adids = queryCartService.retrieveCartAssayIdsFromShoppingCart()
            }
            molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalDataFromIds(cids, adids, pids)
            if (molSpreadSheetData) {
                if (params?.format && params.format != "html") {
                    response.contentType = grailsApplication.config.grails.mime.types[params.format]
                    response.setHeader("Content-disposition", "attachment; filename=molecularSpreadSheet.${params.extension}")
                    LinkedHashMap<String, Object> map = molecularSpreadSheetService.prepareForExport(molSpreadSheetData)
                    exportService.export(params.format, response.outputStream, map["data"], map["fields"], map["labels"], [:], [:])
                    return
                }
                if (molSpreadSheetData.molSpreadsheetDerivedMethod == MolSpreadsheetDerivedMethod.Compounds_NoAssays_NoProjects) {
                    flash.message = message(code: 'show.only.active.compounds', default:
                            "Please note: Only active compounds are shown in the Molecular Spreadsheet")
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