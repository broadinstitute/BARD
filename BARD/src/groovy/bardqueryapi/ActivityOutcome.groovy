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

package bardqueryapi

/**
 * Place holder until this appears in the JDO
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 9/27/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
enum ActivityOutcome {

    ALL('All',0),
    /**
     *
     */
    INACTIVE('Inactive',1),
    /**
     *
     */
    ACTIVE('Active',2),

    /**
     *
     */
    INCONCLUSIVE('Inconclusive',3),
    /**
     *
     */
    UNSPECIFIED('Unspecified',4),
    /**
     *
     */
    PROBE('Probe',5);

    static final private Map<Integer,ActivityOutcome> activityOutcomes;
    static{
        activityOutcomes = [:];
        for (ActivityOutcome activityOutcome : ActivityOutcome.values()) {
            activityOutcomes.put(new Integer(activityOutcome.pubChemValue),activityOutcome);
        }
    }
    private final String label;
    private final int pubChemValue;
    private ActivityOutcome(String label, int pubChemValue){
        this.label = label;
        this.pubChemValue = pubChemValue;
    }
    /**
     * @return the name
     */
    String getLabel() {
        return this.label;
    }
    /**
     * @return the pub chem value in the PC schema
     */
    int getPubChemValue() {
        return this.pubChemValue;
    }
    /**
     * @param outComeValue
     * @return {@link ActivityOutcome}
     */
    static ActivityOutcome findActivityOutcome(int outComeValue){

        return activityOutcomes.get(new Integer(outComeValue));
    }
}
