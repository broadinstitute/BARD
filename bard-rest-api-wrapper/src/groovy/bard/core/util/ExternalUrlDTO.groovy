package bard.core.util

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 3/10/13
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
class ExternalUrlDTO {
    String ncgcUrl
    String promiscuityUrl
    String capUrl

    ExternalUrlDTO(){

    }

    public String getNcgcUrl() {
        return this.ncgcUrl
    }

    public void setNcgcUrl(String baseUrl) {
        this.ncgcUrl = baseUrl
    }
}
