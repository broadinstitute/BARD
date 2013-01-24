package bard.db.model

import bard.db.dictionary.Descriptor
import bard.db.registration.AssayContext

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/7/13
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractContextOwner {

    abstract List getContexts()

    public static class ContextGroup {
        String key;
        String description;
        List<AbstractContext> value;
    }

    /**
     * Create a map where all the assayContexts are grouped a common root in the ontology hierarchy based on a prefered
     * descriptor for the context.
     *
     * @return a Map keyed by the first 2 levels of the ontology hierarchy path with a each key having a list of assayContexts
     */
    List<ContextGroup> groupContexts() {
        Map<String, List<AbstractContext>> mapByPath = getContexts().groupBy { AbstractContext context ->
            context.getContextGroup().toLowerCase().trim()
        }

        def groupDesc = [
                "assay protocol> assay component>":"What is actually in the well?\n" +
                        "What role does it serve in the assay? \n" +
                        "\n" +
                        "What kind of component is it? A cell (transfected?) Organism (microbe?) A protein (fusion?\n" +
                        "Truncation?) A DNA? A kit?\n" +
                        "\n" +
                        "If a biological component, what is the name of the unmodified, publicly referenced parent?\n" +
                        "(e.g., parent cell line, WT protein- give a reference if available, e.g. Uniprot Q1R23)\n" +
                        "\n" +
                        "For small molecules, add with the Pubchem CID. For modified proteins, name it (e.g., GASC1 1-420 GST fusion). For modified cells, name it (e.g., MCF 7 BRCA1-Luciferase reporter)\n" +
                        "\n" +
                        "If a biological component, what species did it originate from?\n"+
                        "\n" +
                        "Contents such as buffers, medium, etc. not necessary unless they define a difference from another assay, "+
                        "e.g., high ATP vs. low ATP for competitive vs. non-competitive inhibitors.",
                "assay protocol> assay design>":"", // Assay method, detection method.  Kind of an overlap with assay readout
                "assay protocol> assay format>":"",  // tiny number of values.  One card at most under this.
                "assay protocol> assay readout>":"How did you measure the thing you measured?" +
                        "Reporter genes, CellTiterGlo, etc. are Bioluminescence.\n" +
                        "Include wavelengths for absorbance or fluorescence assays.  If more than one channel, include one ex/em pair." +
                        "" +
                        "What information is the measurement you made giving you?\n" +
                        "\n" +
                        "Is it a single or multiparameter output?\n" +
                        "\n" +
                        "Is the raw value you’re using (pre-normalization to control) a direct measurement (RLU, RFU) or a calculated value (ratio of RFU, FP) or a time course (e.g., slope or T1 – T0)?\n" +
                        "\n" +
                        "For active compounds, what do you expect the change in the raw value to be?  Typically inhibition = loss of signal, activation = gain of signal, but there are exceptions, such as measurements of substrate depletion (e.g, KinaseGlo.)",
                "assay protocol> assay type>":"", // relatively small list
/*
                "biology> molecular interaction>":"",
                "biology>":"",
                "result type> item count>":"",
                "project management> project information>":"",
                "project management> experiment>":""
        */]


        mapByPath.keySet().each { if(!groupDesc.containsKey(it)) groupDesc.put(it, "") }

        return (groupDesc.keySet() as List).collect {
            def group = mapByPath.get(it)
            if (group == null)
                group = []

            return new ContextGroup(key: it, description: groupDesc.get(it), value: group)
        }
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
}
