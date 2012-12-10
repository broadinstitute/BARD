package bard.db.project

import grails.converters.JSON
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * This is a service that return a list of nodes and edges in JSON format to be displayed in views.
 */
class ProjectExperimentRenderService {
    /**
     * Use BFS to construct graph from a project
     *
     * @param project
     * @return nodes and edges in JSON format
     */
    JSON contructGraph(Project project) {
        List<Long> visitedNodes = []        // as name said
        List<Long> queue = []              // processing queue

        Set<Edge> edges = new HashSet()   // all edges
        List<Node> nodes = []            // nodes have connections to other nodes
        List<Node> isolatedNodes = []   // nodes have no connections to any nodes, need to be displayed differently

        // add each experiment id associated with the project
        project.projectExperiments.each {
            queue.add(it.id)
        }
        // BFS to construct graph
        while (queue.size() > 0) {
            Long currentNode = queue.remove(0)         // remove the first one in the queue
            if (visitedNodes.contains(currentNode)) { // don't process one that is already processed
                continue;
            }
            ProjectExperiment pe = ProjectExperiment.findById(currentNode)
            visitedNodes.add(pe.id)                 // keep visited
            Node node = constructNode(pe)           // construct node
            if (isIsolatedNode(pe)) {
                isolatedNodes << node
            }
            else {
                nodes << node
                edges.addAll(constructEdges(pe, queue))   // construct edge
            }
        }
        def result = ["connectedNodes": nodes, "edges": edges, "isolatedNodes": isolatedNodes]
        print new JSON(result)
        return new JSON(result)
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
        def peAttributes = ['eid': pe?.experiment?.id,
                            'stage': pe?.stage?.label,
                            'assay': pe?.experiment?.assay?.id,
                            'ename': pe?.experiment?.experimentName
                            ]
        return new Node(id: pe?.id, keyValues: peAttributes)
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
        return label
    }

    @Override
    boolean equals(Object obj) {
        if (obj == null) { return false }
        if (obj.is(this)) { return true }
        if (obj.getClass() != getClass()) { return false }

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
