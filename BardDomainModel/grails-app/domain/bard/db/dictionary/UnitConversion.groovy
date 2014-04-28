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

package bard.db.dictionary

class UnitConversion implements Serializable {

    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int FORMULA_MAX_SIZE = 256

    Element fromUnit
    Element toUnit

    BigDecimal multiplier
    BigDecimal offset
    String formula

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static constraints = {
        fromUnit()
        toUnit()
        multiplier( nullable: true)
        offset( nullable: true)
        formula( nullable: true, blank: false, maxSize: FORMULA_MAX_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    BigDecimal convert(BigDecimal value){
        BigDecimal result
        if (formula){
            result = evaluateFormula(value)
        }
        else {
            result = (value + (offset?:0)) * (multiplier?:1)
        }
        result
    }

    private evaluateFormula(BigDecimal value) {
        BigDecimal result
        try {
            Binding binding = new Binding()
            binding.setVariable("value", value)
            binding.setVariable("offset", offset?:0)
            binding.setVariable("multiplier", multiplier?:1)
            GroovyShell shell = new GroovyShell(binding);
            result = shell.evaluate(formula)
        } catch (Exception e) {
            log.error("exception when trying to evaluate the formula : $formula with the value: $value", e)
        }
    }

    static mapping = {
        id(column: 'UNIT_CONVERSION_ID', generator: 'sequence', params: [sequence: 'UNIT_CONVERSION_ID_SEQ'])
    }
}
