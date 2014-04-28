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

package bard.db.registration

import bard.db.enums.HierarchyType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure

class MeasureTreeService {



    public List createMeasureTree(Experiment experiment, String username) {
        def roots = []

        for (m in experiment.experimentMeasures.findAll { it.parent == null }) {
            roots << createTreeFromExperimentMeasure(m, username)
        }

        sortByKey(roots)
        return roots
    }

    public void sortByKey(List<Map> children) {
        for (c in children) {
            if (c.containsKey("children"))
                sortByKey(c["children"])
        }

        children.sort { Map a, Map b -> a["title"].toLowerCase().compareTo(b["title"].toLowerCase()) }
    }

    public Map createTreeFromExperimentMeasure(ExperimentMeasure experimentMeasure, String username) {
        def key = experimentMeasure.id;
        String title = experimentMeasure.displayLabel
        HierarchyType relationship = experimentMeasure.parentChildRelationship


        if (relationship) {
            title = "(${relationship.id}) ${title}"
        }

        Long measureId = experimentMeasure.id

        def children = []

        for (m in experimentMeasure.childMeasures) {
            children.add(createTreeFromExperimentMeasure(m, username))
        }
        return [key: key, title: title, children: children, expand: true, relationship: relationship?.id, measureId: measureId, addClass: experimentMeasure.priorityElement ? "priority" : "", username: username?:""];
    }
}
