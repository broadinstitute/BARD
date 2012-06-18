insert into assay_document(assay_document_id,
 assay_id,
 document_name,
 document_type,
 document_content)
values
 (1,
 1,
 'Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2)',
 'Protocol',
 empty_clob()
 );
insert into assay_document(assay_document_id,
 assay_id,
 document_name,
 document_type,
 document_content)
values
 (2,
 1,
 'Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2) pt 2',
 'Protocol',
 empty_clob()
 );

update assay_document set document_content = 'Assay Overview: '
 || chr(13) || chr(10) || ' Compounds identified from a previously described set of experiments entitled '
 || '"Primary high-throughput assay for chemical inhibitors of Rho kinase 2 (Rhok2) activity" (PubChem AID = 604) were selected for testing '
 || 'in this assay. Out of 212 compounds identified during the primary screen, 206 compounds were assessed in this dose response experiment. '
 || 'Each compound was assayed in 10 point, 1:3 serial dilutions starting at a nominal test concentration of 60 micromolar.'
 || chr(13) || chr(10) || 'As with the '
 || 'primary screen, the assay is based on ability of Rhok2 to phosphorylate a specific peptide sequence derived from its substrate - '
 || 'ribosomal protein S6 (amino acid residues 229-239). Rhok2 uses ATP as a donor of phosphate for the phosphorylation of the substrate, '
 || 'which leads to the depletion of ATP in the reaction mix. An assay kit (#Kinase-Glo#, Promega) was used to quantify enzyme activity. '
 || 'Using this kit, residual amounts of ATP are measured by a secondary enzymatic reaction, through which luciferase utilizes the remaining '
 || 'ATP to produce luminescence. Luminescent signal is directly proportional to ATP concentration and inversely proportional to Rhok2 activity. '
 || 'This dose response assay was conducted in 1536 well plate format. Each concentration was tested nominally in triplicate.'
 || chr(13) || chr(10) || 'Protocol ' ||chr(13) ||chr(10)
 || 'Summary: ' ||chr(13) ||chr(10) || '1.25 microliters of solution containing 20 micromolar ATP and 20 micromolar S6 peptide (substrate) in assay buffer (50 millimolar '
 || 'HEPES pH 7.3, 10 millimolar MgCl2, 0.1% BSA, 2 millimolar DTT) were dispensed in 1536 microtiter plate. 15 nanoliters of test compound or '
 || 'positive and negative control (2.12 millimolar Y-27632 and DMSO, respectively) were then added to the appropriate wells. Each compound '
 || 'dilution was assayed in triplicate, for a nominal total of 30 data points per dose response curve. '
where assay_document_id = 1;

update assay_document set document_content =
    ' The enzymatic reaction was initiated '
 || 'by dispensing 1.25 microliters of 8 nanomolar Rhok2 solution in assay buffer (50 millimolar HEPES pH 7.3, 10 millimolar MgCl2, 0.1% BSA, '
 || '2 millimolar DTT). After 2 hours of incubation at 25 degrees Celsius, 2.5 microliters of Kinase Glo reagent (Promega Corporation, Madison, '
 || 'WI) was added to each well. Plates were incubated for 10 minutes and luminescence was read on Perkin-Elmer Viewlux for 60 seconds.'
 || chr(13) || chr(10) || 'Each '
 || 'compound was tested in triplicate. The percent inhibition for each well has been calculated as follows: '
 || chr(13) || chr(10) || '%inhibition = (test_compound - '
 || 'median_ negative_control)/(median_positive_control - median_negative_control)*100'
 || chr(13) || chr(10) || 'where the positive control is Y-27632 (13 micromolar) '
 || 'and negative control is DMSO only. '
 || chr(13) || chr(10) || 'For each compound, percentage inhibitions were plotted against compound concentration. A four parameter '
 || 'equation describing a sigmoidal dose-response curve was then fitted with adjustable baseline using Assay Explorer software (MDL Information '
 || 'Systems). The reported IC50 values were generated from fitted curves by solving for X-intercept at the 50% inhibition level of Y-intercept.'
 || 'In cases where the highest concentration tested (i.e. 60 micromolar) did not result in greater than 50% inhibition, the IC50 was determined '
 || 'manually as greater than 60 micromolar. '
 || chr(13) || chr(10) || 'Compounds with IC50 values of greater than 10 micromolar were considered inactive, compounds with '
 || 'IC50 equal or less than 10 micromolar are considered active. '
 || chr(13) || chr(10) || 'The activity score was calculated based on pIC50 values for compounds for '
 || 'which an exact IC50 value was calculated and based on the observed pIC50 range, specifically the maximum lower limit of the pIC50 value '
 || 'as calculated from the lowest concentration for which greater than 50% inhibition is observed. This results in a conservative estimate '
 || 'of the activity score for compounds for which no exact IC50 value is given while maintaining a reasonable rank order of all compounds tested.'
where assay_document_id = 2;