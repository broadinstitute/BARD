package db
/**
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class Panel extends DatabaseConnectivity {
	public static def getPanelOverviewByName(String name) {
		def panelOverview = [:]
		def sql = DatabaseConnectivity.getSql()
		sql.eachRow(PanelQueries.PANEL_SUMMARY, [name]) {
			panelOverview = it.toRowResult()
		}
		return panelOverview
	}

	public static def getPanelOverviewById(int plid) {
		def panelOverview = [:]
		def sql = DatabaseConnectivity.getSql()
		sql.eachRow(PanelQueries.PANEL_SUMMARY_BYID, [plid]) {
			panelOverview = it.toRowResult()
		}
		return panelOverview
	}

	public static def getPanelAssays(int panelId){
		def assayIds = []
		def sql = DatabaseConnectivity.getSql()
		sql.eachRow(PanelQueries.PANEL_ASSAYS, [panelId]) {
			assayIds.add(it.ADID)
		}
		return assayIds
	}
}
