package bard.db.project

import grails.converters.JSON
import net.sf.ehcache.hibernate.HibernateUtil
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import bard.db.registration.ExternalReference
import bard.db.experiment.Experiment
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil

/**
 * This is a service that return a list of nodes and edges in JSON format to be displayed in views.
 * The service can be reused with different view implementation if view requires similar objects.
 * Otherwise, this service can be adapted to provide more information to view for displaying purpose.
 */
class ProjectExperimentRenderService {
    /**
     * Use BFS to construct graph from a project
     *
     * @param project
     * @return nodes and edges in JSON format
     */
    JSON contructGraph(Project project) {
        def result = processProject(project)
        return new JSON(result)
    }

    def processProject(Project project) {
        Set<Long> visitedNodes = [] as Set // as name said
        List<Long> queue = []              // processing queue

        Set<Edge> edges = new HashSet()   // all edges
        List<Node> nodes = []            // nodes have connections to other nodes
        List<Node> isolatedNodes = []   // nodes have no connections to any nodes, need to be displayed differently
        Map<Long, ProjectExperiment> byId = [:]

        // add each experiment id associated with the project
        project.projectExperiments.each {
            queue.add(it.id)
            byId[it.id] = it
        }

        // BFS to construct graph
        while (queue.size() > 0) {
            Long currentNode = queue.remove(0)         // remove the first one in the queue
            if (visitedNodes.contains(currentNode)) { // don't process one that is already processed
                continue;
            }
            visitedNodes.add(currentNode)           // keep visited

            ProjectExperiment pe = byId[currentNode]
            Node node = constructNode(pe)           // construct node
            if (isIsolatedNode(pe)) {
                isolatedNodes << node
            } else {
                nodes << node
                edges.addAll(constructEdges(pe, queue))   // construct edge
            }
        }
        countInOutEdges(edges, nodes)
        def result = ["connectedNodes": nodes, "edges": edges, "isolatedNodes": isolatedNodes]
        return result
    }

    /**
     * Count incoming edges and outgoing edges. This information will be used to position nodes on the display screen
     * @param edges
     * @param nodes
     */
    void countInOutEdges(Set<Edge> edges, List<Node> nodes) {
        def nodeInCount = [:]
        def nodeOutCount = [:]
        nodes.each {
            nodeInCount[it.id] = 0
            nodeOutCount[it.id] = 0
        }
        edges.each { Edge edge ->
            nodeInCount[edge.to]++
            nodeOutCount[edge.from]++
        }
        nodes.each {
            it.keyValues.incount = nodeInCount.get(it.id).toString()
            it.keyValues.outcount = nodeOutCount.get(it.id).toString()
        }
    }

    /**
     * nodes no incoming and outgoing edges are isolated
     * @param pe
     * @return boolean
     */
    boolean isIsolatedNode(ProjectExperiment pe) {
        return pe?.followingProjectSteps?.size() == 0 &&
                pe?.precedingProjectSteps?.size() == 0
    }

    /**
     * Each projectexperiment is a node, extract attributes may be displayed
     * @param pe
     * @return node
     */
    Node constructNode(ProjectExperiment pe) {
        pe = GrailsHibernateUtil.unwrapIfProxy(pe)
        def peAttributes
        if (pe instanceof ProjectPanelExperiment) {
            ProjectPanelExperiment ppe = (ProjectPanelExperiment) pe;
            peAttributes = [
                    'type'    : 'panel',
                    'stage'   : ppe.stage?.label,
                    'stageid' : ppe.stage?.id,
                    'incount' : 0,
                    'outcount': 0,
                    'peid'    : ppe.id,
                    'pid'     : ppe.panelExperiment.panelId,
                    'pnlExpId': ppe.panelExperiment.id,
                    'panel'   : ppe.panelExperiment.panel.name,
                    'eids'    : ppe.panelExperiment.experiments.collect { it.id }
            ]
        } else if (pe instanceof ProjectSingleExperiment) {
            ProjectSingleExperiment pse = (ProjectSingleExperiment) pe;
            peAttributes = [
                    'type'    : 'single',
                    'peid'    : pse.id,
                    'stage'   : pse.stage?.label,
                    'stageid' : pse.stage?.id,
                    'aid'     : getAidByExperiment(pse.experiment),
                    'assay'   : pse.experiment?.assay?.id,
                    'incount' : 0,
                    'outcount': 0,
                    'eid'     : pse.experiment?.id,
                    'ename'   : pse.experiment?.experimentName,
                    'cansee'  : pse.experiment?.permittedToSeeEntity(),
            ]
        } else {
            throw new RuntimeException("Unknown projectExperiment type: ${pe?.class?.getName()}")
        }
        return new Node(id: pe?.id, keyValues: peAttributes)
    }

    /**
     *
     * @param experiment
     * @return Aid if there is one
     */
    String getAidByExperiment(Experiment experiment) {
        String aid = ""
        ExternalReference er = ExternalReference.findByExperiment(experiment)
        if (er && er.extAssayRef ==~ /aid=\d+/) {
            def matcher = (er.extAssayRef =~ /aid=(\d+)/)
            if (matcher.matches())
                aid = matcher[0][1]
        }
        return aid
    }

    /**
     * Given a projectexperiment, find all preceding and following projectstep and construct edges
     * @param pe
     * @param queue
     * @return a set of edge
     */
    Set<Edge> constructEdges(ProjectExperiment pe, List<Long> queue) {
        Set<Edge> edges = new HashSet()
        for (ProjectStep step : pe.precedingProjectSteps) {
            edges.add(constructEdge(step, queue))
        }
        for (ProjectStep step : pe.followingProjectSteps) {
            edges.add(constructEdge(step, queue))
        }
        return edges
    }

    /**
     * Given a projectstep, construct edge, add from and to nodes to processing queue
     * @param step
     * @param queue
     * @return edge
     */
    Edge constructEdge(ProjectStep step, List<Long> queue) {
        queue.add(step.nextProjectExperiment.id)
        queue.add(step.previousProjectExperiment.id)
        Edge edge = new Edge(from: step?.previousProjectExperiment?.id,
                to: step?.nextProjectExperiment?.id,
                label: step?.edgeName)
        return edge
    }
}
/**
 * Customized representation for nodes.
 */
class Node implements Serializable {
    String id
    Map<String, String> keyValues  // extra information for display
    @Override
    String toString() {
        return id
    }
}

/**
 * Customized representation for edges.
 */
class Edge implements Serializable {
    String from = "none"
    String to = "none"
    String label = "label"

    @Override
    String toString() {
        return from + " " + to + " " + label
    }

    @Override
    boolean equals(Object obj) {
        if (obj == null) {
            return false
        }
        if (obj.is(this)) {
            return true
        }
        if (obj.getClass() != getClass()) {
            return false
        }

        Edge rhs = (Edge) obj

        return new EqualsBuilder()
                .append(this.from, rhs.from)
                .append(this.to, rhs.to)
                .append(this.label, rhs.label)
                .isEquals()
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 13)
                .append(from)
                .append(to)
                .append(label)
                .toHashCode();
    }
}
