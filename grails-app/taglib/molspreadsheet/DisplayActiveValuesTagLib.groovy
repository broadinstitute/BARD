package molspreadsheet

import results.ExperimentalValue

class DisplayActiveValuesTagLib {



    def curveDescr = { attrs, body ->
        out << body()
        if (attrs.ac50==null) {
            out << ""
        } else {
            out << "AC50 : ${(new ExperimentalValue(attrs.slope, false)).toString()}  <br/>"
        }
        if (attrs.sInf==null) {
            out << ""
        } else {
            out << "sInf : ${(new ExperimentalValue(attrs.sInf, false)).toString()}  <br/>"
        }
        if (attrs.s0==null) {
            out << ""
        } else {
            out << "s0 : ${(new ExperimentalValue(attrs.s0, false)).toString()}  <br/>"
        }
        if (attrs.slope==null) {
            out << ""
        } else {
            out << "HillSlope : ${(new ExperimentalValue(attrs.slope, false)).toString()}  <br/>"
        }
        out
     }


}
