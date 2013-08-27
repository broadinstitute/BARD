package bardqueryapi

import bard.core.rest.spring.ProjectRestService
import bard.core.rest.spring.project.ProjectExperiment
import bard.core.rest.spring.project.ProjectStep
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.springframework.web.client.HttpClientErrorException
import org.springframework.http.HttpStatus

/**
 * This is a service that return a list of nodes and edges in JSON format to be displayed in views.
 * The service can be reused with different view implementation if view requires similar objects.
 * Otherwise, this service can be adapted to provide more information to view for displaying purpose.
 */
class ProjectExperimentRenderService {
    ProjectRestService projectRestService

    /**
     * Use BFS to construct graph from a project
     *
     * @param project
     * @return nodes and edges in JSON format
     */
    Map constructGraph(final Long bardProjectId, final Map<Long, String> experimentTypes = [:]) {
        Map result = [:]
        try {
            final List<ProjectStep> projectSteps = projectRestService.findProjectSteps(bardProjectId);
            final Collection<ProjectExperiment> projectExperiments = projectStepsToProjectExperiments(projectSteps)
            result = processProjectExperiments(projectExperiments, experimentTypes)
        } catch (HttpClientErrorException exp) {
            if (exp.statusCode != HttpStatus.NOT_FOUND) {
                log.error(exp)
            }
        } catch (Exception ee) {
            log.error(ee)
        }
        return result
    }

    Collection<ProjectExperiment> projectStepsToProjectExperiments(final List<ProjectStep> projectSteps) {
        final Map<Long, ProjectExperiment> projectExperimentMap = [:]
        for (ProjectStep projectStep : projectSteps) {
            projectStepToProjectExperiment(projectStep, projectExperimentMap)
        }
        return projectExperimentMap.values()
    }

    void projectStepToProjectExperiment(final ProjectStep projectStep, final Map<Long, ProjectExperiment> projectExperimentMap = [:]) {
        ProjectExperiment currentProjectExperiment = projectStep.nextBardExpt;
        if (currentProjectExperiment) {
            addStepToProjectExperiment(currentProjectExperiment, projectStep, projectExperimentMap, true)
        }
        currentProjectExperiment = projectStep.prevBardExpt;
        if (currentProjectExperiment) {
            addStepToProjectExperiment(currentProjectExperiment, projectStep, projectExperimentMap, false)
        }

    }
    /**
     *
     * @param isPrevious - if true add to list of previous steps, else add to list of following steps
     */
    void addStepToProjectExperiment(final ProjectExperiment currentProjectExperiment, final ProjectStep projectStep,
                                    final Map<Long, ProjectExperiment> projectExperimentMap, final boolean isPreceding) {
        ProjectExperiment projectExperiment = projectExperimentMap.get(currentProjectExperiment.capExptId)
        if (projectExperiment == null) {
            projectExperiment = currentProjectExperiment
        }
        if (isPreceding) {
            projectExperiment.addPrecedingProjectStep(projectStep)
        }
        else {
            projectExperiment.addFollowingProjectStep(projectStep)
        }
        projectExperimentMap.put(projectExperiment.capExptId, projectExperiment)
    }

    Map processProjectExperiments(final Collection<ProjectExperiment> projectExperiments, final Map<Long, String> experimentTypes = [:]) {
        final List<Long> visitedNodes = []        // as name said
        final List<Long> queue = []              // processing queue

        final Set<Edge> edges = new HashSet<Edge>()   // all edges
        final List<Node> nodes = []            // nodes have connections to other nodes
        final List<Node> isolatedNodes = []   // nodes have no connections to any nodes, need to be displayed differently
        final Map<Long, ProjectExperiment> projectExperimentMap = [:]
        // add each experiment id associated with the project
        projectExperiments.each {
            queue.add(it.capExptId)
            projectExperimentMap.put(it.capExptId, it)
        }
        buildNodesAndEdges(edges, queue, nodes, visitedNodes, isolatedNodes, projectExperimentMap, experimentTypes)
        countInOutEdges(edges, nodes)
        final Map result = ["connectedNodes": nodes, "edges": edges, "isolatedNodes": isolatedNodes]
        return result
    }
    // BFS to construct graph
    void buildNodesAndEdges(final Set<Edge> edges, final List<Long> queue, final List<Node> nodes, final List<Long> visitedNodes, final List<Node> isolatedNodes,
                            final Map<Long, ProjectExperiment> projectExperimentMap, final Map<Long, String> experimentTypes = [:]) {
        while (queue.size()) {
            final Long currentNode = queue.remove(0)         // remove the first one in the queue
            if (visitedNodes.contains(currentNode)) { // don't process one that is already processed
                continue;
            }
            final ProjectExperiment projectExperiment = projectExperimentMap.get(currentNode)
            if (projectExperiment) {
                visitedNodes.add(projectExperiment.capExptId)                 // keep visited
                final Node node = constructNode(projectExperiment, experimentTypes)           // construct node
                if (isIsolatedNode(projectExperiment)) {
                    isolatedNodes << node
                }
                else {
                    nodes << node
                    edges.addAll(constructEdges(projectExperiment, queue))   // construct edge
                }
            }
        }
    }
    /**
     * Count incoming edges and outgoing edges. This information will be used to position nodes on the display screen
     * @param edges
     * @param nodes
     */
    void countInOutEdges(final Set<Edge> edges, final List<Node> nodes) {
        def nodeInCount = [:]
        def nodeOutCount = [:]
        nodes.each {
            nodeInCount[it.id] = 0
            nodeOutCount[it.id] = 0
        }
        edges.each {Edge edge ->
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
     * @param projectExperiment
     * @return boolean
     */
    boolean isIsolatedNode(final ProjectExperiment projectExperiment) {
        return projectExperiment.followingProjectSteps.size() == 0 &&
                projectExperiment.precedingProjectSteps.size() == 0
    }
    /**
     * Each projectexperiment is a node, extract attributes may be displayed
     * @param projectExperiment
     * @return node
     */
    Node constructNode(final ProjectExperiment projectExperiment, final Map<Long, String> experimentTypes = [:]) {
        final String stageLabel = experimentTypes.get(projectExperiment.bardExptId)
        Map projectExperimentAttributes = [
                'eid': projectExperiment.capExptId,
                'bardExptId': projectExperiment.bardExptId,
                'stage': stageLabel,
                'assay': projectExperiment.capAssayId,
                'bardAssay': projectExperiment.bardAssayId,
                'ename': projectExperiment.name,
                'incount': 0,
                'outcount': 0,
                'aid': projectExperiment.pubchemAid
        ]
        return new Node(id: projectExperiment.capExptId, keyValues: projectExperimentAttributes)
    }

    /**
     * Given a projectexperiment, find all preceding and following projectstep and construct edges
     * @param projectExperiment
     * @param queue
     * @return a set of edge
     */
    Set<Edge> constructEdges(final ProjectExperiment projectExperiment, final List<Long> queue) {
        Set<Edge> edges = new HashSet()
        for (ProjectStep projectStep : projectExperiment.precedingProjectSteps) {
            edges.add(constructEdge(projectStep, queue))
        }
        for (ProjectStep projectStep : projectExperiment.followingProjectSteps) {
            edges.add(constructEdge(projectStep, queue))
        }
        return edges
    }

    /**
     * Given a projectstep, construct edge, add from and to nodes to processing queue
     * @param step
     * @param queue
     * @return edge
     */
    Edge constructEdge(final ProjectStep step, final List<Long> queue) {
        queue.add(step.nextBardExpt.capExptId)
        queue.add(step.prevBardExpt.capExptId)
        Edge edge = new Edge(from: step.prevBardExpt.capExptId,
                to: step.nextBardExpt.capExptId,
                label: step.edgeName)
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
