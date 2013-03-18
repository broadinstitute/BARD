package bardqueryapi

import grails.plugin.remotecontrol.RemoteControl
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import spock.lang.Specification
import spock.lang.Unroll

import static groovyx.net.http.ContentType.URLENC

@Unroll
class DoseResponseCurveFunctionalSpec extends Specification {
   // RemoteControl remote = new RemoteControl()
//    String baseUrl = "http://localhost:8080/bardwebclient"
//    String userName = "user"
//    String password = "user"
    String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL }
    String userName = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.user.username}
    String password = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers.user.password}


    void authenticate(RESTClient http) {
        http.auth.basic this.userName, this.password
    }

    def "test Render a dose response Curve #label"() {
        given: "That we have successfully logged into the system and created a valid request"
        String requestUrl = "${baseUrl}/"
        RESTClient http = new RESTClient(requestUrl)
        def postBody = createPostBody()
        authenticate(http)


        when: 'We send an HTTP POST request to the Dose response curve service'
        HttpResponseDecorator serverResponse =
            (HttpResponseDecorator) http.post(
                    path: 'doseResponseCurve/doseResponseCurve',
                    body: postBody,
                    requestContentType: URLENC
            )
        then: 'We expect that a dose response curve would be generated'
        assert serverResponse.success
        assert serverResponse.contentType == 'image/png'
        download(serverResponse.data, "drc2")
    }

    private void download(final ByteArrayInputStream stream, String fileName) {

        new File("testDRC_${fileName}.png").withOutputStream { out ->
            out << stream
        }
    }

    private Map createPostBody() {
        def requestMap = [:]
        //== Create Concentration values
        List<Double> concentrations = [0.0000000005000000413701855f,
                0.0000000005000000413701855f,
                0.000000002099999951710174f,
                0.000000002099999951710174f,
                0.000000008000000661922968f,
                0.000000008000000661922968f,
                0.00000002999999892949745f,
                0.00000002999999892949745f,
                0.0000001350000076172364f,
                0.0000001350000076172364f,
                0.0000004999999987376214f,
                0.0000004999999987376214f,
                0.0000020999998469051206f,
                0.0000020999998469051206f,
                0.000007999999979801942f,
                0.000007999999979801942f,
                0.000035000000934815034f,
                0.000035000000934815034f]
        int index = 0
        def concentrationMap = [:]
        for (Double concentration : concentrations) {
            String key = "concentrations[" + index + "]"
            Double value = concentration
            concentrationMap.put(key, value)
            ++index
        }
        requestMap.putAll(concentrationMap)
        //== Create activity values
        def activityMap = [:]
        index = 0
        List<Double> activities = [2.4638421535491943f,
                -1.2127126455307007f,
                1.3271921873092651f,
                -1.7350740432739258f,
                -0.6476028561592102f,
                -1.4504003524780273f,
                -0.31541985273361206f,
                -6.233550071716309f,
                -6.293687343597412f,
                -14.393035888671875f,
                -23.312355041503906f,
                -36.71405029296875f,
                -60.78630447387695f,
                -59.16990661621094f,
                -82.43856811523438f,
                -85.94784545898438f,
                -92.01828002929688f,
                -95.36405181884766f]
        for (Double activity : activities) {
            String key = "activities[" + index + "]"
            Double value = activity
            activityMap.put(key, value)
            ++index
        }
        requestMap.putAll(activityMap)


        Double ac50 = 0.00000124934001632937;
        requestMap.put("ac50", ac50)



        Double hillSlope = 0.9267182946205139;
        requestMap.put("hillSlope", hillSlope)

        Double s0 = 0.15947775542736053;
        requestMap.put("s0", s0)

        Double sinf = -98.35183715820312;
        requestMap.put("sinf", sinf)

        return requestMap
    }

}
