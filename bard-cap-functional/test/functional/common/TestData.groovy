package common

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/10/07
 */
class TestData {

	static def projectId = 1315
	static def assayId = 8129

	final static def documents = [
									documentName:"Document Name",
									documentContent:"this is document contents",
									documentUrl:"https://www.google.com"
		]
	
	final static def contexts = [
		Element:[
			attribute:"repetition throughput",
			dictionaryValue:"multiple repetition",
			dictionaryValueNull:"",
			dictionaryValueEdit:"single repetition"
		],
		FreeText:[
			attribute:"experiment panel name",
			valueDisplay:"Experiment Panel Name Display",
			valueDisplayNull:"",
			valueDisplayEdit:"Experiment Panel Name Display Edit"
			],
		Numeric:[
			attribute:"fold inhibition",
			qualifier:"=",
			numericValue:"320",
			unit:"volume percentage",
			qualifierNull:">",
			numericValueNull:"",
			unitNull:"",
			qualifierEdit:"<=",
			numericValueEdit:"500",
			unitEdit:"volume percentage",
			],
		ExtOntology:[
			attribute:"GO biological process term",
			integratedSearch:"Golgi calcium ion transport",
			ontologyId:"320",
			valueDisplay:"Matrilysin Complexed",
			integratedSearchNull:"",
			ontologyIdNull:"",
			valueDisplayNull:"",
			integratedSearchEdit:"Golgi cell precursor proliferation",
			ontologyIdEdit:"420",
			valueDisplayEdit:"Matrilysin Complexed",
			]
	]
//	final static def ValueType_Element = [
//											AttributeFromDictionary:"repetition throughput",
//											ValueFromDictionary:"multiple repetition"
//											]
//	final static def ValueType_FreeText = [
//											AttributeFromDictionary:"experiment panel name",
//											DiplayValue:"Experiment Panel Name Display"
//											]
//	final static def ValueType_NumericValue = [
//												AttributeFromDictionary:"fold inhibition",
//												Qalifier:"= ",
//												NumericValue:"320",
//												Unit:"volume percentage"
//												]
//	final static def ValueType_ExternalOntology = [
//													AttributeFromDictionary:"GO biological process term",
//													IntegratedSearch:"Golgi calcium ion transport",
//													OntologyId:"320",
//													DiplayValue:"Matrilysin Complexed"
//													]
//	
//	final static def ValueType_ElementwithoutElement = [
//														AttributeFromDictionary:"",
//														ValueFromDictionary:"repetition-point number"
//														]
//	final static def ValueType_ElementwithoutValue = [
//														AttributeFromDictionary:"repetition throughput",
//														ValueFromDictionary:""
//														]
//	final static def ValueType_FreeTextwithoutDisplayValue = [
//																AttributeFromDictionary:"experiment panel name",
//																DiplayValue:""
//																]
//	final static def ValueType_NumericValuewithoutNumericValue = [
//																	AttributeFromDictionary:"fold inhibition",
//																	Qalifier:"= ",
//																	NumericValue:"",
//																	Unit:"volume percentage"
//																	]
//	final static def ValueType_ExternalOntologywithoutValues = [
//																AttributeFromDictionary:"GO biological process term",
//																IntegratedSearch:"",
//																OntologyId:"",
//																DiplayValue:""
//																]
//	
//	final static def ValueType_ElementEdit = [
//												AttributeFromDictionary:"repetition throughput",
//												ValueFromDictionary:"repetition-point number"
//												]
//	final static def ValueType_FreeTextEdit = [
//												AttributeFromDictionary:"experiment panel name",
//												DiplayValue:"Experiment Panel Name - Edited"
//												]
//	final static def ValueType_NumericValueEdit = [
//													AttributeFromDictionary:"fold inhibition",
//													Qalifier:"<=",
//													NumericValue:"420",
//													Unit:"volume percentage"
//													]
//	final static def ValueType_ExternalOntologyEdit = [
//														AttributeFromDictionary:"GO biological process term",
//														IntegratedSearch:"Golgi cell precursor proliferation",
//														OntologyId:"420",
//														DiplayValue:"Matrilysin Complexed - Edited"
//														]

}
