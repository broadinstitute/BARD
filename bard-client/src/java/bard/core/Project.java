package bard.core;

import bard.core.interfaces.ProjectValues;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;




public class Project extends Entity implements ProjectValues {
    private static final long serialVersionUID = 0x6e0a525382fcc15dl;

    protected List<Assay> assays = new ArrayList<Assay>();
    protected List<Experiment> experiments = new ArrayList<Experiment>();
    protected List<Probe> probes = new ArrayList<Probe>();

    public Project () {}
    public Project (String name) {
        super (name);
    }
    public void add (Probe probe) {
        probes.add(probe);
    }
    public boolean remove (Probe probe) {
        return probes.remove(probe);
    }
    public List<Probe> getProbes () {
        return Collections.unmodifiableList(probes);
    }
    public Iterator<Probe> probes () { return getProbes().iterator(); }
    public int getProbeCount () { return probes.size(); }

    public void add (Assay assay) {
        assays.add(assay);
    }
    public boolean remove (Assay assay) {
        return assays.remove(assay);
    }
    public Collection<Assay> getAssays () {
        return Collections.unmodifiableCollection(assays);
    }
    public Iterator<Assay> assays () { return getAssays().iterator(); }
    public int getAssayCount () { return assays.size(); }

    public void add (Experiment expr) {
        experiments.add(expr);
    }
    public boolean remove (Experiment expr) {
        return experiments.remove(expr);
    }
    public Collection<Experiment> getExperiments () {
        return Collections.unmodifiableCollection(experiments);
    }
    public Iterator<Experiment> experiments () {
        return getExperiments().iterator();
    }
    public int getExperimentCount () { return experiments.size(); }

    public String toString () {
        return getClass().getName()+"{assays="+assays.size()
            +",experiments="+experiments.size()+","+super.toString()+"}";
    }
}
