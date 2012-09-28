package bard.dm.minimumassayannotation

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/26/12
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
class AttributeNameMappingBuilder {
    Map build() {
        Map attributeNameMapping = ['[detector] assay component (type in)': 'assay component',
                '[detector] assay component role': 'detection role',
                'assay component concentration (type in)': 'assay component concentration',
                'assay component concentration units': 'concentration unit',
                'biological component base name': 'assay component',
                'species': 'species name',
                'assay detection': 'detection method type',
                'assay target type': 'assay type',
                'component name (type in)': 'assay component name',
                'assay readout content': 'assay readout',
                'assay readout type': 'readout type',
                'biochemical': 'biochemical format',
                'biorad chemidocxrs imaging system': 'Biorad Chemidocxrs Imaging System',
                'cell based: lysed cell': 'cell-based format',
                'cell-based: live cell': 'cell-based format',
                'cells/ml': 'cells per milliliter',
                'chemically labeled protein': 'chemically labeled protein',
                'detection instrument': 'detection instrument name',
                'fluorescence:other': 'fluorescence',
                'fm': 'femtomolar',
                'grams-per-liter': 'gram per liter',
                'imaging methods': 'imaging method',
                'in cell analyzer': 'IN cell analyzer',
                'kodak biomax mr-1': 'Kodak Biomax MR-1',
                'luminescence:other': 'luminescence',
                'Microscope cover slip  22 m^2': 'Microscope Cover Slip  22 mm^2',
                'mm': 'millimolar',
                'moles-per-liter': 'molar',
                'ng/ml': 'nanogram per milliliter',
                'nm': 'nanomolar',
                'optical densitometry': 'optical densitometry',
                'oryctolagus cuniculus': 'Oryctolagus Cuniculus',
                'pm': 'picomolar',
                'radiometric': 'radiometry method',
                'signal direction': 'readout signal direction',
                'spectramax plus 384 microplate reader': 'Spectramax Plus 384 Microplate Reader',
                'typhoon 8600 variable mode imager': 'Typhoon 8600 Variable Mode Imager',
                'um': 'micromolar',
                'µm': 'micromolar',
                '# concentration points': 'number of points',
                '# replicates': 'number of replicates',
                'uniprot': 'UniProt',
                'Aequorea Victoria': 'Aequorea victoria',
                'DNA-small molecule': 'DNA-small molecule',
                'DNA-small molecule interaction assay': 'DNA-small molecule interaction assay',
                'IMAP Kinase assay kit': 'IMAP Kinase assay kit',
                'SUMOylation assay': 'SUMOylation assay',
                'fluorescence interference assay': 'fluorescence interference assay',
                'ubiquitination assay': 'ubiquitination assay',
                'CID': 'PubChem CID',
                'BD Bioscience LSR II': 'BD Bioscience LSR II',
                'BioTek Synergy II plate reader': 'BioTek Synergy II plate reader',
                'MOI': 'mode of infection',
                'Microbeta scintillation counter': 'Microbeta scintillation counter',
                'Streptomyces Avidinii': 'Streptomyces avidinii',
                't-4 bacteriophage': 't-4 bacteriophage',
                'ChemBank': 'ChemBank',
                'DTP.NCI': 'Developmental Therapeutics Program, National Cancer Institute',
                'Vanderbilt, Vanderbilt Chemistry': 'Vanderbilt Screening Center for GPCRs, Ion Channels and Transporters',
                'SRI, SRI Screening': 'Southern Research Institute',
                'gram per liter': 'milligram per milliliter',
                '47-mer dsDNA': '47-mer dsDNA',
                'log10 molar': 'log10 molar',
                'negative log10 molar': 'negative log10 molar',
                'number-per-liter': 'number per liter',
                'number-per-well': 'number per well',
                'uIU/mL': 'micro interational unit per milliliter',
                '--': '',
                'Result type': 'result detail']

        return attributeNameMapping
    }
}
