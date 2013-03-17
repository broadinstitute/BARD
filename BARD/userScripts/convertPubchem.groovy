import bard.db.experiment.PubchemReformatService
import bard.db.registration.ExternalReference

PubchemReformatService pubchemReformatService = ctx.pubchemReformatService

def aids = new File("/Users/pmontgom/data/pubchem-conversion/CARS-603-aid.txt").readLines()

aids.remove("488896")

for (aid in aids) {
    ExternalReference ref = ExternalReference.findByExtAssayRef("aid=${aid}")
    assert ref != null
    println("converting ${aid} -> exp-${ref.experiment.id}")
    pubchemReformatService.convert(ref.experiment.id, "/Users/pmontgom/data/pubchem-conversion/CARS-603/${aid}.csv", "/Users/pmontgom/data/pubchem-conversion/CARS-603-converted/exp-${aid}-${ref.experiment.id}.csv")
}
