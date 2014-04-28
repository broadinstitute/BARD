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
 * this class holds all the database queries to fetch data.
 * @author Muhammad.Rafique
 * Date Created: 13/11/25
 */
class ExperimentQueries {
	final static String EXPERIMENT_SUMMARY_BYID = "SELECT E.EXPERIMENT_ID AS EID, E.EXPERIMENT_STATUS AS Status, E.EXPERIMENT_NAME AS Name, E.DESCRIPTION AS Description, to_char(E.RUN_DATE_FROM, 'MM/DD/YYYY') as RunDateFrom, to_char(E.RUN_DATE_TO, 'MM/DD/YYYY') as RunDateTo, to_char(E.DATE_CREATED, 'MM/DD/YYYY') AS DateCreated, to_char(E.LAST_UPDATED, 'MM/DD/YYYY') AS LastUpdated, E.MODIFIED_BY AS ModifiedBy FROM EXPERIMENT E WHERE E.EXPERIMENT_ID = ?";
	final static String EXPERIMENT_CONTEXTS = "SELECT EC.CONTEXT_NAME ContextName FROM EXPRMT_CONTEXT EC WHERE EC.EXPERIMENT_ID = ? AND EC.CONTEXT_TYPE = ?"
	final static String EXPERIMENT_CONTEXT_ITEMS = "SELECT E.LABEL AttributeLabel, ECI.VALUE_DISPLAY ValueDisplay FROM EXPRMT_CONTEXT_ITEM ECI, ELEMENT E WHERE ECI.ATTRIBUTE_ID = E.ELEMENT_ID AND ECI.EXPRMT_CONTEXT_ID IN(SELECT EC.EXPRMT_CONTEXT_ID ContextID FROM EXPRMT_CONTEXT EC WHERE EC.EXPERIMENT_ID = ? AND EC.CONTEXT_TYPE = ? AND EC.CONTEXT_NAME = ?)";
	final static String EXPERIMENT_RESULTS = "SELECT E.LABEL ResultType FROM EXPRMT_MEASURE EM INNER JOIN ELEMENT E ON EM.RESULT_TYPE_ID = E.ELEMENT_ID WHERE EM.EXPERIMENT_ID = ?";
	final static String EXPERIMENT_DOCUMENT = "SELECT DOCUMENT_NAME as Name, DOCUMENT_CONTENT as Content FROM EXPERIMENT_DOCUMENT WHERE EXPERIMENT_ID = ? AND LOWER(DOCUMENT_TYPE) = ?";
	final static String EXPERIMENT_SUMMARY_CREATED = "SELECT EXPERIMENT_ID as EID, EXPERIMENT_NAME as Name, DESCRIPTION as Description, EXPERIMENT_STATUS as Status, ASSAY_ID as ADID, DISPLAY_NAME as Owner FROM (SELECT * FROM EXPERIMENT E INNER JOIN ROLE R on E.OWNER_ROLE_ID = R.ROLE_ID WHERE E.EXPERIMENT_NAME = ? ORDER BY E.EXPERIMENT_ID DESC, E.DATE_CREATED) WHERE ROWNUM = 1"
}
