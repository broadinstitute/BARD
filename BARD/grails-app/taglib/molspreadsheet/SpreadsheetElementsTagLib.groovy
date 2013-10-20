package molspreadsheet

import bard.core.rest.spring.experiment.ActivityData
import bardqueryapi.JavaScriptUtility

class SpreadsheetElementsTagLib {

    def imageCell = { attrs, body ->
        if (attrs.smiles == 'Unknown smiles') {
            out << attrs.smiles
        } else {
            out << render(template: "/tagLibTemplates/compoundOptions", model: [cid: attrs.cid,
                    smiles: attrs.smiles, sid: attrs.sid,
                    imageWidth: "150", imageHeight: "120"])
        }
    }

    def cidCell = { attrs, body ->
        out << """<a  class="molspreadcell" href="${this.createLink(controller: 'bardWebInterface', action: 'showCompound', id: attrs.cid)}">${attrs.cid}</a>"""
    }


    def promiscuityCell = { attrs, body ->
        out << """<div class="promiscuity"
                     href="${this.createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: attrs.cid])}"
                     id="${attrs.cid}_prom"></div>"""
    }

    def activeVrsTestedCell = { attrs, body ->
        String[] activeVersusActivePill = attrs.activeVrsTested.split()

        if (activeVersusActivePill.size() == 3) {   // our incoming string should always have three elements, but run a test to be sure
            out << """<div>
                      <span class="badge badge-info">
                          <a   href="${this.createLink(controller: 'molSpreadSheet', action: 'showExperimentDetails', params: [cid: attrs.cid, transpose: "true"])}" style="color: white; text-decoration: underline" >${activeVersusActivePill[0]}</a>
                          / ${activeVersusActivePill[2]}
                  </div>"""

        } else {
            out << """<div>
                      <span class="badge badge-info">${attrs.activeVrsTested}</span>
                  </div>"""

        }
    }


    def exptDataCell = { attrs, body ->
        List<MolSpreadSheetColumnHeader> mssHeaders = attrs.mssHeaders
        MolSpreadSheetData molSpreadSheetData = attrs.molSpreadSheetData
        SpreadSheetActivityStorage spreadSheetActivityStorage = attrs.spreadSheetActivityStorage
        int columnNumber = 0
        Double yMinimum = Double.NaN
        Double yMaximum = Double.NaN
        // first let's look for any minimums and maximums for Y normalization
        int currentCol = attrs.colCnt
        if (spreadSheetActivityStorage != null) {
            if (molSpreadSheetData?.columnPointer?.containsKey(spreadSheetActivityStorage.eid)) {
                columnNumber = molSpreadSheetData.columnPointer[spreadSheetActivityStorage.eid]
            }
            if ((columnNumber >= 0) && (columnNumber < mssHeaders?.size())) {
                MolSpreadSheetColumnHeader molSpreadSheetColumnHeader = attrs.mssHeaders[columnNumber + 4]
                if (molSpreadSheetColumnHeader.molSpreadSheetColSubHeaderList?.size() > 0) {
                    // note: if (molSpreadSheetColumnHeader.molSpreadSheetColSubHeaderList?.size()>1) then we work across multiple expts
                    for (MolSpreadSheetColSubHeader molSpreadSheetColSubHeader in molSpreadSheetColumnHeader.molSpreadSheetColSubHeaderList) {
                        if (molSpreadSheetColSubHeader.minimumResponse != Double.NaN) {
                            if ((yMinimum == Double.NaN) ||
                                    (molSpreadSheetColSubHeader.minimumResponse < yMinimum)) {
                                yMinimum = molSpreadSheetColSubHeader.minimumResponse
                            }
                        }
                        if (molSpreadSheetColSubHeader.maximumResponse != Double.NaN) {
                            if ((yMaximum == Double.NaN) ||
                                    (molSpreadSheetColSubHeader.maximumResponse > yMaximum)) {
                                yMaximum = molSpreadSheetColSubHeader.maximumResponse
                            }

                        }
                    }
                }

            }
            // figure out normalization status
            Boolean normalizeColumn = true
            if (molSpreadSheetData != null) {
                String assayId = molSpreadSheetData.experimentNameList[columnNumber]
                if (molSpreadSheetData.mapColumnsNormalization.containsKey(assayId)) {
                    normalizeColumn = molSpreadSheetData.mapColumnsNormalization[assayId]
                }
            }

//            HillCurveValueHolder hillCurveValueHolder = spreadSheetActivityStorage.getHillCurveValueHolderList()[0]
            out << """<td class="molSpreadSheet" property="var${currentCol}">
                      <p>"""
            Boolean weHaveACurveToDisplay = ""
            for (HillCurveValueHolder hillCurveValueHolder in spreadSheetActivityStorage.getHillCurveValueHolderList()) {
                if (hillCurveValueHolder?.conc?.size() > 1) {
                    weHaveACurveToDisplay = true
                }
            }
            MolSpreadSheetCellActivityOutcome molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.newMolSpreadSheetCellActivityOutcome(spreadSheetActivityStorage.activityOutcome)
            final List<HillCurveValueHolder> hillCurveValueHolderList = spreadSheetActivityStorage.getHillCurveValueHolderList()

            String childElements = ""
            if (spreadSheetActivityStorage.childElements?.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder()
                for (ActivityData childElement in spreadSheetActivityStorage.childElements) {
                    if (childElement?.toDisplay()) {
                        stringBuilder.append("<nobr>${childElement.toDisplay()}</nobr><br />")
                    }
                }
                if(hillCurveValueHolderList){
                //We want to display the hill slope so grab the first value in the list
                    HillCurveValueHolder hillCurveValueHolder = hillCurveValueHolderList.get(0)
                    final double hillSlope = hillCurveValueHolder.coef
                    if(hillSlope){
                        String display = "Hill Slope: ${hillSlope}"
                        stringBuilder.append("<nobr>${display}</nobr><br />")
                    }

                }
                childElements = stringBuilder.toString()
            }

            for (HillCurveValueHolder hillCurveValueHolder in hillCurveValueHolderList) {
                if (hillCurveValueHolder?.identifier) {
                    String resultValueHolder = hillCurveValueHolder.toString()
                    out << """<div data-detail-id="drc_${spreadSheetActivityStorage.sid}_${currentCol}" """
                    if (weHaveACurveToDisplay) {
                        out << """     class="drc-popover-link molspreadcellunderline" """
                    } else {
                        out << """     class="molspreadcell" """
                    }
                    out << """     data-original-title="${hillCurveValueHolder.identifier}"
                               data-html="true"
                               data-trigger="hover">"""
                    if (childElements?.length() > 0) {
                        out << """<div
                           rel="tooltip"
                           data-container="body"
                           data-html="true"
                           data-original-title="${JavaScriptUtility.cleanupForHTML(childElements.toString())}"
                           data-trigger="hover">"""
                    }
                    out << """<FONT COLOR="${molSpreadSheetCellActivityOutcome.color}"><nobr>${resultValueHolder} ${spreadSheetActivityStorage.printUnits(resultValueHolder)}</nobr></FONT>"""
                    if (childElements?.length() > 0) {
                        out << """</div>"""
                    }
                    out << """</div>"""
                }
            }
            out << """</p>"""
            if (weHaveACurveToDisplay) {
                out << """<div class='popover-content-wrapper'
                              id="drc_${spreadSheetActivityStorage.sid}_${currentCol}"
                              style="display: none;">
                              <div class="center-aligned">
                                   <img alt="${spreadSheetActivityStorage.sid}"
                                        title="Substance Id : ${spreadSheetActivityStorage.sid}"
                                        src="""
                int curveNumber = 0
                Map combinedParameters = [:]
                Boolean xAxisLabelSpecified = false
                Boolean yAxisLabelSpecified = false
                Boolean xMinimumSpecified = false
                Boolean xMaximumSpecified = false
                Boolean yNormMinimumSpecified = false
                Boolean yNormMaximumSpecified = false
                for (HillCurveValueHolder hillCurveValueHolder in spreadSheetActivityStorage.getHillCurveValueHolderList()) {
                    combinedParameters['curves[' + curveNumber + '].sinf'] = hillCurveValueHolder.sInf
                    combinedParameters['curves[' + curveNumber + '].s0'] = hillCurveValueHolder.s0
                    combinedParameters['curves[' + curveNumber + '].slope'] = hillCurveValueHolder.slope
                    combinedParameters['curves[' + curveNumber + '].hillSlope'] = hillCurveValueHolder.coef
                    combinedParameters['curves[' + curveNumber + '].concentrations'] = hillCurveValueHolder.conc
                    combinedParameters['curves[' + curveNumber + '].activities'] = hillCurveValueHolder.response
                    if ((!xAxisLabelSpecified) && (hillCurveValueHolder.xAxisLabel)) {
                        combinedParameters['xAxisLabel'] = hillCurveValueHolder.xAxisLabel
                        xAxisLabelSpecified = true
                    }
                    if ((!yAxisLabelSpecified) && (hillCurveValueHolder.yAxisLabel)) {
                        combinedParameters['yAxisLabel'] = hillCurveValueHolder.yAxisLabel
                        yAxisLabelSpecified = true
                    }
                    if ((yMinimum != Double.NaN) &&
                            (yMaximum != Double.NaN)) {
                        if (normalizeColumn) {
                            if ((!yNormMinimumSpecified) && (yMinimum != null)) {
                                combinedParameters['yNormMin'] = yMinimum
                                yNormMinimumSpecified = true
                            }
                            if ((!yNormMaximumSpecified) && (yMaximum != null)) {
                                combinedParameters['yNormMax'] = yMaximum
                                yNormMaximumSpecified = true
                            }

                        }
                    }
                    curveNumber++
                }
                out << """ "${
                    this.createLink(
                            controller: 'doseResponseCurve',
                            action: 'doseResponseCurve',
                            params: combinedParameters
                    )
                }"
                        """

                out << """/>
                               </div>
                           </div>
                        """

            }
        } else {
            out << """<td class="molSpreadSheet" property="var${currentCol}">
                          <div rel="tooltip" data-original-title="Not tested in this experiment" class="molspreadcell">
                          <font color="${MolSpreadSheetCellActivityOutcome.Unspecified.color}">N.T.</font></div>
                      </td>"""
        }


    }
}
