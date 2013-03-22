import bard.db.experiment.Experiment
import bard.db.experiment.ResultsService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import bard.db.experiment.PubchemReformatService
import bard.db.registration.ExternalReference

SpringSecurityUtils.reauthenticate('integrationTestUser', null)
PubchemReformatService pubchemReformatService = ctx.pubchemReformatService
ResultsService resultsService = ctx.resultsService


def aids = new File("/Users/pmontgom/data/pubchem-conversion/CARS-603-aid.txt").readLines()

aids.remove("488896")

try {
for (aid in aids) {
    ExternalReference ref = ExternalReference.findByExtAssayRef("aid=${aid}")
    
    if(ref.experiment.experimentFiles.size() > 0) {
        println("Skipping ${aid} -> ${ref.experiment.id} because results exist")
        continue
    }
    
    // set up experiment measures
    def resultMapEntries
    ExternalReference.withSession { session -> 
        session.createSQLQuery("""BEGIN
          delete from result_map;
          insert into result_map select * from southern.result_map@barddev;
          result_map_util.transfer_result_map('${aid}');
        END;
        """).executeUpdate()
        
        resultMapEntries = session.createSQLQuery("select count(*) from result_map where aid = '${aid}'").setCacheable(false).uniqueResult()
    }
    
    if(resultMapEntries == 0) {
        println("Skipping ${aid} -> ${ref.experiment.id} because we're missing resultmapping")
    }
    
    assert ref != null
    println("converting ${aid} -> exp-${ref.experiment.id}")
    def pubchemFile = "/Users/pmontgom/data/pubchem-conversion/CARS-603/${aid}.csv"
    def capFile = "/Users/pmontgom/data/pubchem-conversion/CARS-603-converted/exp-${aid}-${ref.experiment.id}.csv"
    pubchemReformatService.convert(ref.experiment.id, pubchemFile, capFile)

    ResultsService.ImportSummary results = resultsService.importResults(ref.experiment, new FileInputStream(capFile))
    println("errors: ${results.errors.size()}")
    for(e in results.errors) {
        println("\t${e}")
    }
    println("imported: ${results.resultsCreated}")
}
} catch (Exception ex) {
    ex.printStackTrace()
}