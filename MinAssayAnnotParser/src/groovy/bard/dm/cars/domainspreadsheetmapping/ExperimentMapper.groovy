package bard.dm.cars.domainspreadsheetmapping

import bard.dm.cars.spreadsheet.CarsExperiment
import bard.db.registration.ExternalReference
import bard.dm.Log

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 8:29 AM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentMapper {
    private static final String aidExternalAssayReferencePrefix = "aid="

    void addExperimentPairsToProjectPair(ProjectPair projectPair) {
        Collection<CarsExperiment> carsExperimentColl = projectPair.carsProject.carsExperimentList

        Log.logger.info("\tbuilding domain-spreadsheet experiment pairs")

        Map<Integer, Set<CarsExperiment>> aidCarsExperimentMap = buildAidCarsExperimentMap(carsExperimentColl)

        for (Integer aid : aidCarsExperimentMap.keySet()) {
            List<ExternalReference> extRefList = ExternalReference.findAllByExtAssayRef(aidExternalAssayReferencePrefix + aid)

            Set<CarsExperiment> carsExperimentSet = aidCarsExperimentMap.get(aid)
            Log.logger.info("\t\tfor aid: " + aid + " which has " + carsExperimentSet.size() + " cars experiments and " + extRefList.size() + " external references")

            if (extRefList.size() == 1) {
                Log.logger.info("\t\t\tone external reference matches one or more cars experiments - pairing each cars experiment with that external reference")
                for (CarsExperiment carsExperiment : carsExperimentSet) {
                    projectPair.experimentPairList.add(buildSingleExperimentPair(carsExperiment, extRefList.get(0)))
                }
            } else if (extRefList.size() > 0) {
                Log.logger.info("\t\t\tmultiple external references found - attempting to match them to cars experiments based on experiment title")

                projectPair.experimentPairList.addAll(matchByExperiment(carsExperimentSet, extRefList))

                //the remaining unmatched
                if (carsExperimentSet.size() > 0 && extRefList.size() > 0) {
                    Iterator<CarsExperiment> carsIter = carsExperimentSet.iterator()
                    Iterator<ExternalReference> extRefIter = extRefList.iterator()

                    while (carsIter.hasNext() && extRefIter.hasNext()) {
                        CarsExperiment carsExperiment = carsIter.next()
                        ExternalReference extRef = extRefIter.next()

                        Log.logger.info("\t\t\tpartial match between cars experiment and external reference aid: " + carsExperiment.aid + " external reference: " + extRef.id)

                        projectPair.experimentPairList.add(buildSingleExperimentPair(carsExperiment, extRef))
                    }

                }

                //at this point there are either excess cars experiments or excess external references
                if (extRefList.size() > 0) {
                    StringBuilder builder = new StringBuilder()
                    for (ExternalReference extRef : extRefList) {
                        builder.append(extRef.id).append(" ")
                        projectPair.unmatchedExternalReferences.add(extRef)
                    }
                    Log.logger.info("\t\t\tFound more external references than there were cars experiments aid: " + aid + " external references ids: " + builder.toString())
                } else if (carsExperimentSet.size() > 0) {
                    StringBuilder builder = new StringBuilder()
                    for (CarsExperiment carsExperiment : carsExperimentSet) {
                        builder.append(carsExperiment.spreadsheetLineNumber).append(" ")
                        projectPair.unmatchedCarsExperiments.add(carsExperiment)
                    }
                    Log.logger.info("\t\t\tmore cars experiments than db experiments or db external references aid: " + aid + " cars spreadsheet linenumber: " + builder.toString())
                }
            } else {
                Log.logger.info("\t\t\tno external references found that match the aid")
                projectPair.unmatchedCarsExperiments.addAll(carsExperimentSet)
            }
        }
    }

    private ExperimentPair buildSingleExperimentPair(CarsExperiment carsExperiment, ExternalReference extRef) {
        ExperimentPair experimentPair = new ExperimentPair()

        experimentPair.carsExperiment = carsExperiment
        experimentPair.externalReference = extRef

        if (extRef.experiment) {
            experimentPair.experiment = extRef.experiment
            experimentPair.isPossibleProjectSummaryAid = false
        } else {
            experimentPair.isPossibleProjectSummaryAid = true
        }

        return experimentPair
    }

    /**
     * attempts to match based on experiment name items in the two collections.  If a match is found, the matched pair
     * is removed from each of the input collections (carsExperimentColl, extRefColl)
     * @param carsExperimentColl
     * @param extRefColl
     * @return
     */
    private List<ExperimentPair> matchByExperiment(Collection<CarsExperiment> carsExperimentColl, Collection<ExternalReference> extRefColl) {
        List<ExperimentPair> result = new LinkedList<ExperimentPair>()

        Iterator<CarsExperiment> carsExperimentIter = carsExperimentColl.iterator()

        while (carsExperimentIter.hasNext()) {
            CarsExperiment carsExperiment = carsExperimentIter.next()

            Iterator<ExternalReference> extRefIter = extRefColl.iterator()

            boolean foundMatch = false
            while (!foundMatch && extRefIter.hasNext()) {
                ExternalReference extRef = extRefIter.next()

                if (extRef.experiment && carsExperiment.assayName.startsWith(extRef.experiment.experimentName)) {
                    Log.logger.info("\t\t\t\tMatched using cars assayName and db experimentName aid: " + carsExperiment.aid + " experiment id: " + extRef.experiment.id)

                    result.add(buildSingleExperimentPair(carsExperiment, extRef))

                    extRefIter.remove()
                    carsExperimentIter.remove()
                    foundMatch = true
                }
            }
        }

        return result
    }

    private Map<Integer, Set<CarsExperiment>> buildAidCarsExperimentMap(Collection<CarsExperiment> carsExperimentColl) {
        Map<Integer, Set<CarsExperiment>> result = new HashMap<Integer, Set<CarsExperiment>>()

        for (CarsExperiment carsExperiment : carsExperimentColl) {
            Set<CarsExperiment> carsExperimentSet = result.get(carsExperiment.aid)
            if (null == carsExperimentSet) {
                carsExperimentSet = new HashSet<CarsExperiment>()
                result.put(carsExperiment.aid, carsExperimentSet)
            }

            carsExperimentSet.add(carsExperiment)
        }

        return result
    }
}
