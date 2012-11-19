package bard.core;

import bard.core.interfaces.AssayCategory;
import bard.core.interfaces.AssayRole;
import bard.core.interfaces.AssayType;
import bard.core.interfaces.AssayValues;

import java.util.*;


public class Assay extends Entity implements AssayValues {
    private static final long serialVersionUID = 0x740b685435192d40l;

    protected long capAssayId;
    protected String protocol; // assay protocol
    protected String comments; // assay comments
    protected AssayType type;
    protected AssayRole role;
    protected AssayCategory category;

    protected List<Project> projects = new ArrayList<Project>();
    protected List<Experiment> experiments = new ArrayList<Experiment>();
    protected List<Publication> publications = new ArrayList<Publication>();
    protected List<Biology> targets = new ArrayList<Biology>();

    public Assay() {
    }

    public Assay(String name) {
        super(name);
    }

    public long getCapAssayId() {
        return capAssayId;
    }

    public void setCapAssayId(long capAssayId) {
        this.capAssayId = capAssayId;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public void setType(AssayType type) {
        this.type = type;
    }

    public AssayType getType() {
        return type;
    }

    public void setCategory(AssayCategory category) {
        this.category = category;
    }

    public AssayCategory getCategory() {
        return category;
    }

    public void setRole(AssayRole role) {
        this.role = role;
    }

    public AssayRole getRole() {
        return role;
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public Collection<Project> getProjects() {
        return Collections.unmodifiableCollection(projects);
    }

    public void addExperiment(Experiment expr) {
        experiments.add(expr);
    }

    public Collection<Experiment> getExperiments() {
        return Collections.unmodifiableCollection(experiments);
    }
    public void addPublication(Publication expr) {
        publications.add(expr);
    }
    public Collection<Publication> getPublications() {
        return Collections.unmodifiableCollection(publications);
    }
    public void addTarget(Biology target) {
        targets.add(target);
    }

    public Collection<Biology> getTargets() {
        return Collections.unmodifiableCollection(targets);
    }

    public int getPublicationCount() {
        return publications.size();
    }

    public int getProjectCount() {
        return projects.size();
    }

    public int getExperimentCount() {
        return experiments.size();
    }

    public int getTargetCount() {
        return targets.size();
    }
}
