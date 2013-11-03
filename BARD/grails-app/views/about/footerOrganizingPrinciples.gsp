<%@ page import="bard.db.enums.ExperimentStatus; bard.db.enums.ContextType; bard.db.registration.DocumentKind; bard.db.model.AbstractContextOwner; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix"/>
    <meta name="layout" content="howto"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>How to search</title>
</head>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>Organizing principles of BARD</h2></article>
            <aside class="span2"></aside>
        </div>

    </div>
</div>


<article class="hero-block">
    <div class="container-fluid">
        <div class="lowkey-hero-area">
            <div class="row-fluid">
                <aside class="span2"></aside>
                <article class="span8">


                    <h3>
                        Overview
                    </h3>

                    <p>
                        BARD is organized in a novel way that lets users easily find information about projects, protocols, and experimental results, all while keeping the data organized in a manner that facilitates broader analyses across many datasets. The organizing principle is based around three entities that serve different purposes: Projects, Assay Definitions, and Experiments. The relationship of these entities provides both the experimental details associated with any given data point while also providing context for the intended purpose of experiments that were run by data depositors.
                    </p>


                    <h3>
                        Assay Definitions
                    </h3>

                    <p>
                        Assay definitions are the descriptions associated with the generation of assay data. An assay definition contains information about the protocol used to generate data, as well as the intended biological purpose of that protocol. Assay definitions are a unique, abstract description of a protocol that can be used to gain a particular biological insight; therefore they do not directly have data attached to the assay definition and do not have any fixed relationship to other protocols. For example, an assay definition would not be termed a “counterscreen assay” or a “confirmatory assay” because depending on how the assay definition is used in relation to others it could be either; it is also not directly associated with any particular set of compounds tested. Thus, the same assay definition can be used across many screening campaigns (e.g., a standard cytotoxicity protocol). The biology target associated with an assay definition only describes the hypothesis tested by that specific protocol, and additional inferences (such as the specificity of a tested compound) require additional information obtained by executing other assay definitions.
                    </p>

                    <p>
                        Assay definitions include details such as what was added to each reaction container or well (termed assay components); treatment variables such as time, temperature, or volume; how the signal was detected; and the information content of the measured signal (single parameter vs. multiplex, ratio, profile, etc.) In some cases, users can choose to define certain details at the time of data deposition instead of setting one fixed description, such as when the same protocol is sometimes run in 384 well format or 1536 well format.
                    </p>

                    <p>
                        Assay definitions are identified by an ADID.
                    </p>



                    <h3>
                        Experiments
                    </h3>
                    <p>
                        Experiments have one or more PubChem SIDs that identify which molecular substances were tested. Each submitted data point will include at the minimum an [PAC: ???]. In addition, experiments have measurement identifiers that specify what calculated values are being reported for those results, such as percent inhibition, IC50, etc. In most cases these measurements are related: in the case of IC50, many percent measurement values are aggregated to report an IC50. This relationship is important to define when planning to submit results for an experiment. It is used to generate the template for depositing results to BARD and indicates which of these results is considered the priority measurement(s) displayed by BARD. Often, the priority measurement is the result value(s) that a user would examine to determine whether they consider a compound “active.”
                    </p>

                    <p>
                        Experiments are identified by an EID. Compounds tested in experiments are identified by their PubChem substance ID, SID, and associated PubChem compound structure ID, CID.
                    </p>



                    <h3>
                        Projects
                    </h3>
                    <p>
                        Projects are groups of experiments that were executed for a larger purpose such as development of a molecular probe. Projects in BARD capture the order in which experiments were run and the purpose of those experiments in relation to each other and the larger project goal (e.g., “primary screen”, “counterscreen”, “confirmatory screen”). Projects also capture the biological purpose of the group of experiments taken as a whole. No one assay definition can identify the properties of compounds desired for probe or drug development; therefore, goals such as the relevant disease or specific biological target are defined by the project. Auxiliary information such as grant numbers can also be recorded within the project annotations.
                    </p>

                    <p>
                        Note that assay definitions are not directly associated with projects. As described above, the same protocol may be used for a different purpose across multiple projects. For example, the primary screen of one project may be a counterscreen of another project. Projects are defined only by the group of experiments associated to that project. These experiments are associated with assay definitions used to generate the data that are submitted with an experiment. It is only through this indirect relationship that assay definitions are connected to projects. Queries that identify matching projects will return all that project’s experiments and all those experiments’ assay definitions.
                    </p>


                    <p>
                        Projects are identified by a PID.
                    </p>

                    <IMG SRC="${resource(dir: 'images/bardHomepage', file: 'rdm_overview.png')}" ALIGN="top">

                    <h3>
                        Panels
                    </h3>


                    <p>
                        Projects are groups of experiments that were executed for a larger purpose such as development of a molecular probe. Projects in BARD capture the order in which experiments were run and the purpose of those experiments in relation to each other and the larger project goal (e.g., “primary screen”, “counterscreen”, “confirmatory screen”). Projects also capture the biological purpose of the group of experiments taken as a whole. No one assay definition can identify the properties of compounds desired for probe or drug development; therefore, goals such as the relevant disease or specific biological target are defined by the project. Auxiliary information such as grant numbers can also be recorded within the project annotations.
                    </p>

                    <p>
                        Panels are identified by a PLID.
                    </p>





                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>



</body>
</html>>