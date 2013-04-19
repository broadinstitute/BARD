update assay_context_item set qualifier = '= ' where value_num is not null and qualifier is null;
