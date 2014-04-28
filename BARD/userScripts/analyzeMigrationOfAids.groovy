/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import bard.db.enums.Status
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentFile
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import org.hibernate.SQLQuery

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by jasonr on 3/4/14.
 */

final DateFormat df = new SimpleDateFormat("yyyy-MM-dd")
PubChemAid.df = df
final Date threshold = df.parse("2012-10-01")

final File aidHoldUntilDateFile = new File("C:/Local/i_drive/projects/BARD/dataMigration/HTS/aid_hold_until_date.csv")
final File aidFile = new File("C:/Local/i_drive/projects/BARD/dataMigration/HTS/primary_screen_aids.txt")
final broadOwnerRoleId= 16
final String outputFile = new File("${df.format(new Date())}_aid_status.txt")

List<PubChemAid> pubchemAidList = readAidsFromFile(aidFile)

findExternalReferences(pubchemAidList)

findHoldUntilDate(pubchemAidList, aidHoldUntilDateFile)

findAnnotSizes(pubchemAidList)


List<PubChemAid> prioritized = pubchemAidList.findAll({PubChemAid pcAid ->
    return (pcAid.holdUntilDate && pcAid.holdUntilDate.time <= threshold.time && pcAid.erList.size() == 1 && pcAid.erList.get(0).experiment.ownerRoleId == broadOwnerRoleId)
})

final PubChemAidComparator pubChemAidComparator = new PubChemAidComparator(df.parse("2008-01-01").time)

Collections.sort(prioritized, pubChemAidComparator)

List<PubChemAid> next = pubchemAidList.findAll({PubChemAid pcAid ->
    return (pcAid.holdUntilDate && pcAid.holdUntilDate.time <= threshold.time && pcAid.erList.size() == 1 && pcAid.erList.get(0).experiment.ownerRoleId != broadOwnerRoleId)
})
Collections.sort(next, pubChemAidComparator)
prioritized.addAll(next)

next = pubchemAidList.findAll({PubChemAid pcAid ->
    return (pcAid.holdUntilDate && pcAid.holdUntilDate.time <= threshold.time && pcAid.erList.size() > 1)
})
Collections.sort(next, pubChemAidComparator)
prioritized.addAll(next)

Set<PubChemAid> remainSet = new HashSet<>(pubchemAidList)
remainSet.removeAll(prioritized)
next = new ArrayList<PubChemAid>(remainSet)
Collections.sort(next, pubChemAidComparator)
prioritized.addAll(next)

BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))

for (PubChemAid pcAid : prioritized) {
    StringBuilder builder = new StringBuilder()
    builder.append("AID:${pcAid.aid} ")
    for (ExternalReference er : pcAid.erAnnotSizeMap.keySet()) {
        AnnotationSize annotSize = pcAid.erAnnotSizeMap.get(er)

        ExperimentFile ef = null
        if (er.experiment && er.experiment.experimentFiles.size() > 0) {
            ef = Collections.max(er.experiment.experimentFiles, new Comparator<ExperimentFile>() {
                @Override
                int compare(ExperimentFile o1, ExperimentFile o2) {
                    return o1.submissionVersion - o2.submissionVersion
                }
            })
        }

        long efSubstCount = ef ? ef.substanceCount : 0

        builder.append("EID:${er.experimentId} ${er.experiment.experimentStatus} substances:$efSubstCount ")
        builder.append("ADID:${er.experiment?.assayId} ${er.experiment?.assay?.assayStatus} AnnotSizes:$annotSize ")
    }

//    println(builder.toString())
    writer.write(builder.toString())
    writer.newLine()
}


writer.close()


return



class AnnotationSize {
    ExternalReference er
    int assayContextCount
    int assayContextItemCount

    AnnotationSize(ExternalReference er, int assayContextCount, int assayContextItemCount) {
        this.er = er
        this.assayContextCount = assayContextCount
        this.assayContextItemCount = assayContextItemCount
    }
    @Override
    String toString() {
        return "$assayContextCount $assayContextItemCount"
    }
}

class AnnotSizeComparator implements Comparator<AnnotationSize>{
    @Override
    int compare(AnnotationSize o1, AnnotationSize o2) {
        int acomp = o1.assayContextCount - o2.assayContextCount
        if (acomp != 0){
            return acomp
        }
        else {
            return o1.assayContextItemCount - o2.assayContextItemCount
        }
    }
}

class PubChemAid {
    static DateFormat df

    Integer aid
    List<ExternalReference> erList
    Map<ExternalReference, AnnotationSize> erAnnotSizeMap
    Date holdUntilDate

    List<String> notes

    PubChemAid(Integer aid) {
        this.aid = aid

        erList = new LinkedList<>()

        erAnnotSizeMap = new HashMap<>()

        notes = new LinkedList<>()
    }

    @Override
    String toString() {
        String eids = erList.collect({it.experimentId}).join(",")
        String annotSizes = erAnnotSizeMap.keySet().collect({ExternalReference it ->
            String eid = it.experimentId
            AnnotationSize annotSize = erAnnotSizeMap.get(it)
            return "$eid:$annotSize"
        }).join(",")
        return "AID:$aid  EID:$eids AnnotSizes:$annotSizes HoldUntilDate:${df.format(holdUntilDate)}"
    }
}

List<PubChemAid> readAidsFromFile(File inputFile) {
    List<PubChemAid> result = new LinkedList<>()

    BufferedReader reader = new BufferedReader(new FileReader(inputFile))
    String line
    while (line = reader.readLine()) {
        PubChemAid pcAid = new PubChemAid(Integer.valueOf(line))
        result.add(pcAid)
    }
    reader.close()

    return result
}

void findExternalReferences(List<PubChemAid> pubChemAidList) {
    Map<Integer, PubChemAid> aidPubChemAidMap = new HashMap<>()
    for (PubChemAid pcAid : pubChemAidList) {
        aidPubChemAidMap.put(pcAid.aid, pcAid)
    }

    List<String> extAssayRefList = aidPubChemAidMap.keySet().collect ({Integer it -> return "aid=$it".toString()})

    List<ExternalReference> erList = ExternalReference.findAllByExtAssayRefInList(extAssayRefList)

    for (ExternalReference er : erList) {
        Integer aid = Integer.valueOf(er.getAid())

        PubChemAid pcAid = aidPubChemAidMap.get(aid)
        if (pcAid) {
            if (er.experiment && er.experiment.experimentStatus != Status.RETIRED) {
                pcAid.erList.add(er)
            }
        } else {
            System.err.println("WARNING externalReference found wiht AID that does not match provided list")
        }
    }
}



void findHoldUntilDate(Collection<PubChemAid> pubchemAidColl, File aidHoldUntilDateFile) {
    DateFormat df = new SimpleDateFormat("dd-MMM-yy")

    Map<Integer, Date> aidHoldUntilDateMap = new HashMap<>()

    BufferedReader reader = new BufferedReader(new FileReader(aidHoldUntilDateFile))

    String line;
    while ((line = reader.readLine()) != null) {
        String[] row = line.split(",")

        Integer aid = Integer.valueOf(row[0].toString())
        Date date = df.parse(row[1].toString())

        aidHoldUntilDateMap.put(aid, date)
    }

    reader.close()

    for (PubChemAid pcAid : pubchemAidColl) {
        Date holdUntilDate = aidHoldUntilDateMap.get(pcAid.aid)

        pcAid.holdUntilDate = holdUntilDate
    }
}


void findAnnotSizes(Collection<PubChemAid> pubchemAidColl) {
    for (PubChemAid pcAid : pubchemAidColl) {
        for (ExternalReference er : pcAid.erList) {
            if (er.experiment) {
                Assay a = er.experiment.assay

                pcAid.erAnnotSizeMap.put(er, new AnnotationSize(er, a.assayContexts.size(), a.assayContextItems.size()))
            } else {
                pcAid.notes.add("contains external reference that does not point to an experiment")
            }
        }
    }
}

class PubChemAidComparator implements Comparator<PubChemAid> {
    final long timeSortThreshold

    PubChemAidComparator(long timeSortThreshold) {
        this.timeSortThreshold = timeSortThreshold
    }

    @Override
    int compare(PubChemAid o1, PubChemAid o2) {
        int aidDiff = (int)(o2.aid - o1.aid)

        if ((o1.holdUntilDate && o1.holdUntilDate.time > timeSortThreshold) || (o2.holdUntilDate && o2.holdUntilDate.time > timeSortThreshold)) {
            long o1Time = o1.holdUntilDate ? o1.holdUntilDate.time : -10000
            long o2Time = o2.holdUntilDate ? o2.holdUntilDate.time : -10000

            long diff = o1Time - o2Time
            if (diff != 0) {
                return (int)diff
            } else {
                return aidDiff
            }
        } else {
            return aidDiff
        }
    }
}
