package bard.validation.ext

import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException
import bard.validation.ext.ExternalOntologyFactory
import groovy.transform.TypeChecked

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/19/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
class BardExternalOntologyFactory  {
//    Map<String, ExternalOntologyAPI> customExternalOntologyApiMap = [:]
    final static String NCBI_TOOL =                                                ExternalOntologyNCBI.NCBI_TOOL;
    final static String NCBI_EMAIL =                                                ExternalOntologyNCBI.NCBI_EMAIL;

    final Properties defaultProps = new Properties([(ExternalOntologyNCBI.NCBI_TOOL): 'bard', (ExternalOntologyNCBI.NCBI_EMAIL): 'default@bard.nih.gov'])

    private final RegisteringExternalOntologyFactory defaultExternalOntologyFactory = RegisteringExternalOntologyFactory.getInstance()

    public ExternalOntologyAPI getExternalOntologyAPI(String externalSite) throws ExternalOntologyException {
        return getExternalOntologyAPI(externalSite, defaultProps);
    }

    public ExternalOntologyAPI getExternalOntologyAPI(String externalSite, Properties props) throws ExternalOntologyException {
//        ExternalOntologyAPI externalOntologyAPI
//        if (customExternalOntologyApiMap.containsKey(externalSite)){
//            externalOntologyAPI = customExternalOntologyApiMap.get(externalSite)
//        }
//        else{
//            externalOntologyAPI = defaultExternalOntologyFactory.getExternalOntologyAPI(externalSite, props)
//        }

        return defaultExternalOntologyFactory.getExternalOntologyAPI(externalSite, props)
    }

}