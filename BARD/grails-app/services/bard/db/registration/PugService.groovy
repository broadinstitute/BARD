package bard.db.registration

import bard.db.experiment.Substance
import bard.util.NetClientService
import groovy.xml.MarkupBuilder
import org.apache.commons.net.ftp.FTPClient
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient

import java.util.zip.GZIPInputStream

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/7/13
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
class PugService {
    static String PUG_URL = "http://pubchem.ncbi.nlm.nih.gov/pug/pug.cgi"

    NetClientService netClientService
    long timeBetweenRequests = 30 * 1000; // retry every 30 seconds

    /** generates a query for asking about a previous request */
    String requestStatus(String id) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.'PCT-Data'() {
            'PCT-Data_input'() {
                'PCT-InputData'() {
                    'PCT-InputData_request'() {
                        'PCT-Request'() {
                            'PCT-Request_reqid'(id)
                            'PCT-Request_type'(value: "status")
                        }
                    }
                }
            }
        }
        return writer.toString()
    }

    /** Generate a query asking for smiles for a set of sids */
    String substanceQuery(List<String> ids) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.'PCT-Data'() {
            'PCT-Data_input'() {
                'PCT-InputData'() {
                    'PCT-InputData_download'() {
                        'PCT-Download'() {
                            'PCT-Download_uids'() {
                                'PCT-QueryUids'() {
                                    'PCT-QueryUids_ids'() {
                                        'PCT-ID-List'() {
                                            'PCT-ID-List_db'('pcsubstance')
                                            'PCT-ID-List_uids'() {
                                                ids.collect {
                                                'PCT-ID-List_uids_E'(it)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            'PCT-Download_format'(value:"smiles")
                            'PCT-Download_compression'(value:"gzip")
                        }
                    }
                }
            }
        }
        return writer.toString()
    }

    /** Given a FTP url, call a callback with a stream for reading that resource */
    void readFtpResource(String url, Closure callback) {
        def pubChemAnonFtpUser = "anonymous"
        def pubChemAnonFtpPassword = "anonymous"

        FTPClient ftpClient = netClientService.createFtpClient()
        URL parsedUrl = new URL(url);
        ftpClient.connect(parsedUrl.getHost());
        ftpClient.login(pubChemAnonFtpUser, pubChemAnonFtpPassword);
        InputStream input = ftpClient.retrieveFileStream(parsedUrl.getFile())

        callback(input)

        ftpClient.disconnect()
    }

    /** Call a callback once per sid, smiles tuple.  If the sid could not be found smiles == null */
    void parseSmilesTable(InputStream input, Closure callback) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(input)))
        while(true) {
            String line = reader.readLine()
            if (line == null)
                break
            def fields = line.split("\t")
            def id = fields[0]
            def smiles = null
            if (fields.length == 2)
                smiles = fields[1]

            callback(id, smiles)
        }
        reader.close()
    }

    /** submit the given query to PUG and return a parsed version of the response */
    Node executeQuery(String query) {
        HttpClient httpClient = netClientService.createHttpClient()
        HttpPost post = new HttpPost(PUG_URL)
        post.setEntity(new StringEntity(query))
        String response = httpClient.execute(post, new BasicResponseHandler())

        return new XmlParser().parseText(response)
    }

    /**
    Returns the set of substance ids that are not valid.

     First the DB is checked, an if not found there, pubchem is queried.  If found in pubchem a Substance with that id
    is populated in the DB.   After this call, any sid not returned can be assumed to exist in the DB.
     */
    Collection<String> validateSubstanceIds(Collection<Long> sids) {
        // find the subset that are not in the database
        def missingSids = sids.findAll { Substance.get(it) == null }

        List missingFromPubchem = []

        getSubstancesFromPubchem(missingSids) { sid, smiles ->
            Long substanceId = Long.parseLong(sid)
            if(smiles == null) {
                missingFromPubchem << sid
            } else {
                Substance substance = new Substance(id: substanceId, smiles: smiles, dateCreated: new Date())
                // the id doesn't get set in the line above.  Why?
                substance.id = substanceId
                assert substance.save()
            }
        }

        return missingFromPubchem
    }

    Node find(Node xml, String tagName) {
        // there's probably a better way
        return (xml.depthFirst().find {it.name() == tagName})
    }

    def getSubstancesFromPubchem(sids, callback) {
        if (sids.size() > 0) {
            def parsedResponse = executeQuery(substanceQuery(sids))

            def status = find(parsedResponse, "PCT-Status")?."@value"
            if (status != "success") {
                throw new RuntimeException("Querying substances from pubchem failed: "+parsedResponse);
            }

            def reqId = find(parsedResponse, "PCT-Waiting_reqid")?.text()
            if (reqId != null) {
                while(true) {
                    Thread.sleep(timeBetweenRequests)
                    parsedResponse = executeQuery(requestStatus(reqId))
                    if (find(parsedResponse, "PCT-Download-URL_url") != null)
                        break
                }
            }

            def url = find(parsedResponse, "PCT-Download-URL_url")?.text()
            assert url != null

            readFtpResource(url) { input ->
                parseSmilesTable(input, callback)
            }
        }
    }
}
