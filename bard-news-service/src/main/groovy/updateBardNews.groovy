import org.apache.commons.lang3.time.StopWatch
import groovyx.net.http.*

import javax.xml.datatype.DatatypeFactory
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import groovy.sql.Sql
import org.apache.log4j.*

//org.apache.log4j.BasicConfigurator.configure();
//LogManager.rootLogger.level = Level.ERROR
LogManager.getCurrentLoggers().each { Logger logger -> logger.level = Level.ERROR }
Logger log = Logger.getLogger('bardBewsAppender')
log.level = Level.INFO
assert args && args[0], "No config-file name has been provided (e.g., ~/.grails/BARD-production-config.groovy)\n"
String configFileName = args[0]

File configFile = new File(configFileName)
assert configFileName, "Could not open config file: ${configFileName}\n"


ConfigSlurper configSlurper = new ConfigSlurper()
ConfigObject config = configSlurper.parse(configFile.text)
String url = config.dataSource.url
assert url, "Could not find database url configuration\n"
String driverClassName = config.dataSource.driverClassName
assert driverClassName, "Could not find driverClassName configuration\n"
String dialect = config.dataSource.dialect
assert dialect, "Could not find dialect configuration\n"
String username = config.dataSource.username
assert username, "Could not find username configuration\n"
String password = config.dataSource.password
assert password, "Could not find password configuration\n"
println([url, username, password, driverClassName])
Sql sql = Sql.newInstance(url, username, password, driverClassName);

StopWatch sw = new StopWatch()
sw.start()
log.info("started pulling for BARD news")

def uri = new groovyx.net.http.URIBuilder('http://askthebard.blogspot.com/feeds/posts/default')
def http = new HTTPBuilder(uri)

try {
    sql.withTransaction {
        sql.call("call bard_context.set_username('bard_news')")

//        sql.execute('delete from bard_news')
        String bardNewsItemsQuery = """select * from bard_news"""
        def rows = sql.rows(bardNewsItemsQuery)

        http.request(GET, XML) { req ->
            // executed for all successful responses:
            response.success = { resp, xml ->
                assert resp.statusLine.statusCode == 200
                xml.entry.each { entry ->
                    // iterate over each XML ATOM 'entry' element in the response:
                    Date published = DatatypeFactory.newInstance().newXMLGregorianCalendar(entry.published as String).toGregorianCalendar().getTime()
                    Date updated = DatatypeFactory.newInstance().newXMLGregorianCalendar(entry.updated as String).toGregorianCalendar().getTime()
                    String link = (entry.link.find { it["@type"] == "text/html" && it["@rel"] == "alternate" }["@href"] as String).replaceAll("'", "''")

                    String parametrizedSql = """insert into BARD_NEWS (id, version, entry_id, Date_Published, Entry_Date_Updated, title, link, author_name, author_email, author_uri, Date_Created, Last_Updated, modified_by)
            values (
                BARD_NEWS_ID_SEQ.nextval,
                0,
                '${entry.id}',
                timestamp'${published.toTimestamp()}',
                timestamp'${updated.toTimestamp()}',
                '${(entry.title as String).replaceAll("'", "''")}',
                '${link}',
                '${(entry.author.name as String).replaceAll("'", "''")}',
                '${(entry.author.email as String).replaceAll("'", "''")}',
                '${(entry.author.uri as String).replaceAll("'", "''")}',
                current_timestamp,
                null,
                null
            )"""

                    //If we already have that news-item entry in the bard_news table, skip. For now, we are ignoring updates to an existing entry.
                    Boolean newsItemExists = rows*.ENTRY_ID.contains(entry.id)
                    if (newsItemExists) {
                        log.info("Skipping - entry already exists: ${entry.id}")
                        return
                    }

                    log.info("Adding ${entry.id}")
                    sql.execute(parametrizedSql)
                }
            }
        }
    }
}
catch (Throwable exp) {
    log.error("Failed to execute called procedure\n${exp}")
    throw (exp)
}

sw.stop()
String message = "finished pulling BARD news: ${sw}\n"
log.info(message)

return 0