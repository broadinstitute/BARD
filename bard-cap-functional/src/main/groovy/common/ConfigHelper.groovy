package common

import static org.apache.commons.lang3.SystemUtils.FILE_SEPARATOR
import static org.apache.commons.lang3.SystemUtils.getUserHome

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/17/14
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
class ConfigHelper {

    public static final ConfigObject config = parseConfigFiles()

    private static ConfigObject parseConfigFiles() {
        final ConfigObject configObject = new ConfigObject()
        try {
            final List configFiles = ["defaultConfig.groovy",
                    "localConfig.groovy",
                    "${getUserHome().absolutePath}${FILE_SEPARATOR}.gradle${FILE_SEPARATOR}bard-cap-functional-config.groovy"]
            println("evaluating config files, a given property can be overridden by each successive config file (last one wins)")
            for (String configFile in configFiles) {
                final File file = new File(configFile)
                if (file.exists()) {
                    println("applying config info from ${file}")
                    configObject.merge(new ConfigSlurper().parse(file.toURI().toURL()))
                } else {
                    println("skipping,no config file found for ${file}")
                }
            }
        }
        catch (Exception e) {
            println("issue parsing config file(s)" + e)
        }
        configObject
    }

}
