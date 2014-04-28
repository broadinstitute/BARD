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

package register.crowd

import com.fasterxml.jackson.databind.ObjectMapper
import wslite.json.JSONObject
import wslite.rest.ContentType
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

class CrowdRegisterUserService {
    RESTClient restClient

    ObjectMapper objectMapper = new ObjectMapper()


    CrowdRegistrationUser findUserByUserName(String username) {
        try {
            def response = restClient.get(path: '/user',
                    accept: ContentType.JSON,
                    query: [username: "${username}", include_entities: true])
            if (response) {
                final String content = response.contentAsString
                final CrowdRegistrationUser registrationUser = this.objectMapper.readValue(content, CrowdRegistrationUser.class)
                return registrationUser
            }

        } catch (RESTClientException ee) {
            log.error(ee)
        }
        return null
    }

    CrowdSearchResponse findUserByEmail(String email) {
        try {
            String valueString = "email=${email}"
            //now verify that a user with this email does not exist
            def response = restClient.get(path: '/search',
                    accept: ContentType.JSON,
                    query: ['entity-type': 'user', restriction: "${valueString}", include_entities: true])
            if (response) {
                final String content = response.contentAsString
                final CrowdSearchResponse crowdSearchResponse = this.objectMapper.readValue(content, CrowdSearchResponse.class)
                if (crowdSearchResponse.users) {
                    return crowdSearchResponse
                }

            }

        } catch (RESTClientException ee) {
            log.error(ee)
        }
        return null
    }
    void updateRegisteredUser(CrowdRegistrationUser registrationUser) {
        try {
            StringWriter w = new StringWriter()
            this.objectMapper.writeValue(w, registrationUser)

            final Response response = restClient.put(path: "/user?username=${registrationUser.name}") {
                type "application/json"  // String or ContentType
                text w.toString()
            }
            if (response.statusCode >= 300 && response.statusCode < 200) {
                throw new RuntimeException("Error with status code ${response.statusCode}")
            }
        } catch (Exception ee) {
            log.error(ee.getMessage())
            throw ee
        }
    }
    void warnErrors(bean, messageSource, Locale locale = Locale.getDefault()) {

        def message = new StringBuilder(
                "problem ${bean.id ? 'updating' : 'creating'} ${bean.getClass().simpleName}: $bean")
        for (fieldErrors in bean.errors) {
            for (error in fieldErrors.allErrors) {
                message.append("\n\t").append(messageSource.getMessage(error, locale))
            }
        }
        log.warn message
    }
    void registerUser(final CrowdRegistrationUser registrationUser) {
        try {
            StringWriter w = new StringWriter()
            this.objectMapper.writeValue(w, registrationUser)

            final Response response = restClient.post(path: '/user') {
                type "application/json"  // String or ContentType
                text w.toString()
            }
            if (response.statusCode < 300 && response.statusCode > 199) {
                //this is a positive response so add user to group
                addUserToGroup(registrationUser)
            } else {
                throw new RuntimeException("Error with status code ${response.statusCode}")
            }
        } catch (Exception ee) {
            log.error(ee.getMessage())
            throw ee
        }
    }

    CrowdGroupMembership listUsersAndGroups() {
        try {
            final Response response = restClient.get(path: '/group/user/direct',
                    accept: ContentType.JSON,
                    query: [groupname: 'Bard', include_entities: true])
            if (response.statusCode < 300 && response.statusCode > 199) {
                final String content = response.contentAsString
                final CrowdGroupMembership groupMembership = this.objectMapper.readValue(content, CrowdGroupMembership.class)
                return groupMembership;
            } else {
                throw new RuntimeException("Error with status code ${response.statusCode}")
            }
        } catch (RESTClientException ee) {
            log.error(ee)
            throw ee
        }

    }

    void addUserToGroup(final CrowdRegistrationUser registrationUser) {
        addUserToGroup(registrationUser, new CrowdRegisterGroup(name: "Bard"))
    }

    void addUserToGroup(final CrowdRegistrationUser registrationUser, final CrowdRegisterGroup registerGroup) {
        try {
            StringWriter w = new StringWriter()
            this.objectMapper.writeValue(w, registerGroup)

            final Response response = restClient.post(path: "/user/group/direct?username=${registrationUser.name}") {
                type "application/json"  // String or ContentType
                text w.toString()
            }
            if (response.statusCode >= 300 && response.statusCode < 200) {
                throw new RuntimeException("Error with status code ${response.statusCode}")
            }
        } catch (Exception ee) {
            throw ee
        }
    }

    void createNewGroup(String groupName, String description) {
        CrowdGroup crowdGroup = new CrowdGroup()
        crowdGroup.active = true
        crowdGroup.description = description
        crowdGroup.name = groupName
        crowdGroup.type = "GROUP"

        try {
            StringWriter w = new StringWriter()
            this.objectMapper.writeValue(w, crowdGroup)

            final Response response = restClient.post(path: "/user/group") {
                type "application/json"  // String or ContentType
                text w.toString()
            }
            if (response.statusCode >= 300 && response.statusCode < 200) {
                throw new RuntimeException("Error with status code ${response.statusCode}")
            }
        } catch (Exception ee) {
            throw ee
        }

    }

    void resetPassword(String userName,String resetPassword) {
        try {
            String payLoad = new JSONObject().put("value",resetPassword).toString();

            final Response response = restClient.put(path: "/user/password?username=${userName}") {
                type "application/json"  // String or ContentType
                text payLoad
            }
            if (response.statusCode >= 300 && response.statusCode < 200) {
                throw new RuntimeException("Error with status code ${response.statusCode}")
            }
        } catch (Exception ee) {
            throw ee
        }
    }


}
