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

package barddataqa

import org.hibernate.SQLQuery

class CheckForMissingAidsService {

    public static final String[] headerArray = ["Dataset ID", "AID"]

    private static final String baseQueryString = """
select vda.dataset_id, vda.aid from bard_data_qa_dashboard.vw_dataset_aid vda
  left join bard_data_qa_dashboard.aid_info ai on ai.aid = vda.aid
  left join bard_data_qa_dashboard.vw_data_mig_external_reference dm on dm.ext_assay_ref = ('aid=' || vda.aid)
  where ext_assay_ref is null
    and (ai.aid is null or ai.is_summary_aid = 'n')
"""

    private static final String queryString = """
$baseQueryString
order by vda.dataset_id, vda.aid
"""

    private static final String countQueryString = """
select count(*) from
($baseQueryString)
"""

    def sessionFactory

    List<Object[]> findAidsThatAreMissingFromDatasets() {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString)

        return query.list()
    }

    int countAidsThatAreMissingFromDatasets() {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(countQueryString)

        return (int)query.list().get(0)
    }
}
