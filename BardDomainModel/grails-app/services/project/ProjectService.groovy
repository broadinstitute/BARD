package project

import bard.db.project.Project

class ProjectService {

    List<Project> findProjectByPubChemAid(Long aid) {
        def criteria = Project.createCriteria()
        return criteria.listDistinct {
            externalReferences {
                eq('extAssayRef', "aid=${aid.toString()}")
            }
        }
    }
}
