package common

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/10/07
 */
class TestData {

	final static def projectId = 1315
	final static def assayId = 8129
	final static int experimentId = 1552
	final static int panelId = 6

	final static String edited = "-Edited";
	final static int WAIT_INTERVAL = 30;
	final static int R_INTERVAL = 0.5;
	//	final static String IS_EMPTY = "Empty";
	final static def documentHeader = [Description:"description",Protocol:"protocol",Comment:"comment",Publication:"publication",Urls:"urls",Other:"other"]
	final static def documentType = [Description:"description",Protocol:"protocol",Comment:"comments",Publication:"publication",Urls:"external url",Other:"other"]

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
			integratedSearchEdit:"Golgi calcium ion homeostasis",
			ontologyIdEdit:"420",
			valueDisplayEdit:"Matrilysin Complexed",
		]
	]

	final static Map addPanel = [
		name:"Test Panel Name",
		description:"Test Panel Description",
		owner:"BARD Administrator"
	]

	final static Map createProject = [
		name:"Test Project Name",
		description:"Test Project Description",
		status:"Draft",
		group:"Project",
		owner:"BARD Administrator"
	]

	final static Map createAssay = [
		name:"Test Assay Name",
		definitionType:"Regular",
		owner:"BARD Administrator"
	]

	final static Map createExperiment = [
		name:"Test Experiment Name",
		description:"Test Experiment Description",
		owner:"BARD Administrator"
	]

	final static Map rundate = [
		From:[
			day:'3',
			month:'Mar',
			year:'2012'
		],
		To:[
			day:'3',
			month:'Mar',
			year:'2014'
		]
	]

	final static Map resultType = [
		parent:[
			resultType:'PubChem activity score',
			statsModifier:'geometric mean'
		],
		editParent:[
			resultType:'brain penetrance',
			statsModifier:'minimum'
		],
		child:[
			resultType:'categorical result',
			statsModifier:'relative',
			parentResultType:"PubChem activity score",	//this should be the same as resultType value of parent map
			relationship:'supported by'
		],
		dose:[
			doseResultType:'active concentration',
			responseResultType:'percent activity',
			statsModifier:'median'
		],
		editDose:[
			doseResultType:'cytostatic concentration',
			responseResultType:'percent activity',
			statsModifier:'maximum'
		]
	]
	final static def assaysToPanel = [4405, 1730, 2529]


}
