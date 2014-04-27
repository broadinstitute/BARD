package barddataqa

class FindAidInProdService {

    public static final String[] headerArray = ["PubChem AID", "CAP EID", "CAP ADID"]

    static final String queryString = """
select to_number(substr(ext_assay_ref,5)) pubchem_aid, er.experiment_id, e.assay_id from BARD_CAP_PROD.external_reference er
join bard_cap_prod.experiment e on e.experiment_id = er.experiment_id
where ext_assay_ref like 'aid=%'
order by pubchem_aid
"""

    def sessionFactory

    List<Object[]> findAllAidsForExperimentsInProduction() {
        return sessionFactory.getCurrentSession().createSQLQuery(queryString).list()
    }

}
