package main.groovy.db
/**
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class PanelQueries {
	final static String PANEL_SUMMARY = "SELECT PANEL_ID as panelId, NAME as name, DESCRIPTION as description, display_name as owner FROM (SELECT * FROM PANEL P INNER JOIN ROLE R on P.OWNER_ROLE_ID=R.ROLE_ID WHERE P.NAME=? ORDER BY P.PANEL_ID DESC, P.DATE_CREATED ) WHERE ROWNUM = 1";
	final static String PANEL_SUMMARY_BYID = "SELECT P.PANEL_ID AS PLID, P.NAME AS Name, P.DESCRIPTION AS Description, R.DISPLAY_NAME as Owner, to_char(P.DATE_CREATED, 'MM/DD/YYYY') AS DateCreated, to_char(P.LAST_UPDATED, 'MM/DD/YYYY') AS LastUpdated, P.MODIFIED_BY AS ModifiedBy FROM PANEL P, ROLE R WHERE P.OWNER_ROLE_ID = R.ROLE_ID AND P.PANEL_ID = ?";
	final static String PANEL_ASSAYS = "SELECT ASSAY_ID as ADID FROM PANEL_ASSAY WHERE PANEL_ID = ?";
}
