package main.groovy.common

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class Constants {
	//Enumerations
	enum ContextItem{ ADD, UPDATE, DELETE }
	enum ExpectedValueType{ ELEMENT, ONTOLOGY, NUMERIC, FREE, NONE }
	enum DocumentAs{ CONTENTS, URL}
	enum NavigateTo { ASSAY_BY_ID, ASSAY_BY_NAME, PROJECT_BY_ID, PROJECT_BY_NAME }
	enum SearchBy { ASSAY_ID, ASSAY_NAME, PROJECT_ID, PROJECT_NAME }

	// constants
	final static int WAIT_INTERVAL = 30;
	final static int R_INTERVAL = 0.5;
	final static String edited = "-Edited";
	final static String IS_EMPTY = "Empty";
	
	final static int index_0 = 0;
	final static int index_1 = 1;
	final static int index_2 = 2;
	final static int index_3 = 3;

	final static def ValueConstraints = [Free:"Free",List:"List", Range:"Range"]
	
	final static def documentHeader = [Description:"description",Protocol:"protocol",Comment:"comment",Publication:"publication",Urls:"urls",Other:"other"]
	final static def documentType = [Description:"description",Protocol:"protocol",Comment:"comments",Publication:"publication",Urls:"external url",Other:"other"]

	//Database instances strings
	final static def devDatasource = [url:"jdbc:oracle:thin:@barddev:1521:barddev", username:"bard_dev", password:"prEWr9safra8ahu", driver:"oracle.jdbc.OracleDriver"]
	final static def qaDatasource = [url:"jdbc:oracle:thin:@barddev:1521:barddev", username:"bard_qa_cap", password:"Ze3eqe2T", driver:"oracle.jdbc.OracleDriver"]
}
