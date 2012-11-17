package bard.core;

import bard.core.interfaces.ExperimentCategory;
import bard.core.interfaces.ExperimentRole;
import bard.core.interfaces.ExperimentType;
import bard.core.interfaces.ExperimentValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class Experiment extends Entity implements ExperimentValues {
    private static final long serialVersionUID = 0x85b74f051cc5eae4l;

    protected List<Project> projects = new ArrayList<Project>();
    protected Assay assay;
    protected ExperimentType type;
    protected ExperimentRole role;
    protected ExperimentCategory category;
    protected Long pubchemAid;

    public Experiment() {
    }

    public Experiment(String name) {
        super(name);
    }

    public Long getPubchemAid() {
        return pubchemAid;
    }

    public void setPubchemAid(Long pubchemAid) {
        this.pubchemAid = pubchemAid;
    }

    public Assay getAssay() {
        return assay;
    }

    public void setAssay(Assay assay) {
        this.assay = assay;
    }

    public void setType(ExperimentType type) {
        this.type = type;
    }

    public ExperimentType getType() {
        return type;
    }

    public void setCategory(ExperimentCategory category) {
        this.category = category;
    }

    public ExperimentCategory getCategory() {
        return category;
    }

    public void setRole(ExperimentRole role) {
        this.role = role;
    }

    public ExperimentRole getRole() {
        return role;
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public Collection<Project> getProjects() {
        return Collections.unmodifiableCollection(projects);
    }

    public int getProjectCount() {
        return projects.size();
    }
}
