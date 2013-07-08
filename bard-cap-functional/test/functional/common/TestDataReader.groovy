package common

class TestDataReader {
	public static def getTestData(){
		def testData = [:]
		Properties props = new Properties()
		File propsFile = new File('test/resources/bard-cap-config.properties')
		props.load(propsFile.newDataInputStream())
		props.propertyNames().each { prop ->
			testData[prop]=props.getProperty(prop)
		}
		return testData
	}

}
