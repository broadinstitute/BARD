package molspreadsheet

import de.andreasschmitt.export.ExportService
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.commons.GrailsApplication
import querycart.QueryCartService
import javax.servlet.http.HttpServletResponse
import bardqueryapi.InetAddressUtil

@Secured(['isFullyAuthenticated()'])
@Mixin(InetAddressUtil)
class MolSpreadSheetController {
    MolecularSpreadSheetService molecularSpreadSheetService
    ExportService exportService
    QueryCartService queryCartService
    GrailsApplication grailsApplication  //inject GrailsApplication
    RetainSpreadsheetService retainSpreadsheetService

    def index() {
        render(view: 'molecularSpreadSheet', model: [transpose: params.transpose, norefresh: params.norefresh] )
    }

    def showExperimentDetails(Long pid, Long cid, Boolean transpose) {
        render(view: 'molecularSpreadSheet', model: [cid: cid, pid: pid, transpose: transpose])
    }

    def molecularSpreadSheet() {
        MolSpreadSheetData molSpreadSheetData
        Boolean transpose = (params.transpose=="true")
        Boolean noRefreshNeeded = (params.norefresh=="true")
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
            if (noRefreshNeeded && (retainSpreadsheetService.molSpreadSheetData!=null)){
                molSpreadSheetData = retainSpreadsheetService.molSpreadSheetData
            } else {
                molSpreadSheetData = molecularSpreadSheetService.retrieveExperimentalDataFromIds(cids, adids, pids)
            }
            retainSpreadsheetService.molSpreadSheetData = molSpreadSheetData
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
                if (transpose) {
                    render(template: 'tSpreadSheet', model: [molSpreadSheetData: molSpreadSheetData])
                } else {
                    render(template: 'spreadSheet', model: [molSpreadSheetData: molSpreadSheetData])
                }
            } else {
                render(template: 'spreadSheet', model: [molSpreadSheetData: new MolSpreadSheetData()])
            }
        } catch (Exception ee) {
            String errorMessage = "Could not generate SpreadSheet for current Query Cart Contents"
            flash.message = errorMessage
            log.error(errorMessage + ". IP:" + getAddressFromRequest(), ee)
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "${flash.message}")
        }

    }

    def list = {
        // eventually we will perform a sort here and then return something useful, but for now we redirect
        redirect(action: 'index')
    }
}