import bard.db.enums.Status
import bard.db.experiment.Experiment
import bard.db.project.Project
import bard.db.project.ProjectExperiment
import bard.db.project.ProjectSingleExperiment
import bard.db.registration.ExternalReference

import java.text.DateFormat
import java.text.SimpleDateFormat

final Long OWNER_ROLE_ID_BROAD = 16

final File inputDir = new File("C:/Local/i_drive/projects/bard/dataMigration/PCS/")
final String pcsInfoFilename = "2014-03-08_pcs.txt"


final String outputFile = "projectInfo.csv"

List<PubChemSubmissionInfo> pcsInfoList = readPcsInfo(new File(inputDir, pcsInfoFilename))

List<PcsProject> pcsProjectList = aggregatePcsProjects(pcsInfoList)
println("# PCS Projects:  ${pcsProjectList.size()}")

findBardProjects(pcsProjectList)

Set<PcsProject> notOwnedByBroad = new HashSet<>()
Set<PcsProject> noProjectInBard = new HashSet<>()

Map<Status, Set<PcsProject>> broadAndInBardStatusMap = new HashMap<>()
broadAndInBardStatusMap.put(Status.DRAFT, new HashSet<PcsProject>())
broadAndInBardStatusMap.put(Status.APPROVED, new HashSet<PcsProject>())
broadAndInBardStatusMap.put(Status.RETIRED, new HashSet<PcsProject>())
broadAndInBardStatusMap.put(Status.PROVISIONAL, new HashSet<PcsProject>())

for (PcsProject pcsProject : pcsProjectList) {
    boolean hasBardProject = false
    for (Project p : pcsProject.summaryAidProjectMap.values()) {
        if (p) {
            hasBardProject = true

            if (OWNER_ROLE_ID_BROAD == p.ownerRoleId) {
                Set<PcsProject> broadAndInBard = broadAndInBardStatusMap.get(p.projectStatus)
                broadAndInBard.add(pcsProject)
            } else {
                notOwnedByBroad.add(pcsProject)
            }
        }
    }

    if (!hasBardProject) {
        noProjectInBard.add(pcsProject)
    }
}

println("Not owned by the Broad in BARD ${notOwnedByBroad.size()}")
for (PcsProject pcsProject : notOwnedByBroad) {
    List<String> ownerList = pcsProject.summaryAidProjectMap.values().collect({ Project p ->
        return p.owner
    })
    String owners = ownerList.join(",")

    println("$pcsProject   owner in BARD: $owners")
}

println()
println("No BARD project ${noProjectInBard.size()}")
for (PcsProject pcsProject : noProjectInBard) {
    println(pcsProject)
}

println("for SQL query to confirm:")
int i = 0;
List<Long> noProjectInBardAidList = new LinkedList<>()
for (PcsProject pcsProject : noProjectInBard) {
    for (Long summaryAid : pcsProject.summaryAidProjectMap.keySet()) {
        noProjectInBardAidList.add(summaryAid)
    }
}
Collections.sort(noProjectInBardAidList)
println(noProjectInBardAidList.join("','aid="))

println()
println()
for (Status status : [Status.RETIRED, Status.APPROVED, Status.DRAFT, Status.PROVISIONAL]) {
    Set<PcsProject> broadAndInBard = broadAndInBardStatusMap.get(status)

    println("broad and in BARD $status ${broadAndInBard.size()}")

    for (PcsProject pcsProject : broadAndInBard) {
        println("${pcsProject.elnProject} ${pcsProject.pcsEntries.size()}")

        if (pcsProject.summaryAidProjectMap.keySet().size() > 1) {
            System.err.println("\tWarning:  multiple BARD projects found")
        }

        for (Long summaryAid : pcsProject.summaryAidProjectMap.keySet()) {
            Project p = pcsProject.summaryAidProjectMap.get(summaryAid)
            println("\t $summaryAid ${p?.id} ${p?.projectStatus}")
        }
    }

    println()
    println()
}

Date now = new Date()
final DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy")
final Date mandatoryDate = dateFormat.parse("October 01, 2012")

Set<PcsProject> draft = broadAndInBardStatusMap.get(Status.DRAFT)
for (PcsProject pcsProject : draft) {
    if (pcsProject.summaryAidProjectMap.keySet().size() > 1) {
        System.err.println("\tWarning:  multiple BARD projects found")
    } else {
        Project p = pcsProject.summaryAidProjectMap.values().iterator().next()

        println("BARD project ID:  ${p.id}")

        Map<Long, ExternalReference> aidExtRefMap = new HashMap<>()
        for (ProjectExperiment pe : p.projectExperiments) {
            for (Experiment e : pe.experiments) {
                for (ExternalReference er : e.externalReferences) {
                    aidExtRefMap.put(Long.valueOf(er.getAid()), er)
                }
            }
        }

        Map<Long, PubChemSubmissionInfo> aidPcsInfoMap = new HashMap<>()
        for (PubChemSubmissionInfo pcsInfo : pcsProject.pcsEntries) {
            if (!pcsInfo.objective.equalsIgnoreCase(PubChemSubmissionInfo.summaryProjectPhase)) {
                aidPcsInfoMap.put(pcsInfo.aid, pcsInfo)
            }
        }

        Set<Long> justInPcs = new HashSet<>(aidPcsInfoMap.keySet())
        justInPcs.removeAll(aidExtRefMap.keySet())

        if (justInPcs.size() > 0) {
            println("\tPCS reports that these AID's are part of the project, but are not part of the BARD project (yet)")

            for (Long justInPcsAid : justInPcs) {
                PubChemSubmissionInfo pcsInfo = aidPcsInfoMap.get(justInPcsAid)

                print("\t\tAID: $justInPcsAid ")
                if (!pcsInfo.holdUntilDate || pcsInfo.holdUntilDate > now) {
                    print("still on hold ")
                } else if (pcsInfo.holdUntilDate?.time < mandatoryDate.time) {
                    print("MANDATORY to migrate ")
                }

                List<ExternalReference> erList = ExternalReference.findAllByExtAssayRef("aid=$justInPcsAid".toString())

                if (erList.size() == 0) {
                    print("no experiments in BARD for this AID")
                } else {
                    for (ExternalReference er : erList) {
                        print("EID: ${er.experimentId} ")
                    }
                }
                println()
            }
        }

        Set<Long> justInBard = new HashSet<>(aidExtRefMap.keySet())
        justInBard.removeAll(aidPcsInfoMap.keySet())

        if (justInBard.size() > 0) {
            println("\tthese experiments are in BARD but not listed as part of the project in PCS")

            for (Long justInBardAid : justInBard) {
                Long eid = aidExtRefMap.get(justInBardAid).experimentId

                println("\t\tAID: $justInBardAid  EID: $eid")
            }
        }

        println()
        println()
    }
}







return

class PcsProject {
    String elnProject

    List<PubChemSubmissionInfo> pcsEntries

    Map<Long, Project> summaryAidProjectMap

    PcsProject(String elnProject) {
        this.elnProject = elnProject

        pcsEntries = new LinkedList<>()
        summaryAidProjectMap = new HashMap<>()
    }

    @Override
    String toString() {
        StringBuilder builder = new StringBuilder()
        builder.append(elnProject).append(" ")

        for (Long summaryAid : summaryAidProjectMap.keySet()) {
            builder.append("$summaryAid ${summaryAidProjectMap.get(summaryAid)?.id}").append(" ")
        }
        builder.append(pcsEntries.size())

        return builder.toString()
    }
}

class PubChemSubmissionInfo {
    static final String summaryProjectPhase = 'summary'

    String project
    String objective
    String assayName
    String dataType

    Date holdUntilDate

    Integer pbsProjectId

    Long aid

    @Override
    String toString() {
        return "$aid $project $objective $assayName $dataType $pbsProjectId"
    }
}

List<PubChemSubmissionInfo> readPcsInfo(File inputFile) {
    final String delimeter = "\t"

    final DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy")

    List<PubChemSubmissionInfo> result = new LinkedList<>()

    BufferedReader reader = new BufferedReader(new FileReader(inputFile))

    reader.readLine() //skip header

    String line
    while ((line = reader.readLine()) != null) {
        String[] split = line.split(delimeter)

        //if there is a pubchem AID
        if (split.length >= 15 && split[14]) {
            PubChemSubmissionInfo pcs = new PubChemSubmissionInfo()
            pcs.aid = Long.valueOf(split[14])

            pcs.project = split[0]
            pcs.pbsProjectId = Integer.valueOf(pcs.project.split(" ")[0])

            pcs.objective = split[1]
            pcs.assayName = split[2]
            pcs.dataType = split[3]

            if (split[12].trim()) {
                pcs.holdUntilDate = dateFormat.parse(split[12])
            }

            result.add(pcs)
        }
    }

    reader.close()

    return result
}

List<PcsProject> aggregatePcsProjects(List<PubChemSubmissionInfo> pcsInfoList) {
    Map<String, PcsProject> pcsProjectMap = new HashMap<>()

    for (PubChemSubmissionInfo pcsInfo : pcsInfoList) {
        PcsProject pcsProject = pcsProjectMap.get(pcsInfo.project)

        if (!pcsProject) {
            pcsProject = new PcsProject(pcsInfo.project)
            pcsProjectMap.put(pcsProject.elnProject, pcsProject)
        }

        if (pcsInfo.objective.equalsIgnoreCase(PubChemSubmissionInfo.summaryProjectPhase)) {
            pcsProject.summaryAidProjectMap.put(pcsInfo.aid, null)

            if (pcsProject.summaryAidProjectMap.keySet().size() > 1) {
                System.err.println("WARNING - additional pubchem summary AID found $pcsProject")
            }
        }

        pcsProject.pcsEntries.add(pcsInfo)
    }

    return new ArrayList<PcsProject>(pcsProjectMap.values())
}

void findBardProjects(List<PcsProject> pcsProjectList) {
    for (PcsProject pcsProject : pcsProjectList) {
        for (Long summaryAid : pcsProject.summaryAidProjectMap.keySet()) {
            List<ExternalReference> erList = ExternalReference.findAllByExtAssayRef("aid=$summaryAid".toString())

            for (ExternalReference er : erList) {
                if (er.project) {
                    Project previousProject = pcsProject.summaryAidProjectMap.get(summaryAid)

                    if (!previousProject) {
                        pcsProject.summaryAidProjectMap.put(summaryAid, er.project)
                    } else {
                        println("WARNING:  multiple projects found for summary AID: $pcsProject ${er.project.id}")
                    }
                }
            }
        }
    }
}

