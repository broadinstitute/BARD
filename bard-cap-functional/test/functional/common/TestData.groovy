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
}
