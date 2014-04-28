/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package db
/**
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class PanelQueries {
	final static String PANEL_SUMMARY = "SELECT PANEL_ID as panelId, NAME as name, DESCRIPTION as description, display_name as owner FROM (SELECT * FROM PANEL P INNER JOIN ROLE R on P.OWNER_ROLE_ID=R.ROLE_ID WHERE P.NAME=? ORDER BY P.PANEL_ID DESC, P.DATE_CREATED ) WHERE ROWNUM = 1";
	final static String PANEL_SUMMARY_BYID = "SELECT P.PANEL_ID AS PLID, P.NAME AS Name, P.DESCRIPTION AS Description, R.DISPLAY_NAME as Owner, to_char(P.DATE_CREATED, 'MM/DD/YYYY') AS DateCreated, to_char(P.LAST_UPDATED, 'MM/DD/YYYY') AS LastUpdated, P.MODIFIED_BY AS ModifiedBy FROM PANEL P, ROLE R WHERE P.OWNER_ROLE_ID = R.ROLE_ID AND P.PANEL_ID = ?";
	final static String PANEL_ASSAYS = "SELECT ASSAY_ID as ADID FROM PANEL_ASSAY WHERE PANEL_ID = ?";
}
