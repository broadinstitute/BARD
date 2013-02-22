import grails.util.BuildSettingsHolder
import groovyx.remote.transport.http.HttpTransport

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/21/13
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
class XRemoteControl extends groovyx.remote.client.RemoteControl {
    static public final RECEIVER_PATH = "grails-remote-control"

    static defaultReceiverAddress

    XRemoteControl() {
        super(new HttpTransport(getFunctionalTestReceiverAddress(), Thread.currentThread().contextClassLoader), Thread.currentThread().contextClassLoader)
    }

    private static getFunctionalTestReceiverAddress() {
        def base = System.getProperty("geb.build.baseUrl")

        if (base == null) {
            base = getFunctionalTestBaseUrl()
        }

        if (!base) {
            throw new IllegalStateException("Cannot get receiver address for functional testing as functional test base URL is not set. Are you calling this from a functional test?")
        }

        base.endsWith("/") ? base + RECEIVER_PATH : base + "/" + RECEIVER_PATH
    }

    private static getFunctionalTestBaseUrl() {
        BuildSettingsHolder.settings?.functionalTestBaseUrl
    }
}
