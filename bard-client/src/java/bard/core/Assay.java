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

    public void add(Project proj) {
        projects.add(proj);
    }

    public boolean remove(Project proj) {
        return projects.remove(proj);
    }

    public Collection<Project> getProjects() {
        return Collections.unmodifiableCollection(projects);
    }

    public Iterator<Project> projects() {
        return getProjects().iterator();
    }

    public void add(Experiment expr) {
        experiments.add(expr);
    }

    public boolean remove(Experiment expr) {
        return experiments.remove(expr);
    }

    public Collection<Experiment> getExperiments() {
        return Collections.unmodifiableCollection(experiments);
    }

    public Iterator<Experiment> experiments() {
        return getExperiments().iterator();
    }

    public void add(Publication expr) {
        publications.add(expr);
    }

    public boolean remove(Publication expr) {
        return publications.remove(expr);
    }

    public Collection<Publication> getPublications() {
        return Collections.unmodifiableCollection(publications);
    }

    public Iterator<Publication> publications() {
        return getPublications().iterator();
    }

    public void add(Biology target) {
        targets.add(target);
    }

    public boolean remove(Biology target) {
        return targets.remove(target);
    }

    public Collection<Biology> getTargets() {
        return Collections.unmodifiableCollection(targets);
    }

    public Iterator<Biology> targets() {
        return getTargets().iterator();
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


    public String toString() {
        return getClass().getName() + "{protocol=" + protocol + ",comments="
                + comments + ",type=" + type + ",role=" + role + ",category=" + category
                + ",projects=" + projects.size() + ",experiments=" + experiments.size()
                + ",publications=" + publications.size() + ",targets=" + targets.size()
                + "," + super.toString() + "}";
    }
}
