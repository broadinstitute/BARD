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
 * Date Created: 13/02/07
 */
class ProjectQueries {
	final static String PROJECT_SUMMARY_BYID = "SELECT P.PROJECT_ID AS PID, P.PROJECT_STATUS AS Status, P.PROJECT_NAME AS Name, P.DESCRIPTION Description, to_char(P.DATE_CREATED, 'MM/DD/YYYY') AS DateCreated, to_char(P.LAST_UPDATED, 'MM/DD/YYYY') AS LastUpdated, P.MODIFIED_BY AS ModifiedBy FROM PROJECT P WHERE P.PROJECT_ID = ?";
	final static String PROJECT_SEARCH_COUNT = "SELECT COUNT(P.PROJECT_NAME) ProjectCount FROM PROJECT P WHERE LOWER(P.PROJECT_NAME) LIKE ?";
	final static String PROJECT_SEARCH_RSULTS = "SELECT P.PROJECT_ID PID FROM PROJECT P WHERE LOWER(P.PROJECT_NAME) LIKE ?";
	final static String PROJECT_CONTEXTS = "SELECT PC.CONTEXT_NAME ContextName FROM PROJECT_CONTEXT PC WHERE PC.PROJECT_ID = ? AND PC.CONTEXT_TYPE = ?"
	final static String PROJECT_CONTEXT_ITEMS = "SELECT E.LABEL AttributeLabel, PCI.VALUE_DISPLAY ValueDisplay FROM PROJECT_CONTEXT_ITEM PCI, ELEMENT E WHERE PCI.ATTRIBUTE_ID = E.ELEMENT_ID AND PCI.PROJECT_CONTEXT_ID IN(SELECT PC.PROJECT_CONTEXT_ID ContextID FROM PROJECT_CONTEXT PC WHERE PC.PROJECT_ID = ? AND PC.CONTEXT_TYPE = ? AND PC.CONTEXT_NAME = ?)";
	final static String PROJECT_DOCUMENT = "SELECT DOCUMENT_NAME as Name, DOCUMENT_CONTENT as Content FROM PROJECT_DOCUMENT WHERE PROJECT_ID = ? AND LOWER(DOCUMENT_TYPE) = ?";
	final static String PROJECT_SUMMARY_CREATED = "SELECT PROJECT_ID as PID, PROJECT_NAME as Name, DESCRIPTION as Description, GROUP_TYPE as PGroup, PROJECT_STATUS as Status,  DISPLAY_NAME as Owner FROM (SELECT * FROM PROJECT P INNER JOIN ROLE R on P.OWNER_ROLE_ID = R.ROLE_ID WHERE P.PROJECT_NAME = ? ORDER BY P.PROJECT_ID DESC, P.DATE_CREATED) WHERE ROWNUM = 1"
}
