package bard.util

import org.apache.commons.net.ftp.FTPClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.HttpClient

class NetClientService {

    FTPClient createFtpClient() {
        return new FTPClient()
    }

    HttpClient createHttpClient() {
        return new DefaultHttpClient();
    }
}
