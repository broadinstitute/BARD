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

import grails.util.Environment

//Override in config file
//cacheable plugin
grails.cache.config = {
    cache {
        name 'target'
    }
    cache {
        name 'dictionaryElements'
        eternal false
        overflowToDisk true
        maxElementsInMemory 10000
        maxElementsOnDisk 10000000
    }
    cache {
        name 'elementHierarchyPaths'
    }
    cache {
        name 'goOntologyPaths'
    }
}
ncgc.server.root.url = "http://bard.nih.gov/api/v17.3"
promiscuity.badapple.url = "${ncgc.server.root.url}/plugins/badapple/prom/cid/"
bard.cap.home = "http://localhost:8081/BARD/"

log4j = {

    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'

    warn 'org.mortbay.log'
}
/**
 * Loads external config files from the .grails subfolder in the user's home directory
 * Home directory in Windows is usually: C:\Users\<username>\.grails
 * In Unix, this is usually ~\.grails
 *
 * bardqueryapi-commons-config.groovy is used to hold generic, non environment-specific configurations such as external api credentials, etc.
 */
grails.config.locations = []

// If the developer specifies a directory for the external config files at the command line, use it.
// This will look like 'grails -DprimaryConfigDir=[directory name] [target]'
// Otherwise, look for these files in the user's home .grails/projectdb directory
// If there are no external config files in either location, don't override anything in this Config.groovy
String primaryOverrideDirName = System.properties.get('primaryConfigDir')
String secondaryOverrideDirName = "${userHome}/.grails/${appName}"

List<String> fileNames = ["${appName}-commons-config.groovy", "${appName}-${Environment.current.name}-config.groovy"]
fileNames.each {fileName ->
    String primaryFullName = "${primaryOverrideDirName}/${fileName}"
    String secondaryFullName = "${secondaryOverrideDirName}/${fileName}"

    if (new File(primaryFullName).exists()) {
        println "Overriding Config.groovy with $primaryFullName"
        grails.config.locations << "file:$primaryFullName"
    }
    else if (new File(secondaryFullName).exists()) {
        println "Overriding Config.groovy with $secondaryFullName"
        grails.config.locations << "file:$secondaryFullName"
    }
    else {
        println "Skipping Config.groovy overrides: $primaryFullName and $secondaryFullName not found"
    }
}
