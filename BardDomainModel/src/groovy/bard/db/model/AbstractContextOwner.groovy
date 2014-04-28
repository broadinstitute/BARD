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

package bard.db.model

import bard.db.enums.ContextType
import bard.db.registration.AssayContext

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/7/13
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractContextOwner {

    abstract List<? extends AbstractContext> getContexts()

    public static class ContextGroup {
        String key;
        String description;
        List<AbstractContext> value;
    }

    List<ContextGroup> groupContexts() {
        List<ContextGroup> groups = []
        contexts.groupBy { it.contextType } .each{k,v ->
            groups << new ContextGroup(key: k.id, description: "", value: v)
        }

        return groups
    }

    ContextGroup groupBySection(ContextType type) {
        def result = contexts.findAll { it.contextType == type }
        return new ContextGroup(key: type.id, description: "", value: result);
    }

    ContextGroup groupUnclassified() {
        groupBySection(ContextType.UNCLASSIFIED)
    }

    ContextGroup groupAssayDesign() {
        groupBySection(ContextType.ASSAY_DESIGN)
    }

    ContextGroup groupAssayReadout() {
        groupBySection(ContextType.ASSAY_READOUT)
    }

    ContextGroup groupAssayComponents() {
        groupBySection(ContextType.ASSAY_COMPONENTS)
    }

    ContextGroup groupBiology() {
        groupBySection(ContextType.BIOLOGY)
    }

    ContextGroup groupExperimentalVariables() {
        groupBySection(ContextType.EXPERIMENT)
    }

    ContextGroup groupAssayProtocol() {
        groupBySection(ContextType.ASSAY_PROTOCOL)
    }

    /**
     * a hack to try and split the contexts into columns of relatively equal contextItems
     *
     * an attempt at limiting white space and compressing the view
     *
     * @param contexts
     * @return list of up to 2 lists
     */
    List<List<AbstractContext>> splitForColumnLayout(List<AbstractContext> contexts) {
        int totalNumContextItems = 0
        if (contexts.size() > 0) {
            totalNumContextItems = contexts.collect { it.getContextItems().size() }.sum()
        }
        int half = totalNumContextItems / 2
        int count = 0
        List<AssayContext> firstColumnContexts = contexts.findAll { context ->
            count += context.contextItems.size();
            count <= half
        }
        List<AssayContext> secondColumnContexts = contexts - firstColumnContexts
        def splitContexts = [firstColumnContexts, secondColumnContexts].findAll() // eliminates any empty lists
        splitContexts
    }

    public abstract void removeContext(AbstractContext context);

    public abstract AbstractContext createContext(Map properties);
}
