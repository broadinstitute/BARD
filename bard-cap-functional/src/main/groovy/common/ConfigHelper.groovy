/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package common

import static org.apache.commons.lang3.SystemUtils.FILE_SEPARATOR
import static org.apache.commons.lang3.SystemUtils.getUserHome
//testign the package imports 
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
