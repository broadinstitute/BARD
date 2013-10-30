package common

/**
 * @author Muhammad.Rafique
 * Date Created: 13/10/07
 * Last Updated: 13/10/07
 */
class TestData {

	static def projectId = 1315
	static def assayId = 8129

	final static def documents = [
									documentName:"Document Name",
									documentContent:"this is document contents",
									documentUrl:"https://www.google.com"
		]
	final static def ValueType_Element = [
											AttributeFromDictionary:"repetition throughput",
											ValueFromDictionary:"multiple repetition"
											]
	final static def ValueType_FreeText = [
											AttributeFromDictionary:"experiment panel name",
											DiplayValue:"Experiment Panel Name Display"
											]
	final static def ValueType_NumericValue = [
												AttributeFromDictionary:"fold inhibition",
												Qalifier:"= ",
												NumericValue:"320",
												Unit:"volume percentage"
												]
	final static def ValueType_ExternalOntology = [
													AttributeFromDictionary:"GO biological process term",
													IntegratedSearch:"Golgi calcium ion transport",
													OntologyId:"320",
													DiplayValue:"Matrilysin Complexed"
													]
	
	final static def ValueType_ElementwithoutElement = [
														AttributeFromDictionary:"",
														ValueFromDictionary:"repetition-point number"
														]
	final static def ValueType_ElementwithoutValue = [
														AttributeFromDictionary:"repetition throughput",
														ValueFromDictionary:""
														]
	final static def ValueType_FreeTextwithoutDisplayValue = [
																AttributeFromDictionary:"experiment panel name",
																DiplayValue:""
																]
	final static def ValueType_NumericValuewithoutNumericValue = [
																	AttributeFromDictionary:"fold inhibition",
																	Qalifier:"= ",
																	NumericValue:"",
																	Unit:"volume percentage"
																	]
	final static def ValueType_ExternalOntologywithoutValues = [
																AttributeFromDictionary:"GO biological process term",
																IntegratedSearch:"",
																OntologyId:"",
																DiplayValue:""
																]
	
	final static def ValueType_ElementEdit = [
												AttributeFromDictionary:"repetition throughput",
												ValueFromDictionary:"repetition-point number"
												]
	final static def ValueType_FreeTextEdit = [
												AttributeFromDictionary:"experiment panel name",
												DiplayValue:"Experiment Panel Name - Edited"
												]
	final static def ValueType_NumericValueEdit = [
													AttributeFromDictionary:"fold inhibition",
													Qalifier:"<=",
													NumericValue:"420",
													Unit:"volume percentage"
													]
	final static def ValueType_ExternalOntologyEdit = [
														AttributeFromDictionary:"GO biological process term",
														IntegratedSearch:"Golgi cell precursor proliferation",
														OntologyId:"420",
														DiplayValue:"Matrilysin Complexed - Edited"
														]

}
