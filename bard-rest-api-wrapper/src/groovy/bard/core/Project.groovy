package bard.core

import bard.core.interfaces.ProjectValues


class Project extends Entity implements ProjectValues {

    protected List<Probe> probes = new ArrayList<Probe>();

    public Project() {}

    public Project(String name) {
        super(name);
    }

    public void addProbe(Probe probe) {
        probes.add(probe);
    }

    public List<Probe> getProbes() {
        return Collections.unmodifiableList(probes);
    }
}
