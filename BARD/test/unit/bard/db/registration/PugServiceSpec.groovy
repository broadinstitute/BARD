package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.Substance
import bard.util.NetClientService
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import org.apache.commons.io.IOUtils
import org.apache.http.client.HttpClient
import org.apache.commons.net.ftp.FTPClient
import org.apache.http.impl.client.BasicResponseHandler

import java.util.zip.GZIPOutputStream

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/7/13
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(ServiceUnitTestMixin)
@Build([Substance])
class PugServiceSpec  extends spock.lang.Specification {
    static String pugResultReadyReponse = """<?xml version="1.0"?>
<!DOCTYPE PCT-Data PUBLIC "-//NCBI//NCBI PCTools/EN" "http://pubchem.ncbi.nlm.nih.gov/pug/pug.dtd">
<PCT-Data>
  <PCT-Data_output>
    <PCT-OutputData>
      <PCT-OutputData_status>
        <PCT-Status-Message>
          <PCT-Status-Message_status>
            <PCT-Status value="success"/>
          </PCT-Status-Message_status>
        </PCT-Status-Message>
      </PCT-OutputData_status>
      <PCT-OutputData_output>
        <PCT-OutputData_output_download-url>
          <PCT-Download-URL>
            <PCT-Download-URL_url>ftp://ftp-private.ncbi.nlm.nih.gov/pubchem/.fetch/86/2522275178999659327.txt.gz</PCT-Download-URL_url>
          </PCT-Download-URL>
        </PCT-OutputData_output_download-url>
      </PCT-OutputData_output>
    </PCT-OutputData>
  </PCT-Data_output>
</PCT-Data>
"""
    static String pugProcessingResponse = """<?xml version="1.0"?>
<!DOCTYPE PCT-Data PUBLIC "-//NCBI//NCBI PCTools/EN" "http://pubchem.ncbi.nlm.nih.gov/pug/pug.dtd">
<PCT-Data>
  <PCT-Data_output>
    <PCT-OutputData>
      <PCT-OutputData_status>
        <PCT-Status-Message>
          <PCT-Status-Message_status>
            <PCT-Status value="running"/>
          </PCT-Status-Message_status>
        </PCT-Status-Message>
      </PCT-OutputData_status>
      <PCT-OutputData_output>
        <PCT-OutputData_output_waiting>
          <PCT-Waiting>
            <PCT-Waiting_reqid>402936103567975582</PCT-Waiting_reqid>
          </PCT-Waiting>
        </PCT-OutputData_output_waiting>
      </PCT-OutputData_output>
    </PCT-OutputData>
  </PCT-Data_output>
</PCT-Data>
"""

    def compressedStream(String content) {
        def compressedStream = new ByteArrayOutputStream()
        def out = new GZIPOutputStream(compressedStream)
        IOUtils.copy(new ByteArrayInputStream(content.bytes), out)
        out.close()

        return new ByteArrayInputStream(compressedStream.toByteArray())
    }


    def 'test validateSubstanceIds'() {
        setup:

        NetClientService netClientService = Mock(NetClientService)

        HttpClient httpClient = Mock(HttpClient)
        FTPClient ftpClient = Mock(FTPClient)

        // set up interactions
        netClientService.createHttpClient() >> httpClient
        netClientService.createFtpClient() >> ftpClient

        PugService pugService = new PugService()
        pugService.netClientService = netClientService
        pugService.timeBetweenRequests = 0

        when:
        def missing = pugService.validateSubstanceIds(["1","2"])

        then:
        // one id not found
        // we get in progress message twice and then the file
        interaction {
            int index = 0;
            httpClient.execute(_, _) >> { [pugProcessingResponse, pugProcessingResponse, pugResultReadyReponse][index++] }
        }

        then:
        // send a response with id 1 present, 2 absent
        1 * ftpClient.retrieveFileStream(_) >> compressedStream("1\tCC\n2\n")

        missing == ["2"]

        // one id freshly inserted
        Substance.get(1) != null
    }
}
