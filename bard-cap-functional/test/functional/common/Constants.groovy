package common


class Constants {
	//Enumerations
	enum ContextItem{ ADD, UPDATE, DELETE }
	enum ExpectedValueType{ ELEMENT, ONTOLOGY, NUMERIC, FREE, NONE }
	enum DocumentAs{ CONTENTS, URL}
	enum NavigateTo { ASSAY_BY_ID, ASSAY_BY_NAME, PROJECT_BY_ID, PROJECT_BY_NAME }
	enum SearchBy { ASSAY_ID, ASSAY_NAME, PROJECT_ID, PROJECT_NAME }
//	enum ProjectSummaryEdit { STATUS, NAME, DESCRIPTION }

	// constants
	final static int WAIT_INTERVAL = 15;
	final static int R_INTERVAL = 3;
	final static String edited = "-Edited";
	final static String IS_EMPTY = "Empty";
	final static String contextCard = "project management";
	
	final static int index_0 = 0;
	final static int index_1 = 1;
	final static int index_2 = 2;
	final static int index_3 = 3;

	// Dictionary maps
	final static def ValueType_Element = ["AttributeFromDictionary":"repetition throughput","ValueFromDictionary":"multiple repetition"]
	final static def ValueType_FreeText = ["AttributeFromDictionary":"experiment panel name","DiplayValue":"Experiment Panel Name Display"]
	final static def ValueType_NumericValue = ["AttributeFromDictionary":"fold inhibition","Qalifier":"= ", "NumericValue":"320", "Unit":"volume percentage"]
	final static def ValueType_ExternalOntology = ["AttributeFromDictionary":"GO cellular component","IntegratedSearch":"Golgi apparatus", "OntologyId":"320", "DiplayValue":"Matrilysin Complexed"]
	
	final static def ValueType_ElementwithoutElement = ["AttributeFromDictionary":"","ValueFromDictionary":"repetition-point number"]
	final static def ValueType_ElementwithoutValue = ["AttributeFromDictionary":"repetition throughput","ValueFromDictionary":""]
	final static def ValueType_FreeTextwithoutDisplayValue = ["AttributeFromDictionary":"experiment panel name","DiplayValue":""]
	final static def ValueType_NumericValuewithoutNumericValue = ["AttributeFromDictionary":"fold inhibition","Qalifier":"= ", "NumericValue":"", "Unit":"volume percentage"]
	final static def ValueType_ExternalOntologywithoutValues = ["AttributeFromDictionary":"GO cellular component","IntegratedSearch":"", "OntologyId":"", "DiplayValue":""]
	
	final static def ValueType_ElementEdit = ["AttributeFromDictionary":"repetition throughput","ValueFromDictionary":"repetition-point number"]
	final static def ValueType_FreeTextEdit = ["AttributeFromDictionary":"experiment panel name","DiplayValue":"Experiment Panel Name - Edited"]
	final static def ValueType_NumericValueEdit = ["AttributeFromDictionary":"fold inhibition","Qalifier":"<=", "NumericValue":"420", "Unit":"volume percentage"]
	final static def ValueType_ExternalOntologyEdit = ["AttributeFromDictionary":"GO cellular component","IntegratedSearch":"Golgi cis cisterna", "OntologyId":"420", "DiplayValue":"Matrilysin Complexed - Edited"]
	
	
	final static def documentHeader = ["Description":"description","Protocol":"protocol", "Comment":"comment", "Publication":"publication", "Urls":"urls", "Other":"other"]
	final static def documentType = ["Description":"description","Protocol":"protocol", "Comment":"comments", "Publication":"publication", "Urls":"external url", "Other":"other"]
	//Database instances strings
	final static def dbInstance = [qa:"bard-qa", dev:"bard-dev"]
	final static def devDatasource = [ url:"jdbc:oracle:thin:@vmbarddev:1521:barddev", username:"bard_dev", password:"prEWr9safra8ahu", driver:"oracle.jdbc.OracleDriver"]
	final static def qaDatasource = [ url:"jdbc:oracle:thin:@vmbarddev:1521:barddev", username:"bard_qa_cap", password:"Ze3eqe2T", driver:"oracle.jdbc.OracleDriver"]
}
