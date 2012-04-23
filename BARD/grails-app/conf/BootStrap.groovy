import bard.db.registration.AssayStatus
import bard.db.util.Unit
import groovy.sql.Sql
import org.codehaus.groovy.grails.commons.ApplicationAttributes
import org.codehaus.groovy.grails.commons.ApplicationHolder

import javax.servlet.ServletContext
import javax.sql.DataSource
import bard.db.dictionary.ElementStatus

class BootStrap {

    def init = { ServletContext ctx ->
        environments {
            development {

                new AssayStatus(status:"Pending").save();
                new AssayStatus(status:"Active").save();
                new AssayStatus(status:"Superceded").save();
                new AssayStatus(status:"Retired").save();

                new ElementStatus(elementStatus: 'Pending', capability: 'Element is new, not yet approved but can be used for assasy definition and data entry subject to future curation and approval').save()
                new ElementStatus(elementStatus: 'Published', capability: 'Element can be used for any assay definiton or data upload').save()
                new ElementStatus(elementStatus: 'Deprecated', capability: 'Element has been replaced by a another one.  It should not be used in new assasy definitions, but can be used when uploading new experiments.  It is subject to future retirement').save()
                new ElementStatus(elementStatus: 'Retired', capability: 'Element has been retired and must not be used for new assay definitions.  It can be used for uploading experiment data').save()

                new Unit(unit:"uM", description: "micromolar").save()
                new Unit(unit:"mL", description: "milliLiters").save()

                def appCtx = ctx.getAttribute(ApplicationAttributes.APPLICATION_CONTEXT)
                DataSource dataSource = appCtx.getBean("dataSource")
                Sql sql = new Sql(dataSource)
                def sqlFilePath = "resources/element_data_load.sql"
                def sqlString = ApplicationHolder.application.parentContext.getResource("classpath:$sqlFilePath").inputStream.text
                sqlString.eachLine {
                    sql.execute(it)
                }
            }
        }
    }
    def destroy = {
    }
}
