package jdo

import bard.core.rest.RESTEntityServiceManager
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import bard.core.*

/**
 * Tests for RESTAssayService in JDO
 */

interface RESTServiceInterface {

    final static String baseURL = "http://bard.nih.gov/api/latest"

}