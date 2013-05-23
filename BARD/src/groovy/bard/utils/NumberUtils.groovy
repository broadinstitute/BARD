package bard.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.commons.lang.StringUtils

class NumberUtils {
	
	private static final Pattern SCIENTIFIC_NOTATION_PATTERN = Pattern.compile("^[-+]?[1-9][0-9]*\\.?[0-9]*([Ee][+-]?[0-9]+)")
	
	private NumberUtils(){}
	
	static BigDecimal convertScientificNotationValue(String value){
		String cleanValue = StringUtils.strip(value)
		if(isScientificNotationValue(value)){
			return new BigDecimal(cleanValue)
		}
		else
			return null
	}
	
	static boolean isScientificNotationValue(String value){
		boolean isScientificNotationvalue = false
		if(value){
			Matcher matcher = SCIENTIFIC_NOTATION_PATTERN.matcher(value);
			isScientificNotationvalue = matcher.matches()
		}
		return isScientificNotationvalue
	}	
}
