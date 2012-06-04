package bardqueryapi

import org.codehaus.groovy.grails.commons.GrailsApplication

@Mixin(QueryApiControllerHelper)
class QueryApiController {
    QueryAssayApiService queryAssayApiService
    QueryCompoundApiService queryCompoundApiService
    QueryProjectApiService queryProjectApiService
    QueryTargetApiService queryTargetApiService
    GrailsApplication grailsApplication

    def index() {
        render(view: "index.gsp")
    }

    def searchTarget() {
        final TargetDisplayType targetDisplayType = params.targetType as TargetDisplayType
        if (params.target) {
            switch (targetDisplayType) {
                case TargetDisplayType.Accession:
                    final String accessionUrl = "/bard/rest/v1/targets/accession/" + params.target
                    final accession = queryTargetApiService.findProteinByUniprotAccession(accessionUrl)
                    render accession as String
                    return
                case TargetDisplayType.GeneId:
                    final String geneUrl = "/bard/rest/v1/targets/geneid/" + params.target
                    final gene = queryTargetApiService.findProteinByGeneId(geneUrl)
                    render gene as String
                    return
                default:
                    throw new RuntimeException("Unhandled " + targetDisplayType)
                    return
            }
        } else {
            render "Target parameter required"
        }
    }

    def searchProject() {
        final ProjectDisplayType projectDisplayType = params.projectType as ProjectDisplayType
        if (params.project) {
            final String projectUrl = "/bard/rest/v1/projects/" + params.project
            switch (projectDisplayType) {
                case ProjectDisplayType.Project:
                    render queryProjectApiService.findProject(projectUrl)
                    return
                case ProjectDisplayType.Probes:
                    render queryProjectApiService.findProbesByProject(projectUrl)
                    return
                case ProjectDisplayType.Assays:
                    render queryProjectApiService.findAssaysByProject(projectUrl)
                    return
                default:
                    render queryProjectApiService.findProjects()
                    return
            }
        } else {
            render queryProjectApiService.findProjects()
        }
    }


    def searchCompound() {
        if (params.compound) {
            final def compoundFormat = params.compoundFormat as CompoundFormat
            final def compoundDisplayType = params.compoundType as CompoundDisplayType
            findCompoundByFormat(compoundFormat, compoundDisplayType, this.queryCompoundApiService)
            return
        } else {
            render "Compound parameter required"
            return
        }
    }

    def searchAssay() {
        final AssayDisplayType assayDisplayType = params.assayType as AssayDisplayType
        if (params.assay) {
            final String assayUrl = "/bard/rest/v1/assays/" + params.assay
            switch (assayDisplayType) {
                case AssayDisplayType.Compounds:
                    render queryAssayApiService.findCompoundsByAssay(assayUrl, null)
                    return
                case AssayDisplayType.Publications:
                    render queryAssayApiService.findPublicationsByAssay(assayUrl)
                    return
                case AssayDisplayType.Targets:
                    render queryAssayApiService.findProteinTargetsByAssay(assayUrl)
                    return
                case AssayDisplayType.Assay:
                    render queryAssayApiService.findAssayByAid(assayUrl)
                    return
                default:
                    throw new RuntimeException("Unhandled " + assayDisplayType)
            }
        } else {
            render "Assay parameter required"
        }
    }
}
class QueryApiControllerHelper {


    def handleProbes(final def compoundFormat, final QueryCompoundApiService queryCompoundApiService) {
        final String compoundUrl = "/bard/rest/v1/compounds/probeid/" + params.compound
        switch (compoundFormat) {
            case CompoundFormat.DAY_LIGHT_SMILES:
            case CompoundFormat.SDF:
                return queryCompoundApiService.findCompoundByProbeId(compoundUrl, [Accept: compoundFormat.getMimeType()])
            case CompoundFormat.DEFAULT:
                return queryCompoundApiService.findCompoundByProbeId(compoundUrl, null)
            default:
                throw new RuntimeException("Unhandled " + compoundFormat)
        }
    }

    def findCompoundByFormat(final def compoundFormat, final def compoundDisplayType, final QueryCompoundApiService queryCompoundApiService) {

        switch (compoundDisplayType as CompoundDisplayType) {
            case CompoundDisplayType.PROBEID:
                render handleProbes(compoundFormat, queryCompoundApiService)
                return
            case CompoundDisplayType.CID:
                render handleCIDs(compoundFormat, queryCompoundApiService)
                return
            case CompoundDisplayType.SID:
                render handleSIDs(compoundFormat, queryCompoundApiService)
                return
            default:
                throw new RuntimeException("Unhandled " + compoundDisplayType)
        }
    }

    def handleSIDs(final def compoundFormat, final QueryCompoundApiService queryCompoundApiService) {
        final String compoundUrl = "/bard/rest/v1/compounds/sid/" + params.compound
        switch (compoundFormat as CompoundFormat) {
            case CompoundFormat.DAY_LIGHT_SMILES:
            case CompoundFormat.SDF:
                return queryCompoundApiService.findCompoundBySID(compoundUrl, [Accept: compoundFormat.getMimeType()])
            case CompoundFormat.DEFAULT:
                return queryCompoundApiService.findCompoundBySID(compoundUrl, null)
            default:
                throw new RuntimeException("Unhandled " + compoundFormat)
        }
    }

    def handleCIDs(final def compoundFormat, final QueryCompoundApiService queryCompoundApiService) {
        final String compoundUrl = "/bard/rest/v1/compounds/" + params.compound
        switch (compoundFormat as CompoundFormat) {
            case CompoundFormat.DAY_LIGHT_SMILES:
            case CompoundFormat.SDF:
                return queryCompoundApiService.findCompoundByCID(compoundUrl, [Accept: compoundFormat.getMimeType()])

            case CompoundFormat.DEFAULT:
                return queryCompoundApiService.findCompoundByCID(compoundUrl, null)

            default:
                throw new RuntimeException("Unhandled " + compoundFormat)
        }
    }

}
public enum ProjectDisplayType {
    Probes,
    Assays,
    Project
}
public enum AssayDisplayType {
    Targets,
    Publications,
    Compounds,
    Assay
}
public enum TargetDisplayType {
    Accession,
    GeneId
}
public enum CompoundDisplayType {
    CID,
    PROBEID,
    SID

}
public enum CompoundFormat {
    DEFAULT("Some mime type"),
    SDF("chemical/x-mdl-sdfile"),
    DAY_LIGHT_SMILES("chemical/x-daylight-smiles")


    final String mimeType

    private CompoundFormat(String mimeType) {
        this.mimeType = mimeType
    }

    public String getMimeType() {
        return this.mimeType
    }
}
