package barddataqa

class ProjectsAssaysCentersService {

    public static final String[] headerArray = ["Project ID", "Assay Defintion ID (ADID)", "Center"]

    private static final String queryString = """
select pe.project_id, e.assay_id, pci.value_display from bard_cap_prod.project_experiment pe
  join bard_cap_prod.experiment e on e.experiment_id = pe.experiment_id
  left join bard_cap_prod.project_context pc on pc.project_id = pe.project_id
  left join bard_cap_prod.project_context_item pci on pci.project_context_id = pc.project_context_id and pci.attribute_id=559
  where pci.value_display is not null
  group by pe.project_id, e.assay_id, pci.value_display
  order by pe.project_id, e.assay_id
"""

    def sessionFactory

    List<Object> findProjectsAssaysAndCenters() {
        return sessionFactory.getCurrentSession().createSQLQuery(queryString).list()
    }
}
