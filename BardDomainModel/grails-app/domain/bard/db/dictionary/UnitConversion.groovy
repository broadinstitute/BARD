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