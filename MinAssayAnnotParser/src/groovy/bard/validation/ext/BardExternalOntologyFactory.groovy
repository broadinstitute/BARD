package bard.validation.ext

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 4/2/13
 * Time: 10:40 AM
 * To change this template use File | Settings | File Templates.
 */
/**
 * This is copied from BARD project to look up internal ontology first, then the external default ontology
 */
class BardExternalOntologyFactory implements ExternalOntologyFactory {


    Map<String, ExternalOntologyAPI> customExternalOntologyApiMap = [:]

    final Properties defaultProps = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'default@bard.nih.gov'])

    private final DefaultExternalOntologyFactoryImpl defaultExternalOntologyFactory = new DefaultExternalOntologyFactoryImpl()


    public ExternalOntologyAPI getExternalOntologyAPI(String externalSite) throws ExternalOntologyException {
        return getExternalOntologyAPI(externalSite, defaultProps);
    }



    public ExternalOntologyAPI getExternalOntologyAPI(String externalSite, Properties props) throws ExternalOntologyException {
        ExternalOntologyAPI externalOntologyAPI
        if (customExternalOntologyApiMap.containsKey(externalSite)){
            externalOntologyAPI = customExternalOntologyApiMap.get(externalSite)
        }
        else{
            externalOntologyAPI = defaultExternalOntologyFactory.getExternalOntologyAPI(externalSite, props)
        }
    }
}