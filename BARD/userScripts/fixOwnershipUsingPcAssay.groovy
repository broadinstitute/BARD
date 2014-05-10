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


import bard.db.audit.BardContextUtils
import bard.db.enums.Status
import bard.db.experiment.Experiment
import bard.db.people.Role
import bard.db.project.ProjectExperiment
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory


/**
 * Created by dlahr on 4/26/2014.
 */

//generate pcaFile from from pcassay table created / refreshed by Mark Southern in schema bard_data_qa_dashboard using:
//select assay_aid, assay_source_name from pcassay order by assay_aid;
String pcaFile = "C:/Local/documents and analysis/therapeutics platform/projects/bard/dataMigration/2014-04_fix_ownership/2014-05-10_pcassay_ownership.txt"

String sourceOwnerMapFile = "C:/Local/documents and analysis/therapeutics platform/projects/bard/dataMigration/2014-04_fix_ownership/2014-04-26_pcassay_role_map.txt"

PrintWriter out = new PrintWriter("output.txt")

SpringSecurityUtils.reauthenticate("dlahr", null)

SessionFactory sf = ctx.sessionFactory
BardContextUtils.setBardContextUsername(sf.currentSession, "dlahr")
println(BardContextUtils.getCurrentUsername(sf.currentSession))

final Role broadRole = Role.findById(16)
Set<Long> wereBroadExperimentSet = new HashSet<>()
Set<Long> wereBroadAssaySet = new HashSet<>()

Map<Long, Role> aidOwnerRoleMap = buildAidOwnerRoleIdMap(pcaFile, sourceOwnerMapFile)
Map<Long, String> okToChangeFrom = [42l:"Admin role for jesque ui", 8l:"Assay Provider", 5l:"Assay Registrar", 3l:"BARD Administrator",
                                    16l:"Broad",11l:"Curator",4l:"Dictionary Manager",7l:"Project Manager",6l:"Results Depositor",
                                    9l:"Science Officer",41l:"Temp admin role needs by springsec",2l:"Viewer"] as Map<Long, String>


Warnings warnings = new Warnings()

int progress = 0
int changeCount = 0
Experiment.withTransaction {status ->
    println("finding experiments:")
    List<Experiment> experimentList =
            Experiment.findAllByExperimentStatusNotEqual(Status.RETIRED)
//            Experiment.findAllById(10284)
    out.println("# non-retired experiments found:  ${experimentList.size()}")

    for (Experiment e : experimentList) {
        if (e.projectExperiments.size() > 0) {
            ProjectExperiment pe = e.projectExperiments.find({ ProjectExperiment pe ->
                pe.project.ownerRole == e.ownerRole
            })
            if (!pe) {
                List<Long> projectIdList = e.projectExperiments.collect({ ProjectExperiment pe1 ->
                    pe1.project.id
                })
                warnings.add(e, "belongs to projects but ownership does not match any of those projects.  project ID's: ${projectIdList.join(",")}")
            }
        } else if (e.externalReferences.size() > 0) {
            Set<String> aidSet = new HashSet<>(e.externalReferences.collect({ ExternalReference er -> er.aid }))
            aidSet.remove(null)

            if (aidSet.size() > 2) {
                warnings.add(e, "has more than one PubChem AID associated with it.  pubChem AID's:  ${aidSet.join(",")}")
            } else if (aidSet.size() == 0) {
                List<String> extAssayRefList = e.externalReferences.collect({ ExternalReference er -> er.extAssayRef })
                warnings.add(e, "has at least one external reference entry but none are to PubChem. extArrayRef's: ${extAssayRefList.join(",")}")
            } else {
                Long aid = Long.valueOf(aidSet.iterator().next())

                final Role pcaOwner = aidOwnerRoleMap.get(aid)

                if (pcaOwner) {
                    if (! pcaOwner.id.equals(e.ownerRole.id)) {
                        if (okToChangeFrom.containsKey(e.ownerRole.id)) {
                            if (e.ownerRole.id == broadRole.id) {
                                wereBroadExperimentSet.add(e.id)
                            }

                            e.ownerRole = pcaOwner

                            changeCount++
                        } else {
                            warnings.add(e, "current owner is not in okToChangeFrom. current owner: ${e.ownerRole.displayName}  pcaOwner: ${pcaOwner.displayName}")
                        }
                    }
                }
            }
        }

        progress++
        if (progress % 100 == 0) {
            println("progress: $progress")
        }
    }
    out.println("changed ownership for $changeCount experiments")

    println("finding assay defintions")
    List<Assay> assayList =
            Assay.findAllByAssayStatusNotEqual(Status.RETIRED)
//            Assay.findAllById(10608)
    out.println("number of non-retired assay definitions:  ${assayList.size()}")

    progress = 0
    changeCount = 0
    for (Assay a : assayList) {
        Set<Role> expOwnerSet = new HashSet<>(a.experiments.collect({Experiment it -> it.ownerRole}))

        if (! expOwnerSet.contains(a.ownerRole)) {
            if (expOwnerSet.size() == 1) {
                Role expOwner = expOwnerSet.iterator().next()

                if (okToChangeFrom.containsKey(a.ownerRole.id)) {
                    if (a.ownerRole.id == broadRole.id) {
                        wereBroadAssaySet.add(a.id)
                    }

                    a.ownerRole = expOwner

                    changeCount++
                } else {
                    warnings.add(a, "current owner is not in okToChangeFrom.  current owner:${a.ownerRole.displayName}  experiment owner:${expOwner.displayName}")
                }
            } else if (expOwnerSet.size() > 1) {
                String expOwnersString = expOwnerSet.collect({Role it -> it.displayName}).join(",")
                warnings.add(a, "ownership mismatch, but there are multiple experiments with different owners.  current owner:${a.ownerRole.displayName}  experiment owners:$expOwnersString")
            }
        }

        progress++
        if (progress % 100 == 0) {
            println("progress: $progress")
        }

    }

    out.println("changed ownership for $changeCount assay definitions")

    println("committing ownership transaction")
//    status.setRollbackOnly()
}


out.println()
out.println()
out.println("were Broad experiments before change:  ${wereBroadExperimentSet.size()}")
int count = 0
for (Long eId : wereBroadExperimentSet) {
    out.print("$eId, ")
    count++
    if (count % 10 == 0) {
        out.println()
    }
}
out.println()
out.println()
out.println("were Broad assay definitions before change:  ${wereBroadAssaySet.size()}")
count = 0
for (Long aId : wereBroadAssaySet) {
    out.print("$aId, ")
    count++
    if (count % 10 == 0) {
        out.println()
    }
}

out.println()
out.println()
out.println("warnings detected:")
for (Object o : warnings.warningMap.keySet()) {
    for (String w : warnings.warningMap.get(o)) {
        out.println("${o.class}\t${o.id}\t$w")
    }
}


out.close()
return


class Warnings {
    Map<Object, List<String>> warningMap = new HashMap<>()

    void add(Object o, String warning) {
        List<String> wList = warningMap.get(o)

        if (! wList) {
            wList = new LinkedList<>()
            warningMap.put(o, wList)
        }

        wList.add(warning)
    }
}


Map<Long, Role> buildAidOwnerRoleIdMap(String pcaFile, String sourceOwnerMapFile) {
    List<Role> roles = Role.findAll()

    Map<String, Role> pcaNameRoleMap = new HashMap<>()
    BufferedReader reader = new BufferedReader(new FileReader(sourceOwnerMapFile))
    String line
    while ((line = reader.readLine()) != null) {
        String[] split = line.split("\t")

        final Long roleId = Long.valueOf(split[0])

        Role role = roles.find({Role it -> it.id == roleId})

        pcaNameRoleMap.put(split[2], role)
    }
    reader.close()


    Map<Long, Role> result = new HashMap<>()

    reader = new BufferedReader(new FileReader(pcaFile))
    while ((line = reader.readLine()) != null) {
        String[] split = line.split("\t")

        Long aid = Long.valueOf(split[0])

        if (split.length >= 2) {
            Role role = pcaNameRoleMap.get(split[1])

            if (role) {
                result.put(aid, role)
            }
        }
    }
    reader.close()

    return result
}

