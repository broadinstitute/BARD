/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package molspreadsheet

import bardqueryapi.ActivityOutcome

class  MolSpreadSheetCellHelper {

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException numberFormatException)
        {
            return false;
        }
        return true;
    }
}

enum MolSpreadSheetCellType {
    lessThanNumeric,
    greaterThanNumeric,
    percentageNumeric,
    numeric,
    identifier,
    image,
    string,
    unknown,
    unhandled;
}





enum MolSpreadSheetCellActivityOutcome {
    Active("Active", "#F62217"),   //rgb(246, 34, 23) [red]
    Inactive("Inactive", "#157DEC"),  //rgb(21, 125, 236)  [blue]
    Inconclusive("Inconclusive", "#E9AB17"), //rgb(233, 171, 23)  [yellow]
    Unspecified("Unspecified", "#736F6E"), //rgb(115, 111, 110)  [gray]
    Probe("Probe", "#4CC417"),  //  rgb(76, 196, 23) [green]
    Unknown("Uninitialized", "#000000")  ;//We should never use this. This is for exception handling and allows us to test all branches of the code

    private String name
    public String color

    public static MolSpreadSheetCellActivityOutcome newMolSpreadSheetCellActivityOutcome(ActivityOutcome activityOutcome) {
        MolSpreadSheetCellActivityOutcome molSpreadSheetCellActivityOutcome
        switch ( activityOutcome ){
            case ActivityOutcome.ACTIVE:
                molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.Active
                break
            case ActivityOutcome.INACTIVE:
                molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.Inactive
                break
            case ActivityOutcome.INCONCLUSIVE:
                molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.Inconclusive
                break
            case ActivityOutcome.UNSPECIFIED:
                molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.Unspecified
                break
            case ActivityOutcome.PROBE:
                molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.Probe
                break
            default:
                molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.Unknown
        }
        return molSpreadSheetCellActivityOutcome
    }


    MolSpreadSheetCellActivityOutcome(String name,String color) {
        this.name = name;
        this.color = color;
    }

    public String toString() {
        return name;
    }
}





enum MolSpreadSheetCellUnit {
    Molar("M", 0),
    Millimolar("mM", 3),
    Micromolar("uM", 6),
    Nanomolar("nM", 9),
    Picomolar("pM", 12),
    Femtomolar("fM", 15),
    Attamolar("aM", 18),
    Zeptomolar("zM", 21),
    Yoctomolar("yM", 24),
    unknown("U", 0),
    unhandled("Unhandled Exception",-5)  ;//We should never use this. This is for exception handling and allows us to test all branches of the code

    private String value
    private  int decimalPlacesFromMolar

    MolSpreadSheetCellUnit(String value,int decimalPlacesFromMolar) {
        this.value = value;
        this.decimalPlacesFromMolar = decimalPlacesFromMolar;
    }

    public String toString() {
        return value;
    }
}
