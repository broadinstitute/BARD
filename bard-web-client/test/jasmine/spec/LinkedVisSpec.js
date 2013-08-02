describe("Testing linkedVis.js", function () {
    var fakeData = {
        "Category" : [
            {     "CatIdx" : 0,
                "CatName" : "Biological process",
                "CatDescr" : "GO Biological process",
                "CatIdent" : "GO_biological_process_term"
            },
            {     "CatIdx" : 1,
                "CatName" : "Assay format",
                "CatDescr" : "Bard assay format",
                "CatIdent" : "assay_format"
            },
            {     "CatIdx" : 2,
                "CatName" : "Protein target",
                "CatDescr" : "Panther protein target",
                "CatIdent" : "assay_type"
            },
            {     "CatIdx" : 3,
                "CatName" : "Assay type",
                "CatDescr" : "Bard assay format",
                "CatIdent" : "protein_target"
            }],
        "Hierarchy" : [
            {     "CatRef" : 0,
                "HierType" : "Graph",
                "Structure" : {}
            },
            {     "CatRef" : 1,
                "HierType" : "Tree",
                "Structure" : {"struct" :
                    [{"name":"/", "children": [
                        {"name":"cell-based format", "assays": [0,2,4,6,10, 11, 12, 13, 14] },
                        {"name":"cell-free format", "assays": [], "children": [
                            {"name":"whole-cell lysate format", "assays": [3]}
                        ]},
                        {"name":"biochemical format", "assays": [5, 7, 9], "children": [
                            {"name":"protein format", "assays": [], "children": [
                                {"name":"single protein format", "assays": [1, 8, 15]}
                            ]}
                        ]}
                    ]}]}
            },
            {     "CatRef" : 2,
                "HierType" : "Tree",
                "Structure" : {"struct" :
                    [{"name":"/", "children": [
                        {"name":"signaling molecule", "assays": [0] },
                        {"name":"enzyme modulator", "assays": [2,4], "children": [
                            {"name":"G-protein modulator", "assays": [7]},
                            {"name":"G-protein", "assays": [5], "children": [
                                {"name":"heterotrimeric G-protein", "assays": [1]},
                                {"name":"small GTPase", "assays": [3,6]}
                            ]}
                        ]},
                        {"name":"transporter", "assays": [], "children": [
                            {"name":"ATP-binding cassette", "assays": [12,13]},
                            {"name":"ion channel", "assays": [11], "children": [
                                {"name":"anion channel", "assays": [10,14]},
                                {"name":"voltage-gated ion channel", "assays": [9], "children": [
                                    {"name":"voltage-gated potassium channel", "assays": [8,15]}
                                ]}
                            ]}
                        ]}
                    ]}]}
            },
            {     "CatRef" : 3,
                "HierType" : "Tree",
                "Structure" : {}
            }],
        "Assays" : [
            {     "AssayIdx" : 0,
                "AssayName" : "Radiotracer Incision Assay (RIA) for Inhibitors of Human Apurinic/apyrimidinic Endonuclease 1 (APE1)",
                "AssayAc" : 1,
                "AssayIn" : 0,
                "AssayId" : 1017
            },
            {     "AssayIdx" : 1,
                "AssayName" : "Inhibitors of Bloom's syndrome helicase: Efflux Ratio Profiling Assay",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 1730
            },
            {     "AssayIdx" : 2,
                "AssayName" : "Inhibitors of Bloom's syndrome helicase: Aqueous Profiling Assay",
                "AssayAc" : 1,
                "AssayIn" : 0,
                "AssayId" : 1732
            },
            {     "AssayIdx" : 3,
                "AssayName" : "Inhibitors of Bloom's syndrome helicase: Metabolic Stability Profiling",
                "AssayAc" : 1,
                "AssayIn" : 0,
                "AssayId" : 1733
            },
            {     "AssayIdx" : 4,
                "AssayName" : "Inhibitors of APE1: Caco-2 Cell Permeability Profiling",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 1735
            },
            {     "AssayIdx" : 5,
                "AssayName" : "Inhibitors of APE1: Mouse Plasma Stability Profiling",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 1612
            },
            {     "AssayIdx" : 6,
                "AssayName" : "Inhibitors of APE1: Metabolic Stability Profiling",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 1651
            },
            {     "AssayIdx" : 7,
                "AssayName" : "Inhibitors of APE1: Aqueous Solubility Profiling",
                "AssayAc" : 1,
                "AssayIn" : 0,
                "AssayId" : 1604
            },
            {     "AssayIdx" : 8,
                "AssayName" : "qHTS Assay for Inhibitors of Bloom's syndrome helicase (BLM)",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 2483
            },
            {     "AssayIdx" : 9,
                "AssayName" : "qHTS Assay for Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 2472
            },
            {     "AssayIdx" : 10,
                "AssayName" : "qHTS FP-Based Assay for Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 2623
            },
            {     "AssayIdx" : 11,
                "AssayName" : "qHTS Assay for Inhibitors of BRCT-Phosphoprotein Interaction (Green Fluorophore)",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 3402
            },
            {     "AssayIdx" : 12,
                "AssayName" : "qHTS Assay for Inhibitors of BRCT-Phosphoprotein Interaction (Red Fluorophore)",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 3418
            },
            {     "AssayIdx" : 13,
                "AssayName" : "Homologous recombination_Rad 51_dose response_2",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 3594
            },
            {     "AssayIdx" : 14,
                "AssayName" : "Homologous recombination - Rad 51",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 3874
            },
            {     "AssayIdx" : 15,
                "AssayName" : "Late stage assay provider results from the probe development effort to identify inhibitors of Wee1 degradation: luminescence-based cell-based assay to identify inhibitors of Wee1 degradation",
                "AssayAc" : 0,
                "AssayIn" : 1,
                "AssayId" : 537
            }],
        "AssayCross" : [
            {     "AssayRef" : 0,
                "data" : {
                    "0" : "none",
                    "1" : "cell-based format",
                    "2" : "protein-small molecule interaction assay",
                    "3" : "signaling molecule"
                }
            },
            {     "AssayRef" : 2,
                "data" : {
                    "0" : "regulation of gene expression",
                    "1" : "cell-based format",
                    "2" : "protein expression assay",
                    "3" : "enzyme modulator"
                }
            },
            {     "AssayRef" : 15,
                "data" : {
                    "0" : "bontoxilysin activity",
                    "1" : "single protein format",
                    "2" : "direct enzyme activity assay",
                    "3" : "transporter"
                }
            },
            {     "AssayRef" : 1,
                "data" : {
                    "0" : "plectin-1",
                    "1" : "single protein format",
                    "2" : "protein-protein interaction assay",
                    "3" : "enzyme modulator"
                }
            },
            {     "AssayRef" : 3,
                "data" : {
                    "0" : "none",
                    "1" : "whole-cell lysate format",
                    "2" : "none",
                    "3" : "enzyme modulator"
                }
            },
            {     "AssayRef" : 4,
                "data" : {
                    "0" : "regulation of gene expression",
                    "1" : "cell-based format",
                    "2" : "protein expression assay",
                    "3" : "enzyme modulator"
                }
            },
            {     "AssayRef" : 6,
                "data" : {
                    "0" : "bontoxilysin activity",
                    "1" : "cell-based format",
                    "2" : "functional assay",
                    "3" : "enzyme modulator"
                }
            },
            {     "AssayRef" : 5,
                "data" : {
                    "0" : "none",
                    "1" : "biochemical format",
                    "2" : "fluorescence interference assay",
                    "3" : "enzyme modulator"
                }
            },
            {     "AssayRef" : 7,
                "data" : {
                    "0" : "plectin-1",
                    "1" : "biochemical format",
                    "2" : "protein-protein interaction assay",
                    "3" : "enzyme modulator"
                }
            },
            {     "AssayRef" : 8,
                "data" : {
                    "0" : "plectin-1",
                    "1" : "single protein format",
                    "2" : "protein-protein interaction assay",
                    "3" : "transporter"
                }
            },
            {     "AssayRef" : 9,
                "data" : {
                    "0" : "bontoxilysin activity",
                    "1" : "biochemical format",
                    "2" : "direct enzyme activity assay",
                    "3" : "transporter"
                }
            },
            {     "AssayRef" : 10,
                "data" : {
                    "0" : "kinase activity",
                    "1" : "cell-based format",
                    "2" : "protein-small molecule interaction assay",
                    "3" : "transporter"
                }
            },
            {     "AssayRef" : 11,
                "data" : {
                    "0" : "none",
                    "1" : "cell-based format",
                    "2" : "reporter-gene assay",
                    "3" : "transporter"
                }
            },
            {     "AssayRef" : 12,
                "data" : {
                    "0" : "cell death",
                    "1" : "cell-based format",
                    "2" : "transporter assay",
                    "3" : "transporter"
                }
            },
            {     "AssayRef" : 13,
                "data" : {
                    "0" : "regulation of gene expression",
                    "1" : "cell-based format",
                    "2" : "protein expression assay",
                    "3" : "transporter"
                }
            },
            {     "AssayRef" : 14,
                "data" : {
                    "0" : "immunological synapse formation",
                    "1" : "cell-based format",
                    "2" : "gene-expression assay",
                    "3" : "transporter"
                }
            }]
    };



    describe('data manipulation module', function () {

        it('Make it through loading a realistic data set test', function () {

            expect (linkedVizData.validateLinkedData).toBeDefined()  ;
            linkedVizData.parseData(fakeData);
            expect(linkedVizData.validateLinkedData()).toBe(true);
        });
    });


    describe('test the number of widgets counter', function () {

        it('tests count of faked data looks for result', function () {

            expect (linkedVizData.numberOfWidgets()).toBe(4);

        });
    });


    describe('test the number of widgets counter', function () {

        it('tests known count of faked data', function () {

            expect (linkedVizData.retrieveLinkedData().length).toBe(16);

        });
    });



    describe('test ability to dynamically determine if hierarchy data exists', function () {

        it('tests faked data, where data type 0 has no hierarchy but datatype 1 does', function () {

            expect (linkedVizData.extendedHierarchyDataExists(0)).toBe(false);
            expect (linkedVizData.extendedHierarchyDataExists(1)).toBe(true);


        });
    });



    describe('test CID value getter and setter', function () {

        it('should start with zero, set to a new value, and then remember it', function () {

            expect (linkedVizData.cid()).toBe(0);
            linkedVizData.cid(47);
            expect (linkedVizData.cid()).not.toBe(0);
            expect (linkedVizData.cid()).toBe(47);

        });
    });




});










