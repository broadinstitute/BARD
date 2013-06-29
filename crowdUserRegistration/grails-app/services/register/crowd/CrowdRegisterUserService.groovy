package register.crowd

import com.fasterxml.jackson.databind.ObjectMapper
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
                //create the role if it does not exists
                //Insert into person table with the newObjectRole
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
}
