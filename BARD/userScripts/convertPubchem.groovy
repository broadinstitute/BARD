import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import bard.db.experiment.PubchemReformatService
import bard.db.registration.AttributeType
import bard.db.registration.ExternalReference
import groovy.sql.Sql
import org.hibernate.classic.Session
import org.hibernate.jdbc.Work

import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException

PubchemReformatService pubchemReformatService = ctx.pubchemReformatService

for (aid in [488896, 493194, 540246, 540326]) {
    ExternalReference ref = ExternalReference.findByExtAssayRef("aid=${aid}")
    assert ref != null
    println("converting ${aid} -> exp-${ref.experiment.id}")
    pubchemReformatService.convert(ref.experiment.id, "/Users/pmontgom/data/pubchem-conversion/CARS-603/${aid}.csv", "/Users/pmontgom/data/pubchem-conversion/CARS-603-converted/exp-${ref.experiment.id}.csv")
}
